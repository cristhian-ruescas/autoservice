# Arquitetura do Tech Challenge

## Visão Geral

O Tech Challenge é uma aplicação de oficina de manutenção automotiva, estruturada em 4 repositórios separados, cada um com CI/CD, infraestrutura como código e observabilidade.

## Diagrama de Componentes

```mermaid
graph LR
    Client["Client"]
    APIGW["AWS API Gateway"]
    Lambda["Lambda Auth<br/>CPF Validator"]
    K8s["EKS Cluster<br/>Autoservice App"]
    DB["AWS RDS<br/>PostgreSQL"]
    CloudWatch["CloudWatch<br/>Logs & Metrics"]
    Dashboard["Datadog/New Relic<br/>Dashboard"]

    Client -->|HTTP/REST| APIGW
    APIGW -->|POST /auth| Lambda
    Lambda -->|Query| DB
    Lambda -->|JWT Token| APIGW
    APIGW -->|Routed + JWT| K8s
    K8s -->|Read/Write| DB
    K8s -->|JSON Logs| CloudWatch
    CloudWatch -->|Ingest| Dashboard
    Lambda -->|Logs| CloudWatch
```

## Fluxo de Autenticação

```mermaid
sequenceDiagram
    participant Client
    participant Gateway as API Gateway
    participant Lambda as Lambda Auth
    participant DB as PostgreSQL
    participant App as Autoservice App

    Client->>Gateway: POST /auth {cpf: "123.456.789-00"}
    Gateway->>Lambda: Invoke with CPF
    Lambda->>DB: SELECT customer WHERE cpf = ?
    DB-->>Lambda: Customer found (status)
    Lambda-->>Gateway: JWT Token (HS256)
    Gateway-->>Client: 200 {token, type, expires_in}
    
    Client->>Gateway: GET /ordens-servico Header: Authorization: Bearer {token}
    Gateway->>App: Forward JWT via authorizer
    App->>DB: Execute business logic
    DB-->>App: Data
    App-->>Gateway: Response
    Gateway-->>Client: 200 {data}
```

## Fluxo de autenticação + abertura de ordem de serviço (detalhado)

```mermaid
sequenceDiagram
    participant Client
    participant Gateway as API Gateway
    participant Lambda as Lambda Auth
    participant App as Autoservice App
    participant DB as PostgreSQL

    Client->>Gateway: POST /auth (cpf)
    Gateway->>Lambda: Invoke auth function
    Lambda->>DB: Valida CPF + status do cliente
    DB-->>Lambda: Cliente valido/invalido
    Lambda-->>Gateway: JWT + claims (cpf, roles, issuer)
    Gateway-->>Client: Token de acesso

    Client->>Gateway: POST /atendimentos + Bearer JWT
    Gateway->>App: Encaminha request autenticada
    App->>App: JwtAuthenticationFilter + CpfAccessGuard
    App->>DB: Persistencia de cliente/veiculo/OS
    DB-->>App: Confirmacao
    App-->>Gateway: 201 Created
    Gateway-->>Client: Atendimento aberto
```

## Componentes Principais

### 1. `autoservice-lambda-auth`
- **Responsabilidade**: Autenticação por CPF, geração de JWT
- **Tecnologia**: Java 21, AWS Lambda, RDS
- **Integração**: API Gateway via Authorizer
- **Deploy**: GitHub Actions com empacotamento Maven e `aws lambda update-function-*`

### 2. `autoservice-infra-db`
- **Responsabilidade**: RDS PostgreSQL gerenciado, backup, replicação
- **Tecnologia**: AWS RDS, Terraform
- **Ambientes**: homolog e prod
- **Deploy**: Terraform com backend S3 e lock DynamoDB

### 3. `autoservice-infra-k8s`
- **Responsabilidade**: Cluster EKS, VPC, networking, observabilidade
- **Tecnologia**: AWS EKS, Terraform, Kubernetes
- **Ambientes**: homolog e prod
- **Deploy**: Terraform com GitHub Actions

### 4. `autoservice`
- **Responsabilidade**: Aplicação principal (Spring Boot), lógica de negócio
- **Tecnologia**: Java 21, Spring Boot 3.5, Docker
- **Deploy**: Docker → ECR → EKS via GitHub Actions
- **Observabilidade**: JSON logs, X-Correlation-Id, health probes

## Fluxo de Deploy

```mermaid
graph TB
    PR["Pull Request"]
    Validate["Validate<br/>fmt, tests"]
    Merge["Merge to main"]
    HomologBranch["push to homolog"]
    ProdBranch["push to prod"]
    HomologDeploy["Deploy Homolog"]
    ProdDeploy["Deploy Prod"]

    PR -->|CI| Validate
    Validate -->|✓| Merge
    Merge -->|create branch| HomologBranch
    Merge -->|create branch| ProdBranch
    HomologBranch -->|GitHub Actions| HomologDeploy
    ProdBranch -->|GitHub Actions| ProdDeploy
```

## Estratégia de Segurança

- **Autenticação**: CPF + JWT via Lambda Authorizer
- **Autorização**: Spring Security com roles (ADMIN, USER)
- **Networking**: Cluster EKS em subnets privadas, apenas NAT Gateway para egress
- **Banco**: RDS em subnets privadas, acesso via security group
- **Secrets**: GitHub Secrets para credenciais, AWS Secrets Manager para runtime

## Observabilidade

### Logs
- Formato: JSON estruturado (Logback + Logstash encoder)
- Correlação: header `X-Correlation-Id` propagado em todas as requisições
- Retenção: 30 dias (homolog), 90 dias (prod)

### Métricas
- CloudWatch Metrics via Spring Boot Actuator
- HPA basado em CPU (70%) e memória (80%)
- Dashboard consolidado em Datadog/New Relic

Metricas de negocio expostas pela aplicacao:
- `autoservice.service_orders.opened.total`
- `autoservice.service_orders.status_transitions.total`
- `autoservice.notifications.webhook.result`
- `autoservice.notifications.webhook.latency`

### Alertas
- Falha de health check
- CPU > 80% ou memória > 90%
- Erro nas integrações
- Taxa de erro da API > 5%

Alertas objetivos recomendados para a entrega:
1. Falha no processamento de OS/notificacao:
   - `autoservice.notifications.webhook.result{result=exception|failed|interrupted} >= 3` em 5m.
2. Latencia de API:
   - p95 de rotas criticas (`/atendimentos`, `/ordens-servico/**`) > 800ms por 10m.
3. Taxa de erro:
   - respostas 5xx > 2% por 5m.
4. Uptime:
   - `actuator/health` com 2 falhas consecutivas em 2m.

## Decisões Arquiteturais

- ADRs locais (placeholder): `./adr/` *(preencher quando pasta for versionada neste repo)*.
- RFCs locais (placeholder): `./rfc/` *(preencher quando pasta for versionada neste repo)*.
- ADRs consolidados (repo oficial): `https://<PREENCHER-LINK-ADRS>`.
- RFCs consolidados (repo oficial): `https://<PREENCHER-LINK-RFCS>`.
