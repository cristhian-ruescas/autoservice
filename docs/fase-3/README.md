# Fase 3 — módulo da aplicação (Repo 4)

Este diretório documenta o **escopo do repositório principal** após a segregação SDD §14.

## O que este repo entrega

| Peça | Onde |
|------|------|
| API Spring Boot + JWT validation (`iss=autoservice-auth`) | `src/` |
| Instrumentação | Actuator, Micrometer/Prometheus, logs JSON, `X-Correlation-Id` |
| Container da API | `docker/Dockerfile` + Compose local (Postgres + Mailpit) |
| Deploy K8s da app + IngressRoute | `k8s/`, `k8s/gateway/` |
| CI/CD app | `.github/workflows/ci-cd.yml` |

## O que NÃO vive mais neste repo

| Peça | Repositório responsável |
|------|-------------------------|
| Function serverless `POST /auth/cpf` | `autoservice-lambda-auth` (ou Worker Cloudflare) |
| Terraform k3d + Traefik + metrics-server + Grafana stack | `autoservice-infra-k8s` |
| Terraform banco gerenciado (Neon/RDS) | `autoservice-infra-db` |
| Postgres in-cluster / PVC | legado F2 — prod F3 usa DB gerenciado |
| Dashboards Grafana JSON | `autoservice-infra-k8s` |

## Contrato JWT (cliente)

| Campo | Valor |
|-------|-------|
| Algoritmo | HS256 |
| `iss` | `autoservice-auth` |
| Claims | `sub` (clienteId), `cpf`, `iat`, `exp` |
| Header | `Authorization: Bearer …` |
| Resposta auth externa | `{ "access_token", "token_type": "Bearer", "expires_in" }` |

Admin legado: `iss=autoservice-admin` via `POST /auth/login` (permanece neste repo).

## Fluxo local (só app)

```bash
cp local.variable.env.example local.variable.env
docker compose -f docker/docker-compose.yaml up --build
```

Obter JWT no serviço de auth externo; depois:

```bash
curl -s http://localhost:8088/ordens-servico \
  -H "Authorization: Bearer <access_token>"
```

## Modelo / ER

Ver [ER.md](./ER.md).

## Referências

- SDD master: [`sdd/sdd.md`](../../sdd/sdd.md)
- Decisões: [`sdd/sdd-decisoes.md`](../../sdd/sdd-decisoes.md)
