# Autoservice — API da oficina (MVP)

Back-end monolítico em camadas (**Spring Boot 3**, **Java 21**) para gestão de **ordens de serviço**, **clientes**, *
*veículos**, **peças**, **estoque**, **ordens de compra**, **catálogo de serviços** e **métricas de tempo de execução**.

Documentação interativa: **`/swagger-ui.html`** (OpenAPI em **`/v3/api-docs`**).

## Objetivos do projeto

- Formalizar o fluxo de **atendimento → diagnóstico → orçamento → aprovação → execução → entrega**.
- Centralizar **cadastros** e **estoque** com rastreabilidade.
- Oferecer **acompanhamento da OS** por API (`GET /ordens-servico/{id}/andamento`).
- Dar suporte à disciplina de **DDD** (domínio, aplicação, infraestrutura, apresentação) e qualidade (testes, cobertura
  nos pacotes de domínio/aplicação).

## Por que PostgreSQL?

Foi adotado **PostgreSQL** por ser **open-source**, amplamente usado em produção, com forte suporte a **integridade
referencial**, **transações ACID**, tipos numéricos/decimais para valores monetários e adequação a dados relacionais (
clientes, veículos, OS, itens, estoque). O driver oficial integra-se bem com **Spring Data JPA** e com ambientes
containerizados.

## Como rodar localmente

### Pré-requisitos

- **JDK 21**
- **Maven** (ou use o wrapper `./mvnw` na raiz do projeto)
- **PostgreSQL** acessível (ou **Docker** — ver pasta `docker/`)

### Variáveis e porta

Por padrão (`src/main/resources/application.yaml`):

| Variável / propriedade       | Exemplo                                        | Descrição                                                      |
|------------------------------|------------------------------------------------|----------------------------------------------------------------|
| `server.port`                | `8088`                                         | Porta HTTP                                                     |
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5432/autoservice` | JDBC                                                           |
| `SPRING_DATASOURCE_USERNAME` | `postgres`                                     | Usuário                                                        |
| `SPRING_DATASOURCE_PASSWORD` | `postgres`                                     | Senha                                                          |
| `APP_BASE_URL`               | `http://localhost:8088`                        | Base URL nos links de orçamento                                |
| `MAIL_*`                     | opcional                                       | SMTP para envio de orçamento (defaults apontam para localhost) |

Crie o banco `autoservice` no Postgres ou use o `docker-compose` da pasta `docker/`.

### Build e execução

```bash
./mvnw clean verify    # testes + relatório JaCoCo em target/site/jacoco
./mvnw spring-boot:run
```

Abra o Swagger em: **http://localhost:8088/swagger-ui.html**

### Docker (Postgres + aplicação)

Na raiz do repositório:

```bash
docker compose -f docker/docker-compose.yaml up --build
```

- **Postgres:** porta **5432**
- **API:** porta **8088**

O `Dockerfile` está em **`docker/Dockerfile`** (build multi-stage com Maven + JRE 21).

## Testes e cobertura

- **Unitários** e **integração** (Testcontainers + Postgres quando o Docker está disponível).
- Testes de integração com `@EnabledIf` ignoram o ambiente sem Docker.
- **JaCoCo** (`pom.xml`): relatório na fase `test`; regra de cobertura aplicada ao bundle configurado (exclui, entre
  outros, `presentation` e `infrastructure` do relatório de verificação — alinhado ao foco em **domínio** e **casos de
  uso**).

## Qualidade e vulnerabilidades com SonarQube

O projeto possui SonarScanner for Maven configurado no `pom.xml` e um SonarQube local no Docker Compose via profile
`quality`.

Suba o SonarQube:

```bash
docker compose -f docker/docker-compose.yaml --profile quality up -d sonarqube
```

Acesse `http://localhost:9000`, crie um token de análise e execute:

```bash
export SONAR_TOKEN=<token-gerado-no-sonarqube>
./mvnw clean verify sonar:sonar
```

## Principais recursos da API

| Área                               | Base path                                       |
|------------------------------------|-------------------------------------------------|
| Atendimento / abertura de OS       | `/atendimentos`                                 |
| Ordens de serviço                  | `/ordens-servico`                               |
| Métricas (tempo médio de execução) | `/ordens-servico/metricas/tempo-medio-execucao` |
| Catálogo de serviços               | `/servicos`                                     |
| Peças                              | `/pecas`                                        |
| Estoque                            | `/estoques`                                     |
| Ordens de compra                   | `/ordens-compra`                                |

## Decisões de modelagem do MVP

### Criação de cliente e veículo no fluxo de atendimento

No MVP, a criação de cliente e veículo foi centralizada em `POST /atendimentos`, que já abre a OS inicial no status
`RECEBIDO`.
Essa decisão evita cadastros órfãos e mantém o primeiro registro do atendimento (cliente, veículo e relato inicial) de
forma transacional.

Os endpoints administrativos de clientes e veículos cobrem listagem, consulta, atualização e remoção (`GET`, `PUT`,
`DELETE`), enquanto o `create` ocorre no fluxo principal de negócio.

### Controle de estoque orientado a eventos de negócio

O controle de estoque é atualizado automaticamente por eventos do domínio:

- entrada ao realizar ordem de compra;
- baixa ao finalizar execução da OS.

Assim, o saldo de estoque permanece consistente com o ciclo operacional da oficina.

## Licença / uso acadêmico

Projeto desenvolvido no contexto do **Tech Challenge** (pós-graduação SOAT).
