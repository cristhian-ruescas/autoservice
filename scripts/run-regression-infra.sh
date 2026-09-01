#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export PATH="$HOME/bin:$PATH"
CLUSTER="autoservice-regression"
DB_URL="${SPRING_DATASOURCE_URL:-jdbc:postgresql://host.k3d.internal:5432/autoservice}"
DB_USER="${SPRING_DATASOURCE_USERNAME:-postgres}"
DB_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-postgres}"

cleanup() {
  echo "=== Cleanup ==="
  "$HOME/bin/k3d" cluster delete "$CLUSTER" 2>/dev/null || true
  docker compose -f "$ROOT/docker/docker-compose.yaml" stop app 2>/dev/null || true
}
trap cleanup EXIT

echo "=== 1. Kustomize validate ==="
kubectl kustomize "$ROOT/k8s" > /tmp/k8s-rendered.yaml
test -s /tmp/k8s-rendered.yaml
echo "Kustomize OK ($(wc -l < /tmp/k8s-rendered.yaml) lines)"

echo "=== 2. Start local Postgres (docker compose) ==="
docker compose -f "$ROOT/docker/docker-compose.yaml" up -d postgres
docker compose -f "$ROOT/docker/docker-compose.yaml" exec -T postgres pg_isready -U postgres

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

echo "=== 5. Build and import app image ==="
docker compose -f "$ROOT/docker/docker-compose.yaml" build app --no-cache
docker tag docker-app:latest autoservice-app:regression
"$HOME/bin/k3d" image import autoservice-app:regression -c "$CLUSTER"

sed "s|ghcr.io/cristhian-ruescas/autoservice:latest|autoservice-app:regression|g; s|imagePullPolicy: Always|imagePullPolicy: IfNotPresent|g; s|replicas: 2|replicas: 1|g" \
  "$ROOT/k8s/30-deployment-app.yaml" | kubectl apply -f -
kubectl apply -f "$ROOT/k8s/31-service-app.yaml"

kubectl rollout status deployment/autoservice-app -n autoservice --timeout=420s
kubectl -n autoservice get pods,svc

echo "=== 6. Port-forward smoke ==="
kubectl -n autoservice port-forward svc/autoservice-app 18088:8088 >/tmp/pf.log 2>&1 &
PF_PID=$!
sleep 8
APP_URL=http://localhost:18088 python3 "$ROOT/scripts/smoke-e2e.py" || { kill $PF_PID 2>/dev/null; exit 1; }
kill $PF_PID 2>/dev/null || true

echo "=== K8S REGRESSION OK ==="
