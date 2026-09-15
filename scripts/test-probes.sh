#!/usr/bin/env bash
set -euo pipefail
export PATH="$HOME/bin:$PATH"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CLUSTER="${1:-autoservice-probe-test}"

k3d cluster delete "$CLUSTER" 2>/dev/null || true
KEEP_CLUSTER=1 bash "$ROOT/scripts/debug-k8s-rollout.sh" "$CLUSTER" >/tmp/k8s-debug.log 2>&1 || true

export KUBECONFIG="$(k3d kubeconfig write "$CLUSTER")"
kubectl -n autoservice get pods
echo "--- probe events ---"
kubectl -n autoservice describe pod -l app=autoservice-app 2>&1 | grep -A2 -E 'Unhealthy|Ready|Warning' | tail -20

echo "--- health endpoints ---"
for path in /actuator/health /actuator/health/readiness /actuator/health/liveness; do
  echo "GET $path"
  kubectl -n autoservice exec deploy/autoservice-app -- wget -S -qO- "http://127.0.0.1:8088$path" 2>&1 | head -5 || true
  echo
done

k3d cluster delete "$CLUSTER" 2>/dev/null || true
