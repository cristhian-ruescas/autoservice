#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export PATH="$HOME/bin:$PATH"
CLUSTER="autoservice-regression"

cleanup() {
  echo "=== Cleanup infra ==="
  "$HOME/bin/k3d" cluster delete "$CLUSTER" 2>/dev/null || true
  docker compose -f "$ROOT/docker/docker-compose.yaml" stop app 2>/dev/null || true
}
trap cleanup EXIT

echo "=== 1. Kustomize validate ==="
kubectl kustomize "$ROOT/k8s" > /tmp/k8s-rendered.yaml
test -s /tmp/k8s-rendered.yaml
echo "Kustomize OK ($(wc -l < /tmp/k8s-rendered.yaml) lines)"

echo "=== 2. Terraform validate (docker) ==="
docker run --rm -v "$ROOT:/workspace" -w /workspace/infra hashicorp/terraform:1.9 init -backend=false -input=false >/dev/null
docker run --rm -v "$ROOT:/workspace" -w /workspace/infra hashicorp/terraform:1.9 validate

echo "=== 3. k3d cluster ==="
if [ ! -x "$HOME/bin/k3d" ]; then
  curl -fsSL "https://github.com/k3d-io/k3d/releases/download/v5.9.0/k3d-linux-amd64" -o "$HOME/bin/k3d"
  chmod +x "$HOME/bin/k3d"
fi
if [ ! -x "$HOME/bin/kubectl" ]; then
  curl -fsSL "https://dl.k8s.io/release/v1.31.0/bin/linux/amd64/kubectl" -o "$HOME/bin/kubectl"
  chmod +x "$HOME/bin/kubectl"
fi

"$HOME/bin/k3d" cluster delete "$CLUSTER" 2>/dev/null || true
"$HOME/bin/k3d" cluster create "$CLUSTER" --wait --agents 1
export KUBECONFIG="$("$HOME/bin/k3d" kubeconfig write "$CLUSTER")"
kubectl cluster-info

echo "=== 4. Apply K8s manifests ==="
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

echo "=== 5. Build and import app image ==="
docker compose -f "$ROOT/docker/docker-compose.yaml" build app --no-cache
docker tag docker-app:latest autoservice-app:regression
"$HOME/bin/k3d" image import autoservice-app:regression -c "$CLUSTER"

sed "s|ghcr.io/cristhian-ruescas/autoservice:latest|autoservice-app:regression|g; s|imagePullPolicy: Always|imagePullPolicy: IfNotPresent|g; s|replicas: 2|replicas: 1|g" \
  "$ROOT/k8s/30-deployment-app.yaml" | kubectl apply -f -
kubectl apply -f "$ROOT/k8s/31-service-app.yaml"

kubectl rollout status deployment/autoservice-postgres -n autoservice --timeout=180s
kubectl rollout status deployment/autoservice-app -n autoservice --timeout=420s
kubectl -n autoservice get pods,svc

echo "=== 6. Port-forward smoke ==="
kubectl -n autoservice port-forward svc/autoservice-app 18088:8088 >/tmp/pf.log 2>&1 &
PF_PID=$!
sleep 8
APP_URL=http://localhost:18088 python3 "$ROOT/scripts/smoke-e2e.py" || { kill $PF_PID 2>/dev/null; exit 1; }
kill $PF_PID 2>/dev/null || true

echo "=== INFRA REGRESSION OK ==="
