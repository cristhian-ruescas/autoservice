# Kubernetes — Autoservice

Manifestos **canônicos** (mesmos usados pelo CI e pelo Terraform em `/infra`).

## Arquivos

| Arquivo | Função |
|---------|--------|
| `00-namespace.yaml` | Namespace `autoservice` |
| `10-configmap-app.yaml` | ConfigMap da API |
| `11-secret-app.example.yaml` | Exemplo de Secret da API (não commitar valores reais) |
| `20-configmap-postgres.yaml` | ConfigMap do Postgres |
| `21-initdb-postgres.yaml` | Scripts de init (schemas) |
| `21-secret-postgres.example.yaml` | Exemplo de Secret do Postgres |
| `22-pvc-postgres.yaml` | PVC do banco |
| `23-deployment-postgres.yaml` | Deployment Postgres |
| `24-service-postgres.yaml` | Service Postgres |
| `30-deployment-app.yaml` | Deployment da API (+ HPA target) |
| `31-service-app.yaml` | Service da API |
| `32-hpa-app.yaml` | HPA (CPU 70% / memória 75%) |
| `kustomization.yaml` | Agrupa os manifests acima (sem Secrets) |

Secrets **não** entram no kustomize: são criados pelo CI, pelo Terraform ou manualmente a partir dos `*.example.yaml`.

## Fluxos suportados

### 1. Terraform (cluster + DB + opcionalmente app)

```bash
cd infra/
cp terraform.tfvars.example terraform.tfvars   # edite secrets
terraform init
terraform apply
```

Cria k3d, namespace, secrets (`autoservice-postgres-secret`, `autoservice-app-secret`), Postgres (manifests numerados), metrics-server e, se `deploy_app=true`, a app (mesmos YAMLs do CI).

### 2. CI/CD (GitHub Actions)

Em `main`/`master`: build → push GHCR → cria secrets → `kubectl apply` dos mesmos arquivos numerados.

### 3. Manual com kubectl / kustomize

```bash
cp k8s/11-secret-app.example.yaml k8s/11-secret-app.yaml
cp k8s/21-secret-postgres.example.yaml k8s/21-secret-postgres.yaml
# edite os arquivos com valores reais (gitignored)
kubectl apply -f k8s/11-secret-app.yaml
kubectl apply -f k8s/21-secret-postgres.yaml

# opcional: pull secret GHCR (necessário se a imagem for privada)
# kubectl create secret docker-registry ghcr-pull-secret ...

kubectl apply -k k8s
```

## HPA

- **Min / max:** 2 / 10 réplicas  
- **CPU:** 70% · **Memória:** 75%  
- Requer **metrics-server** (instalado pelo Terraform; no CI assume-se cluster com metrics-server)

```bash
kubectl -n autoservice get hpa autoservice-app-hpa
kubectl -n autoservice describe hpa autoservice-app-hpa
kubectl top pods -n autoservice
```

Detalhes: [docs/HPA-CONFIGURATION.md](../docs/HPA-CONFIGURATION.md).

## Validação

```bash
kubectl -n autoservice get pods,svc,hpa
kubectl -n autoservice rollout status deployment/autoservice-postgres
kubectl -n autoservice rollout status deployment/autoservice-app
```

## Troubleshooting

| Sintoma | Ação |
|---------|------|
| HPA metrics `Unknown` | `kubectl get deploy metrics-server -n kube-system` |
| ImagePullBackOff | Crie `ghcr-pull-secret` ou ajuste a imagem em `30-deployment-app.yaml` |
| App sem DB | Confira secret `autoservice-postgres-secret` e Service `autoservice-postgres` |
