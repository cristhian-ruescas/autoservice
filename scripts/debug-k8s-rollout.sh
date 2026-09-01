#!/usr/bin/env bash
set -euo pipefail
export PATH="$HOME/bin:$PATH"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CLUSTER="${1:-autoservice-debug}"
DB_URL="${SPRING_DATASOURCE_URL:-jdbc:postgresql://host.k3d.internal:5432/autoservice}"
DB_USER="${SPRING_DATASOURCE_USERNAME:-postgres}"
DB_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-postgres}"

cleanup() {
  k3d cluster delete "$CLUSTER" 2>/dev/null || true
}
if [ "${KEEP_CLUSTER:-}" != "1" ]; then
  trap cleanup EXIT
fi

docker compose -f "$ROOT/docker/docker-compose.yaml" up -d postgres
docker compose -f "$ROOT/docker/docker-compose.yaml" exec -T postgres pg_isready -U postgres

k3d cluster delete "$CLUSTER" 2>/dev/null || true
k3d cluster create "$CLUSTER" --wait --agents 1
export KUBECONFIG="$(k3d kubeconfig write "$CLUSTER")"

kubectl apply -f "$ROOT/k8s/00-namespace.yaml"
kubectl create secret generic autoservice-app-secret \
  --namespace=autoservice \
  --from-literal=AUTOSERVICE_JWT_SECRET=regression-jwt-secret-min-32-chars-long \
  --from-literal=MAIL_USERNAME=test@autoservice.local \
  --from-literal=MAIL_PASSWORD=test \
  --from-literal=SPRING_DATASOURCE_URL="$DB_URL" \
  --from-literal=SPRING_DATASOURCE_USERNAME="$DB_USER" \
  --from-literal=SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD" \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f "$ROOT/k8s/10-configmap-app.yaml"

docker compose -f "$ROOT/docker/docker-compose.yaml" build app --no-cache -q
docker tag docker-app:latest autoservice-app:regression
k3d image import autoservice-app:regression -c "$CLUSTER"

sed "s|ghcr.io/cristhian-ruescas/autoservice:latest|autoservice-app:regression|g; s|imagePullPolicy: Always|imagePullPolicy: IfNotPresent|g" \
  "$ROOT/k8s/30-deployment-app.yaml" | kubectl apply -f -
kubectl apply -f "$ROOT/k8s/31-service-app.yaml"

echo "Waiting 90s for app..."
sleep 90
kubectl -n autoservice get pods -o wide
kubectl -n autoservice describe pod -l app=autoservice-app | tail -50
kubectl -n autoservice logs -l app=autoservice-app --tail=120 --all-containers=true || true
