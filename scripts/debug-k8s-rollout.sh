#!/usr/bin/env bash
set -euo pipefail
export PATH="$HOME/bin:$PATH"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CLUSTER="${1:-autoservice-debug}"

cleanup() {
  k3d cluster delete "$CLUSTER" 2>/dev/null || true
}
if [ "${KEEP_CLUSTER:-}" != "1" ]; then
  trap cleanup EXIT
fi

k3d cluster delete "$CLUSTER" 2>/dev/null || true
k3d cluster create "$CLUSTER" --wait --agents 1
export KUBECONFIG="$(k3d kubeconfig write "$CLUSTER")"

kubectl apply -f "$ROOT/k8s/00-namespace.yaml"
kubectl apply -f "$ROOT/k8s/20-configmap-postgres.yaml"
kubectl apply -f "$ROOT/k8s/21-initdb-postgres.yaml"
kubectl apply -f "$ROOT/k8s/22-pvc-postgres.yaml"
kubectl apply -f "$ROOT/k8s/23-deployment-postgres.yaml"
kubectl apply -f "$ROOT/k8s/24-service-postgres.yaml"
kubectl create secret generic autoservice-postgres-secret \
  --namespace=autoservice \
  --from-literal=POSTGRES_PASSWORD=postgres-regression \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl create secret generic autoservice-app-secret \
  --namespace=autoservice \
  --from-literal=AUTOSERVICE_JWT_SECRET=regression-jwt-secret-min-32-chars-long \
  --from-literal=MAIL_USERNAME=test@autoservice.local \
  --from-literal=MAIL_PASSWORD=test \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f "$ROOT/k8s/10-configmap-app.yaml"

docker compose -f "$ROOT/docker/docker-compose.yaml" build app --no-cache -q
docker tag docker-app:latest autoservice-app:regression
k3d image import autoservice-app:regression -c "$CLUSTER"

sed "s|ghcr.io/cristhian-ruescas/autoservice:latest|autoservice-app:regression|g; s|imagePullPolicy: Always|imagePullPolicy: IfNotPresent|g" \
  "$ROOT/k8s/30-deployment-app.yaml" | kubectl apply -f -
kubectl apply -f "$ROOT/k8s/31-service-app.yaml"

kubectl rollout status deployment/autoservice-postgres -n autoservice --timeout=180s
echo "Waiting 90s for app..."
sleep 90
kubectl -n autoservice get pods -o wide
kubectl -n autoservice describe pod -l app=autoservice-app | tail -50
kubectl -n autoservice logs -l app=autoservice-app --tail=120 --all-containers=true || true
