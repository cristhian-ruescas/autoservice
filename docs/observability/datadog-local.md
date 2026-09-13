# Datadog — execução local

Guia para enviar traces, métricas e logs da aplicação para o Datadog **antes** do deploy no EKS.

## Pré-requisitos

- Conta Datadog com API key gerada em [Organization Settings → API Keys](https://app.datadoghq.com/organization-settings/api-keys)
- Docker (para compose) ou JDK 21 + Maven (para execução direta)

## 1. Configurar variáveis locais

Copie o exemplo e preencha a key (não commite o arquivo real):

```bash
cp local.variable.env.example local.variable.env
```

Edite `local.variable.env`:

```properties
DD_API_KEY=sua-api-key-aqui
DD_SITE=datadoghq.com
DD_ENV=local
DD_SERVICE=autoservice
```

## 2. Subir com Docker Compose

```bash
docker compose -f docker/docker-compose.yaml up --build
```

O stack sobe **dois componentes** de observabilidade:

| Componente | Função |
|------------|--------|
| `datadog-agent` | Recebe traces/logs na porta 8126 e envia para a nuvem Datadog |
| `dd-java-agent` (na app) | Instrumenta a JVM e envia traces para o Agent |

> **Importante:** o `dd-java-agent` **não envia direto** para o Datadog. Ele manda para o Agent (`DD_AGENT_HOST=datadog-agent`). Sem o Agent, os traces falham com `Failed to connect to localhost:8126`.

O agente Java só é ativado quando `DD_API_KEY` está definida no `local.variable.env`.

## 3. Subir com Maven (sem Docker)

Baixe o agente uma vez:

```powershell
curl -fsSL -o dd-java-agent.jar https://dtdg.co/latest-java-tracer
```

No PowerShell, exporte as variáveis e rode:

```powershell
$env:DD_API_KEY = "sua-api-key"
$env:DD_SITE = "datadoghq.com"
$env:DD_ENV = "local"
$env:DD_SERVICE = "autoservice"
$env:DD_LOGS_INJECTION = "true"

./mvnw spring-boot:run `
  -Dspring-boot.run.jvmArguments="-javaagent:dd-java-agent.jar"
```

## 4. Alertas (monitors)

Monitors versionados em `docs/observability/monitors/autoservice-monitors.json`:

| Monitor | Gatilho |
|---------|---------|
| Erros 5xx | > 3 em 5 min |
| Health check | erro em `/actuator/health` |
| Falha OS | `autoservice.ordem_servico.erro` > 0 |
| Uptime | nenhuma requisição em 10 min |

Import via UI (copie as queries do JSON) ou script:

```powershell
$env:DD_API_KEY = "sua-api-key"
$env:DD_APP_KEY = "sua-application-key"
.\scripts\import-datadog-monitors.ps1
```

Detalhes: `docs/observability/monitors/README.md`

## 5. Validar no Datadog

Após alguns minutos, verifique:

| Onde | O que procurar |
|------|----------------|
| **APM → Services** | serviço `autoservice` |
| **Logs → Explorer** | [app.datadoghq.com/logs](https://app.datadoghq.com/logs) — logs JSON com `correlation_id`, `dd.trace_id`, `dd.span_id` |
| **Logs → Live Tail** | [app.datadoghq.com/logs/livetail](https://app.datadoghq.com/logs/livetail) (sem "e" no meio) |
| **Infrastructure** | host/container (quando Agent K8s estiver ativo) |

## 4.1 Logs Correlation (trace ↔ log)

Pré-requisitos já configurados no projeto:

- `DD_LOGS_INJECTION=true` e `DD_TRACE_LOGS_INJECTION=true` na app
- Logback JSON inclui `dd.trace_id` e `dd.span_id` (injetados pelo Java agent)
- `HttpAccessLogFilter` emite log estruturado `event=http_access` por requisição
- Datadog Agent coleta logs dos containers (`DD_LOGS_ENABLED=true`)

### Validar correlação

1. Rebuild e suba o stack:
   ```powershell
   docker compose -f docker/docker-compose.yaml up --build -d
   ```
2. Gere tráfego:
   ```powershell
   curl.exe http://localhost:8088/actuator/health
   ```
3. **Logs → Explorer** ([/logs](https://app.datadoghq.com/logs)) ou **Live Tail** ([/logs/livetail](https://app.datadoghq.com/logs/livetail)) — filtro: `service:autoservice @event:http_access`
4. Abra um log e confirme campos `dd.trace_id` e `dd.span_id`
5. **APM → Traces** — abra um trace e clique em **Logs** (aba lateral) para ver logs correlacionados

No serviço (`APM → Services → autoservice`), o item **Logs Correlation** deve mudar para **DETECTED** após alguns minutos com tráfego.

## 5. Testar correlação

Faça uma requisição e observe o header de resposta:

No **PowerShell**, use `curl.exe` (o alias `curl` aponta para `Invoke-WebRequest`):

```powershell
curl.exe -i http://localhost:8088/actuator/health
```

O header `X-Correlation-Id` aparece na resposta e nos logs JSON.

Para forçar um ID específico:

```powershell
curl.exe -H "X-Correlation-Id: teste-demo-001" http://localhost:8088/actuator/health
```

## 6. Importar dashboard compartilhado

1. Abra [Dashboards](https://app.datadoghq.com/dashboard/lists) → **New Dashboard**
2. **⚙ Configure** → **Import dashboard JSON**
3. Cole o conteúdo de `docs/observability/dashboards/autoservice-tech-challenge.json`
4. Salve e compartilhe a URL com a equipe

Detalhes: [`dashboards/README.md`](./dashboards/README.md)

## Próximo passo (EKS)

Quando o cluster EKS estiver pronto, instale o Datadog Agent via Helm no repositório `autoservice-infra-k8s` e retome o wizard do Datadog (passos 3–5).
