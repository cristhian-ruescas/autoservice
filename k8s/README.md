# Kubernetes Manifests — Autoservice App (Repo 4)

Manifests da **aplicação** no cluster. Provisionamento de cluster (k3d, Traefik, metrics-server) fica no repo `autoservice-infra-k8s`. Banco gerenciado (Neon) fica no repo `autoservice-infra-db`.

## Arquivos

| Arquivo | Descrição |
|---------|-----------|
| `00-namespace.yaml` | Namespace `autoservice` |
| `10-configmap-app.yaml` | Config não sensível (mail, base URL) |
| `11-secret-app.example.yaml` | Template do secret (JWT, mail, datasource Neon) |
| `30-deployment-app.yaml` | Deployment da API Spring Boot |
| `31-service-app.yaml` | Service ClusterIP (porta 8088) |
| `32-hpa-app.yaml` | HorizontalPodAutoscaler (CPU/memória) |
| `kustomization.yaml` | Kustomize para validação e apply |

## Pré-requisitos

- Cluster Kubernetes com **metrics-server** (repo `autoservice-infra-k8s`)
- Credenciais do banco gerenciado (repo `autoservice-infra-db`)
- Imagem publicada no GHCR (pipeline CI/CD)

## Deploy

```bash
cp k8s/11-secret-app.example.yaml k8s/11-secret-app.yaml
# Edite com valores reais (Neon, JWT, SMTP) — não commitar

kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/11-secret-app.yaml
kubectl apply -k k8s
```

Validação sem aplicar:

```bash
kubectl apply --dry-run=client -k k8s
kubectl kustomize k8s
```

## Verificação

```bash
kubectl -n autoservice get pods,svc,hpa
kubectl -n autoservice rollout status deployment/autoservice-app
kubectl -n autoservice logs -l app=autoservice-app --tail=50
```

## HPA

Scripts de teste: `scripts/validate-hpa.sh`, `scripts/test-hpa-load.sh`.

Documentação detalhada: `docs/HPA-CONFIGURATION.md`.

## Troubleshooting

**HPA com métricas "Unknown"** — confirme que o metrics-server está instalado no cluster.

**App não sobe** — verifique o secret `autoservice-app-secret` (URL/username/password do Neon).

**Banco inacessível** — confirme que o Neon permite conexão externa e que `sslmode=require` está na URL.
