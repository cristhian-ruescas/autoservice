# Autoservice — Tech Challenge (Fase 2)

API da oficina mecânica em **Spring Boot 3** / **Java 21**, com persistência em **PostgreSQL**.  
Documentação interativa: **`/swagger-ui.html`** (OpenAPI em **`/v3/api-docs`**).

## Descrição da solução e dos objetivos desta fase

### Contexto

Na **Fase 1** foi implantado o sistema inicial de gestão de ordens de serviço, veículos, clientes e controle de peças. Com o aumento da demanda, a perspectiva de novas unidades e a necessidade de **alta disponibilidade**, a oficina precisa evoluir a aplicação para:

- reduzir riscos operacionais com **infraestrutura escalável**;
- **automatizar** o provisionamento e o deploy do ambiente;
- melhorar **qualidade** e organização do código, com evolução sustentável;
- suportar **grandes volumes de OS em horários de pico**, com **escalabilidade dinâmica**.

### Solução (Fase 2)

Esta fase evolui a aplicação da Fase 1 com foco em **qualidade**, **resiliência** e **escalabilidade**, unindo:

1. **Código** — refatoração com Clean Code e arquitetura em camadas (hexagonal / Clean Architecture), testes automatizados e APIs do fluxo crítico da OS;
2. **Plataforma** — containerização (Docker / Compose), orquestração (Kubernetes + HPA), infraestrutura como código (Terraform) e pipeline CI/CD (GitHub Actions).

No domínio, a API cobre atendimento (abertura de OS), diagnóstico e orçamento, aprovação/reprovação (incluindo canal por e-mail), execução, estoque, ordens de compra, entrega e consultas/listagens de status.

### Objetivos desta fase

| Pilar | Objetivo |
|-------|----------|
| **Aplicação** | Refatorar mantendo Clean Code e separação de camadas; cobrir fluxos críticos com testes unitários e/ou de integração; evoluir as APIs de OS (abertura, status, aprovação, listagem priorizada, atualização via e-mail). |
| **Conteinerização** | Garantir build reprodutível com Dockerfile e ambiente local com docker-compose. |
| **Kubernetes** | Deploy com Deployments, Services, ConfigMaps, Secrets e **HPA** (CPU/memória) para escala sob pico. |
| **IaC** | Provisionar cluster (local/cloud) e banco com **Terraform**, documentando recursos e como aplicar. |
| **CI/CD** | Automatizar build, testes, imagem Docker, deploy do banco e aplicação dos manifestos no cluster. |

## Arquitetura proposta

### Componentes da aplicação

Monólito em camadas (dependências apontando para o domínio):

```text
presentation/     Controllers REST, DTOs, security (JWT), OpenAPI/Swagger
       |
application/      Casos de uso (comandos/queries) e orquestração de fluxos
       |
domain/           Entidades, regras de negócio, eventos e políticas de status da OS
       |
infrastructure/   JPA, e-mail, PDF (orçamento), listeners de eventos, integrações
```

| Componente | Responsabilidade |
|------------|------------------|
| **Atendimento / abertura de OS** | Recebe cliente, veículo, serviços e peças; retorna o identificador único da OS |
| **Consulta de status** | Situação atual: Recebida, Diagnóstico, Aguardando Aprovação, Execução, Finalizada, Entregue (`GET .../andamento` e detalhe da OS) |
| **Aprovação de orçamento** | Endpoints de aprovação/recusa (API e links externos/e-mail) |
| **Listagem de OS** | Ordenação por prioridade de status (Em Execução > Aguardando Aprovação > Diagnóstico > Recebida), mais antigas primeiro; exclui finalizadas/entregues da listagem operacional |
| **Catálogos** | Peças, tipos de veículo, serviços |
| **Estoque e ordem de compra** | Entrada/baixa alinhadas ao ciclo da OS |
| **Autenticação** | Login JWT para rotas administrativas |
| **Notificações** | E-mail com andamento / links de orçamento (atualização de status via ferramenta externa) |
| **Métricas** | Tempo médio de execução das OS |

