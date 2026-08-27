# Relatório — Refatoração do módulo principal (Fase 3)

**Branch:** `refactor/module-part3`  
**Repositório:** `autoservice` (SDD Repo 4 — Aplicação principal)  
**Base:** SDD §14 (quatro repositórios) e critérios de aceite Fase 3  

---

## Objetivo

Deixar neste repositório **apenas** o que o SDD atribui ao módulo da aplicação, removendo artefatos que já pertencem (ou devem pertencer) aos repositórios irmãos:

| Repo irmão | Responsabilidade SDD |
|------------|----------------------|
| `autoservice-lambda-auth` | Function serverless CPF → JWT |
| `autoservice-infra-k8s` | Terraform cluster + Traefik + metrics-server (+ Grafana) |
| `autoservice-infra-db` | Terraform banco gerenciado |

---

## O que foi removido

### 1. `auth-cpf/` (código Node + Dockerfile + testes)

- Serviço local `POST /auth/cpf` embutido no monorepo.
- **Motivo:** SDD §14.1 — autenticação serverless é repositório separado (`autoservice-lambda-auth` / Worker).
- Manifests K8s associados também removidos: `33/34/35-*-auth-cpf.yaml`, `41-ingressroute-auth.yaml`.
- Serviço `auth-cpf` removido do `docker/docker-compose.yaml`.
- Steps de teste, build de imagem e deploy do auth-cpf removidos do CI.

### 2. `infra/` (Terraform k3d + Traefik + Grafana/Loki + dashboards)

- Provisionamento de cluster, Helm Traefik, metrics-server e stack de observabilidade.
- **Motivo:** SDD §14.2 / ADR-008 — ownership do Gateway e do cluster é o repo `autoservice-infra-k8s`.

### 3. Manifests de Postgres in-cluster (`k8s/20`–`24` + secret example)

- Deployment, Service, PVC, ConfigMaps e initdb do Postgres no cluster.
- **Motivo:** SDD §14.3 / ADR-002 — em F3 o SoT é banco **gerenciado** (`autoservice-infra-db`); Postgres in-cluster era legado F2/local acoplado ao deploy da app.
- Job CI `deploy-db` removido.

### 4. Referências cruzadas na documentação

- README raiz, `k8s/README.md`, `docs/fase-3/*`, `docs/HPA-*` e `local.variable.env.example` atualizados para não apontarem para `infra/` nem `auth-cpf/` como parte deste módulo.

---

## O que permaneceu (escopo correto do Repo 4)

| Área | Conteúdo |
|------|----------|
| Código Spring | `domain` / `application` / `infrastructure` / `presentation` |
| Auth na app | `POST /auth/login` (admin) + validação JWT cliente (`iss=autoservice-auth`) |
| Observabilidade **na app** | Actuator, Micrometer/Prometheus, logs JSON, correlation filter, métricas de OS |
| Container | `docker/Dockerfile` + Compose **local** (Postgres + Mailpit + app) para desenvolvimento |
| K8s app | Namespace, ConfigMap, Secret example, Deployment, Service, HPA |
| Gateway app | `IngressRoute` + middleware (`k8s/gateway`) — CRDs Traefik vêm do infra-k8s |
| CI/CD | test → kustomize validate → GHCR → deploy **somente da app** |
| Docs API | Swagger / OpenAPI / Postman |
| SDD | `sdd/` permanece como especificação |

---

## Ajustes de integração feitos

1. **ConfigMap** (`10-configmap-app.yaml`): JDBC deixa de apontar para `autoservice-postgres` e usa placeholder de **DB gerenciado** (`sslmode=require`).
2. **Secret da app**: passa a carregar `SPRING_DATASOURCE_PASSWORD` (não mais `autoservice-postgres-secret`).
3. **Deployment**: remove dependência do secret do Postgres in-cluster; senha via `envFrom` do secret da app.
4. **Kustomize base**: apenas recursos da aplicação.
5. **Kustomize gateway**: apenas IngressRoute da app (sem rota `/auth/cpf`).
6. **CI**: remove testes/build/deploy de auth-cpf e job de deploy de banco; exige `SPRING_DATASOURCE_PASSWORD` nos secrets de deploy.

---

## Dependências externas (contrato operacional)

Para a app subir em homolog/prod após esta refatoração:

1. **`autoservice-infra-db`** — host/credenciais JDBC → ConfigMap/Secret  
2. **`autoservice-infra-k8s`** — cluster, Traefik (CRDs), metrics-server, (Grafana)  
3. **`autoservice-lambda-auth` (ou Worker)** — `POST /auth/cpf` com JWT alinhado (`access_token`, `sub=clienteId`, `iss=autoservice-auth`, mesmo secret)  
4. **Este repo** — valida JWT e serve as APIs de negócio atrás do Traefik  

---

## Riscos / próximos passos (fora deste PR de escopo)

- Alinhar contrato JWT da lambda (`token` vs `access_token`, `sub`) com o que o Spring já valida.
- Implementar de fato o conteúdo faltante em `autoservice-infra-k8s` (hoje scaffold) usando o que saiu de `infra/`.
- Preencher `SPRING_DATASOURCE_URL` real com outputs do `infra-db`.
- Configurar proteção de branch / PR obrigatório (SDD §6.2 / ADR-006) no GitHub — operação de repositório, não código.

---

## Critério SDD atendido por esta mudança

> §14.4 Repo 4 — Aplicação principal: código Spring + Dockerfile + manifests/Kustomize da app + IngressRoute + validação JWT + pipeline test→GHCR→deploy app + OpenAPI/Postman + README.

Itens de auth serverless, Terraform de cluster/DB e stack Grafana **deixam** de ser ownership deste módulo.
