# Autoservice — Repositório da aplicação (SDD Repo 4)

API da oficina mecânica em **Spring Boot 3** / **Java 21**, com persistência em **PostgreSQL**.  
Documentação interativa: **`/swagger-ui.html`** (OpenAPI em **`/v3/api-docs`**).

Este repositório é o **módulo principal da aplicação** (Fase 3 / SDD §14.4).  
Auth serverless, Terraform de cluster/Traefik e banco gerenciado vivem em **repositórios irmãos**.

## Escopo deste repositório

| Inclui | Não inclui (outros repos) |
|--------|---------------------------|
| Código Spring (domain / application / infrastructure / presentation) | Function auth CPF → JWT (`autoservice-lambda-auth` / Worker) |
| Dockerfile da API + Compose local (Postgres + Mailpit) | Terraform k3d / Traefik / Grafana (`autoservice-infra-k8s`) |
| Manifests K8s da **app** (Deployment, Service, HPA, ConfigMap, Secret) | Terraform banco gerenciado Neon/RDS (`autoservice-infra-db`) |
| IngressRoute Traefik da app (`k8s/gateway`) | Provisionamento do cluster / metrics-server |
| Validação JWT (`iss=autoservice-auth` e admin legado) | Dashboards Grafana / stack Prometheus-Loki |
| CI/CD: test → GHCR → deploy da app | Deploy do banco e da function de auth |

## Relação com os outros módulos (SDD)

```text
[Cliente / Postman]
       │
       ▼
[Traefik — repo infra-k8s]
   ├── /auth/cpf  ──► [Function serverless — repo auth]
   └── APIs + JWT ──► [autoservice-app + HPA — ESTE REPO]
                            │
                            ▼
                   [Postgres gerenciado — repo infra-db]
                            │
              [Prometheus / Grafana / Loki — repo infra-k8s]
```

Ordem de provisionamento: **infra-db** → **infra-k8s** → **auth** → **este repo (app)**.

## Arquitetura da aplicação

```text
presentation/     Controllers REST, DTOs, security (JWT), OpenAPI/Swagger
       |
application/      Casos de uso (comandos/queries) e orquestração de fluxos
       |
domain/           Entidades, regras de negócio, eventos e políticas de status da OS
       |
infrastructure/   JPA, e-mail, PDF (orçamento), listeners, métricas Micrometer
```

| Componente | Responsabilidade |
|------------|------------------|
| **Atendimento / abertura de OS** | Cliente, veículo, serviços/peças → id da OS |
| **Consulta de status** | Andamento e detalhe da OS |
| **Aprovação de orçamento** | API + links de e-mail |
| **Listagem de OS** | Prioridade operacional |
| **Catálogos / estoque / OC** | CRUD e eventos de domínio |
| **Autenticação** | Valida JWT; emite só admin legado (`POST /auth/login`). CPF → JWT é externo. |
| **Observabilidade (app)** | Actuator, Prometheus scrape, logs JSON, correlation id |

## Contrato JWT (consumido por este módulo)

| Campo | Valor |
|-------|-------|
| Algoritmo | HS256 (mesmo secret do serviço de auth) |
| Cliente | `iss=autoservice-auth`, claims `sub` (clienteId), `cpf`, `iat`, `exp` |
| Admin legado | `iss=autoservice-admin` via `POST /auth/login` |
| Header | `Authorization: Bearer <token>` |

## Instruções

### Execução local

**Pré-requisitos:** JDK 21, Maven (ou `./mvnw`), Docker (opcional).

| Variável | Exemplo | Descrição |
|----------|---------|-----------|
| `server.port` | `8088` | Porta HTTP |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/autoservice` | JDBC |
| `SPRING_DATASOURCE_USERNAME` / `PASSWORD` | `postgres` | Credenciais |
| `APP_BASE_URL` | `http://localhost:8088` | Links de orçamento |
| `AUTOSERVICE_JWT_SECRET` | (secreto) | Deve ser o **mesmo** do serviço auth externo |
| `MAIL_*` | Mailpit local | SMTP demo |