### Infraestrutura provisionada

| Camada | Recursos |
|--------|----------|
| **Local (Compose)** | Postgres + API (`docker/`) — desenvolvimento |
| **Terraform (`/infra`)** | Cluster Kubernetes local (**K3d**), namespace `autoservice`, **PostgreSQL** (Helm) |
| **Kubernetes (`/k8s`)** | App: Deployment, Service, ConfigMap, Secret, **HPA** · Banco: Deployment, Service, ConfigMap, Secret, **PVC**, initdb |

```text
Cliente / Postman / Swagger / e-mail (links)
              |
              v
       Service (K8s)  ------->  autoservice-app  (Deployment + HPA CPU/memória)
                                       |
                                       v
                              autoservice-postgres  (Deployment + PVC)
```

O **HPA** escala as réplicas da API conforme consumo de CPU e memória, preparando o sistema para picos de ordens de serviço.

### Fluxo de deploy

```text
Push no GitHub (main / master / develop)
        |
        v
GitHub Actions (.github/workflows/ci-cd.yml)
        |-- build da aplicação
        |-- testes automatizados
        |-- validação dos manifestos (kustomize)
        |-- build + push da imagem Docker (GHCR)     [main/master]
        |-- deploy do banco + app no Kubernetes      [main/master]
        v
Cluster consome: ghcr.io/cristhian-ruescas/autoservice:latest
```

Secrets no ambiente GitHub `production`: `KUBECONFIG`, `POSTGRES_PASSWORD`, `AUTOSERVICE_JWT_SECRET`, `MAIL_USERNAME`, `MAIL_PASSWORD`.

---

## Instruções

### Execução local

**Pré-requisitos:** JDK 21, Maven (ou `./mvnw`), PostgreSQL acessível **ou** Docker.

**Variáveis principais** (`src/main/resources/application.yaml`):

| Variável / propriedade       | Exemplo                                        | Descrição                                       |
|------------------------------|------------------------------------------------|-------------------------------------------------|
| `server.port`                | `8088`                                         | Porta HTTP                                      |
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5432/autoservice` | JDBC                                            |
| `SPRING_DATASOURCE_USERNAME` | `postgres`                                     | Usuário                                         |
| `SPRING_DATASOURCE_PASSWORD` | `postgres`                                     | Senha                                           |
| `APP_BASE_URL`               | `http://localhost:8088`                        | Base URL dos links de orçamento                 |
| `MAIL_*`                     | opcional                                       | SMTP (defaults apontam para localhost)          |
| `AUTOSERVICE_JWT_SECRET`     | (secreto)                                      | Assinatura do JWT                               |

1. Copie `local.variable.env.example` para `local.variable.env` e ajuste JWT/mail (e demais valores necessários).
2. Crie o banco `autoservice` no Postgres **ou** use o Compose abaixo.

**Maven:**

```bash
./mvnw clean verify    # testes + relatório JaCoCo em target/site/jacoco
./mvnw spring-boot:run
```

**Docker (Postgres + aplicação)** — na raiz do repositório:

```bash
docker compose -f docker/docker-compose.yaml up --build
```

- **Postgres:** porta **5432**
- **API:** porta **8088**
- **Swagger:** http://localhost:8088/swagger-ui.html

O `Dockerfile` está em **`docker/Dockerfile`** (build multi-stage com Maven + JRE 21).

### Deploy em Kubernetes

Manifestos em **`/k8s`** (aplicados via `kustomization.yaml`):

- App: Deployment, Service, ConfigMap, Secret (exemplo), HPA
- Postgres: Deployment, Service, ConfigMap, Secret (exemplo), PVC, initdb

**1. Secrets** (não versionar valores reais):

