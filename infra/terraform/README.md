# Infraestrutura como Código — Terraform

Provisiona, de forma reproduzível, toda a infraestrutura necessária para executar
o Autoservice em Kubernetes:

- **Cluster Kubernetes** (via [`kind`](https://kind.sigs.k8s.io/) — reproduzível
  localmente, sem custo e sem credenciais de nuvem).
- **Banco de dados PostgreSQL** (StatefulSet + Service + PVC).
- **Recursos da aplicação**: ConfigMap, Secret, Deployment, Service e HPA.

> Para nuvem (EKS/GKE/AKS), substitua o recurso `kind_cluster` pelo módulo do
> provedor desejado e reaproveite os demais recursos `kubernetes_*`.

## Pré-requisitos

- [Terraform](https://developer.hashicorp.com/terraform/downloads) >= 1.5
- [Docker](https://docs.docker.com/get-docker/) (backend do `kind`)
- [kind](https://kind.sigs.k8s.io/) e [kubectl](https://kubernetes.io/docs/tasks/tools/)

## Uso

```bash
cd infra/terraform

# 1. Copie e ajuste as variáveis
cp terraform.tfvars.example terraform.tfvars

# 2. Inicialize os provedores
terraform init

# 3. Verifique o plano
terraform plan

# 4. Provisione tudo
terraform apply

# 5. Acesse a aplicação
kubectl -n autoservice port-forward svc/autoservice-app 8088:80
# Swagger: http://localhost:8088/swagger-ui.html
```

## Destruir

```bash
terraform destroy
```

## Variáveis principais

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `cluster_name` | `autoservice` | Nome do cluster kind |
| `namespace` | `autoservice` | Namespace da aplicação |
| `app_image` | `ghcr.io/owner/autoservice:latest` | Imagem publicada pela pipeline |
| `app_replicas` | `2` | Réplicas iniciais |
| `hpa_min_replicas` / `hpa_max_replicas` | `2` / `6` | Limites do autoscaler |
| `hpa_cpu_target` / `hpa_memory_target` | `70` / `80` | Metas de utilização (%) |
| `postgres_password` | `postgres` | Senha do banco (sensível) |
| `jwt_secret` | — | Segredo JWT (sensível) |

Valores sensíveis (`postgres_password`, `jwt_secret`) devem ser definidos via
`terraform.tfvars` (fora do versionamento) ou variáveis de ambiente
`TF_VAR_postgres_password` / `TF_VAR_jwt_secret`.
