#!/usr/bin/env bash
# Gera o valor base64 do secret KUBE_CONFIG para GitHub Actions.
# Uso: ./scripts/export-kubeconfig-secret.sh [caminho-do-kubeconfig]

set -euo pipefail

KUBECONFIG_PATH="${1:-${KUBECONFIG:-$HOME/.kube/config}}"

if [ ! -f "$KUBECONFIG_PATH" ]; then
  echo "Arquivo kubeconfig não encontrado: $KUBECONFIG_PATH" >&2
  exit 1
fi

echo "Adicione o valor abaixo como secret KUBE_CONFIG no GitHub (Settings > Secrets):"
echo
base64 -w 0 "$KUBECONFIG_PATH" 2>/dev/null || base64 "$KUBECONFIG_PATH" | tr -d '\n'
echo