```bash
cp k8s/11-secret-app.example.yaml k8s/11-secret-app.yaml
cp k8s/21-secret-postgres.example.yaml k8s/21-secret-postgres.yaml
# edite os arquivos com valores reais
kubectl apply -f k8s/11-secret-app.yaml
kubectl apply -f k8s/21-secret-postgres.yaml
```

Ou use `kubectl create secret generic` (como no pipeline CI/CD).

**2. Imagem:** ajuste `k8s/30-deployment-app.yaml` se usar outro registry/tag. Padrão:

`ghcr.io/cristhian-ruescas/autoservice:latest`

**3. Validar e aplicar:**

```bash
kubectl apply --dry-run=client -k k8s
kubectl apply -k k8s
```

**4. Verificar:**

```bash
kubectl -n autoservice get pods
kubectl -n autoservice get svc
kubectl -n autoservice get hpa
kubectl -n autoservice rollout status deployment/autoservice-postgres
kubectl -n autoservice rollout status deployment/autoservice-app
```

Pré-requisito: cluster acessível via `kubectl` (pode ser o provisionado com Terraform/K3d abaixo, ou outro cluster).

### Provisionamento da infraestrutura com Terraform

Os scripts estão em **`/infra`**: cluster Kubernetes local (**K3d**), namespace e **PostgreSQL** via Helm Bitnami.

