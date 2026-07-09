#!/usr/bin/env bash
set -euo pipefail
export PATH="$HOME/bin:$PATH"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CLUSTER="autoservice-debug4"
export KUBECONFIG="$(k3d kubeconfig write "$CLUSTER" 2>/dev/null || true)"

if ! k3d cluster list | grep -q "$CLUSTER"; then
  echo "Cluster $CLUSTER not found"
  exit 1
fi

sed "s|ghcr.io/cristhian-ruescas/autoservice:latest|autoservice-app:regression|g; s|imagePullPolicy: Always|imagePullPolicy: IfNotPresent|g; s|replicas: 2|replicas: 1|g" \
  "$ROOT/k8s/30-deployment-app.yaml" | kubectl apply -f -
kubectl apply -f "$ROOT/k8s/31-service-app.yaml"

kubectl rollout status deployment/autoservice-app -n autoservice --timeout=600s || true
kubectl -n autoservice get pods
kubectl -n autoservice logs -l app=autoservice-app --tail=80 --all-containers=true 2>&1 | tail -80
