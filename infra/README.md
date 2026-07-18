# Infraestrutura (Terraform) — Fase 2

Provisiona o mesmo stack que o CI usa em Kubernetes:

| Recurso | Origem |
|---------|--------|
| Cluster local | **k3d** (`null_resource`) |
| Namespace | `autoservice` |
| Secrets | `autoservice-postgres-secret`, `autoservice-app-secret` (+ `ghcr-pull-secret` opcional) |
| Postgres | Manifests `k8s/20`–`24` (iguais ao CI) |
| Metrics Server | Helm (necessário para HPA) |
| App (opcional) | Manifests `k8s/10`, `30`–`32` (iguais ao CI / kustomize) |

## Pré-requisitos

- Terraform **1.3+**
- [k3d](https://k3d.io/) no `PATH`
- `kubectl` (recomendado)
- Docker Desktop (ou runtime usado pelo k3d)

## Uso

```bash
cd infra
cp terraform.tfvars.example terraform.tfvars
# edite senhas / JWT / (opcional) GHCR
terraform init
terraform plan
terraform apply
```

Se o cluster `autoservice-local` **já existir**, o `apply` falha na criação do k3d. Opções:

```bash
k3d cluster delete autoservice-local
# ou reutilize o cluster e aplique só os manifests:
kubectl apply -k ../k8s
```

### Variáveis principais

| Variável | Descrição |
|----------|-----------|
| `postgres_password` | Chave `POSTGRES_PASSWORD` (usuário/DB = `postgres` / `autoservice` nos ConfigMaps) |
| `jwt_secret` / `mail_*` | Secret da app (mesmos nomes do CI) |
| `deploy_app` | `true` (padrão) aplica também Deployment/Service/HPA da API |
| `ghcr_username` / `ghcr_token` | Se preenchidos, cria `ghcr-pull-secret` e mantém `imagePullSecrets` no Deployment |

`terraform.tfvars` é gitignored — não commite segredos.

## Validação

```bash
kubectl cluster-info
kubectl -n autoservice get pods,svc,hpa
kubectl -n autoservice get secret autoservice-postgres-secret autoservice-app-secret
```

## Destruir

```bash
terraform destroy
```

Remove o cluster k3d e os recursos rastreados pelo state.

## Relação com o CI

O pipeline (`.github/workflows/ci-cd.yml`) **não** roda Terraform no deploy: ele assume cluster acessível no runner self-hosted e aplica os **mesmos** YAMLs numerados + secrets com os **mesmos** nomes. Este módulo serve para bootstrap local / documentação de IaC da Fase 2.