**Pré-requisitos:** Terraform 1.3+, [k3d](https://k3d.io/) no `PATH`, `kubectl` (recomendado).

```bash
cd infra
cp terraform.tfvars.example terraform.tfvars   # ajuste senhas/nomes
terraform init
terraform plan
terraform apply
```

**Recursos criados:**

- Cluster K3d (`autoservice-local` por padrão)
- Namespace `autoservice`
- PostgreSQL (Helm)
- ConfigMaps/Secrets auxiliares conforme o módulo

Validação:

```bash
kubectl cluster-info
kubectl get pods -n autoservice
kubectl get services -n autoservice
```

Detalhes e customização: **[infra/README.md](./infra/README.md)**.  
Para destruir: `terraform destroy` (dentro de `infra/`).

Após o cluster existir, o deploy da API segue a seção **Deploy em Kubernetes** (ou o CI/CD em `main`/`master`).

---

## Collection completa das APIs

| Canal | Link |
|-------|------|
| **Postman (collection completa)** | [`Autoservice API.postman_collection.json`](./Autoservice%20API.postman_collection.json) |
| **Swagger UI** (app em execução) | http://localhost:8088/swagger-ui.html |
| **OpenAPI JSON** (gerado / snapshot) | [`openapi.json`](./openapi.json) · runtime: `/v3/api-docs` |

Importe o arquivo Postman no cliente Postman (Import → arquivo) ou use o Swagger após subir a API.

### Principais bases path

| Área                               | Base path                                       |
|------------------------------------|-------------------------------------------------|
| Autenticação                       | `/auth`                                         |
| Atendimento / abertura de OS       | `/atendimentos`                                 |
| Ordens de serviço                  | `/ordens-servico`                               |
| Métricas (tempo médio de execução) | `/ordens-servico/metricas/tempo-medio-execucao` |
| Catálogo de serviços               | `/servicos`                                     |
| Peças                              | `/pecas`                                        |
| Estoque                            | `/estoques`                                     |
| Ordens de compra                   | `/ordens-compra`                                |

---

## CI/CD (GitHub Actions)

Pipeline em **`.github/workflows/ci-cd.yml`**:

- build da aplicação e testes;
- validação dos manifestos Kubernetes (`kubectl kustomize`);
- build/push da imagem Docker no GHCR (push em `main`/`master`);
- deploy do banco e da aplicação no Kubernetes (push em `main`/`master`).

Dispara em `push`/`pull_request` para `main`, `master` e `develop` (deploy completo apenas em `main`/`master`).

## Checklist de entrega — Go/No-Go (Fase 2)

### Go (concluído no repositório)

- [x] Refatoração em camadas (DDD/hexagonal) e código atualizado;
- [x] Testes automatizados unitários e de integração;
- [x] Dockerfile e docker-compose;
- [x] Manifestos Kubernetes em `/k8s` (Deployment/Service/ConfigMap/Secret/HPA/PVC);
- [x] Pipeline CI/CD em `.github/workflows/ci-cd.yml`;
- [x] Scripts Terraform em `/infra` e documentação de provisionamento;
- [x] Link da collection completa de APIs (Postman/Swagger — [`Autoservice API.postman_collection.json`](./Autoservice%20API.postman_collection.json)).

### No-Go / entrega acadêmica

- [x] Vídeo de demonstração;
- [x] PDF final com link do repositório, arquitetura e vídeo;
- [x] Validar execução completa do CI/CD em ambiente real com cluster ativo (evidência no vídeo de demonstração — esteira passando na `main`).

## Testes e cobertura

- **Unitários** e **integração** (Testcontainers + Postgres quando o Docker está disponível).
- Testes de integração com `@EnabledIf` ignoram o ambiente sem Docker.
- **JaCoCo** (`pom.xml`): relatório na fase `test`; regra de cobertura aplicada ao bundle configurado (exclui, entre outros, `presentation` e `infrastructure` do relatório de verificação — alinhado ao foco em **domínio** e **casos de uso**).

## Qualidade e vulnerabilidades com SonarQube

O projeto possui SonarScanner for Maven no `pom.xml` e SonarQube local no Docker Compose (profile `quality`).

```bash
docker compose -f docker/docker-compose.yaml --profile quality up -d sonarqube
```

Acesse `http://localhost:9000` (padrão: usuário `admin` / senha `admin`).

Crie um token em **My Account > Security**:

<img src="readme.assets/criando-sonar-token.png" width="400">

```bash
export SONAR_TOKEN=<token-gerado-no-sonarqube>
./mvnw clean verify sonar:sonar
```

Chave do projeto: `com.autoservice:autoservice`.

Para o relatório de vulnerabilidades da entrega, use um **User Token** (tokens só de análise podem não consultar Security Hotspots):

```bash
export SONAR_TOKEN=<user-token-gerado-no-sonarqube>
./scripts/generate-sonar-security-report.sh
```

- Relatório: [docs/security/sonar-vulnerability-report.md](docs/security/sonar-vulnerability-report.md)
- Script: [scripts/generate-sonar-security-report.sh](scripts/generate-sonar-security-report.sh)
- Respostas brutas da API: `target/sonar-security/`

## Decisões de modelagem do MVP

### Criação de cliente e veículo no fluxo de atendimento

No MVP, a criação de cliente e veículo foi centralizada em `POST /atendimentos`, que já abre a OS inicial no status `RECEBIDO`. Essa decisão evita cadastros órfãos e mantém o primeiro registro do atendimento de forma transacional.

Os endpoints administrativos de clientes e veículos cobrem listagem, consulta, atualização e remoção (`GET`, `PUT`, `DELETE`); o `create` ocorre no fluxo principal de negócio.

### Controle de estoque orientado a eventos de negócio

O controle de estoque é atualizado automaticamente por eventos do domínio:

- entrada ao realizar ordem de compra;
- baixa ao finalizar execução da OS.

Assim, o saldo permanece consistente com o ciclo operacional da oficina.

## Por que PostgreSQL?

**PostgreSQL** foi adotado por ser open-source, amplamente usado em produção, com forte suporte a integridade referencial, transações ACID e tipos adequados a valores monetários e ao modelo relacional (clientes, veículos, OS, itens, estoque). Integra-se bem com **Spring Data JPA** e ambientes containerizados.

## Licença / uso acadêmico

Projeto desenvolvido no contexto do **Tech Challenge** (pós-graduação SOAT).
