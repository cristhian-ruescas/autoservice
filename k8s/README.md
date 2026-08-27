# Kubernetes — manifests da aplicação (Repo 4)

Somente recursos da **API Autoservice**. Cluster, Traefik, Postgres gerenciado e auth CPF ficam nos repositórios irmãos.

## Base (`kubectl apply -k k8s`)

| Manifesto | Função |
|-----------|--------|
| `00-namespace.yaml` | Namespace `autoservice` |
| `10-configmap-app.yaml` | JDBC (host do infra-db), mail, `APP_BASE_URL` |
| `11-secret-app.example.yaml` | JWT + senha DB + SMTP (não commitar valores reais) |
| `30-deployment-app.yaml` | Deployment da API |
| `31-service-app.yaml` | Service |
| `32-hpa-app.yaml` | HPA (requer metrics-server do infra-k8s) |

## Gateway (`kubectl apply -k k8s/gateway`)

Aplicar **depois** do Traefik (repo `autoservice-infra-k8s`):

| Manifesto | Função |
|-----------|--------|
| `40-middleware-correlation.yaml` | Middleware Traefik |
| `40-ingressroute-app.yaml` | `PathPrefix(/)` → `autoservice-app:8088` |

Rota `/auth/cpf` **não** é definida aqui — pertence ao serviço serverless + IngressRoute/roteamento no infra-k8s ou API Gateway.

## Secrets esperados

- `autoservice-app-secret`: `AUTOSERVICE_JWT_SECRET`, `SPRING_DATASOURCE_PASSWORD`, `MAIL_*`
- `ghcr-pull-secret`: pull da imagem no GHCR (criado pelo CI)

## Dependências externas

1. Banco gerenciado configurado em `SPRING_DATASOURCE_URL` (outputs de `autoservice-infra-db`)
2. Cluster + Traefik + metrics-server (`autoservice-infra-k8s`)
3. Mesmo `AUTOSERVICE_JWT_SECRET` do emissor de JWT cliente (`iss=autoservice-auth`)