```bash
cp local.variable.env.example local.variable.env
./mvnw clean verify
./mvnw spring-boot:run
```

**Docker (Postgres + API + Mailpit)** — na raiz:

```bash
docker compose -f docker/docker-compose.yaml up --build
```

- **Postgres:** `5432` · **API:** `8088` · **Mailpit UI:** http://localhost:8025
- **Swagger:** http://localhost:8088/swagger-ui.html
- **Prometheus:** http://localhost:8088/actuator/prometheus

Auth CPF (`POST /auth/cpf`) deve ser chamado no repositório/serviço de autenticação (não faz parte deste Compose).

### Deploy em Kubernetes

Pré-requisitos: cluster + Traefik (`autoservice-infra-k8s`) e banco gerenciado (`autoservice-infra-db`).

Manifestos em **`/k8s`**:

- App: Namespace, ConfigMap, Secret (exemplo), Deployment, Service, HPA
- Gateway: IngressRoute + middleware (`kubectl apply -k k8s/gateway`) — requer CRDs Traefik

```bash
cp k8s/11-secret-app.example.yaml k8s/11-secret-app.yaml
# edite JWT (igual ao auth), senha JDBC (infra-db) e mail
kubectl apply -f k8s/11-secret-app.yaml

# Ajuste SPRING_DATASOURCE_URL no ConfigMap com o host do banco gerenciado
kubectl apply -k k8s
kubectl apply -k k8s/gateway
```

Imagem padrão: `ghcr.io/cristhian-ruescas/autoservice:latest`

```bash
kubectl -n autoservice get pods,svc,hpa
kubectl -n autoservice rollout status deployment/autoservice-app
```

Grafana / Traefik / Neon: ver READMEs dos repositórios `autoservice-infra-k8s` e `autoservice-infra-db`.

## Collection das APIs

| Canal | Link |
|-------|------|
| **Postman** | [`Autoservice API.postman_collection.json`](./Autoservice%20API.postman_collection.json) |
| **Swagger UI** | http://localhost:8088/swagger-ui.html |
| **OpenAPI** | [`openapi.json`](./openapi.json) · `/v3/api-docs` |

| Área | Base path |
|------|-----------|
| Autenticação admin | `/auth/login` |
| Atendimento | `/atendimentos` |
| Ordens de serviço | `/ordens-servico` |
| Serviços / peças / estoque / OC | `/servicos`, `/pecas`, `/estoques`, `/ordens-compra` |

## CI/CD

Pipeline **`.github/workflows/ci-cd.yml`**:

1. Build + testes (Postgres de serviço no Actions)
2. Validação kustomize (`k8s` + `k8s/gateway`)
3. Build/push imagem GHCR (`main`/`master`)
4. Deploy **somente da app** no Kubernetes

Secrets de deploy: `AUTOSERVICE_JWT_SECRET`, `SPRING_DATASOURCE_PASSWORD`, `MAIL_USERNAME`, `MAIL_PASSWORD`.

Banco e cluster **não** são provisionados por este pipeline.

## Testes e cobertura

- Unitários e integração (Testcontainers quando Docker disponível).
- JaCoCo focado em domínio e casos de uso.

## Qualidade (SonarQube opcional)

```bash
docker compose -f docker/docker-compose.yaml --profile quality up -d sonarqube
export SONAR_TOKEN=<token>
./mvnw clean verify sonar:sonar
```

## Por que PostgreSQL?

Open-source, ACID, FKs e tipos adequados a valores monetários; maduro em containers e cloud gerenciada (consumido via `autoservice-infra-db` na F3).

## Documentação SDD

- [`sdd/sdd.md`](./sdd/sdd.md) — especificação canônica
- [`sdd/sdd-decisoes.md`](./sdd/sdd-decisoes.md) — ADRs/RFCs
- [`docs/fase-3/README.md`](./docs/fase-3/README.md) — escopo do módulo app na F3

## Licença / uso acadêmico

Projeto do **Tech Challenge** (pós-graduação SOAT) — repositório 4 de 4.
