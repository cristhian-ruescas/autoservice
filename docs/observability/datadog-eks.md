# Datadog — homolog e produção (EKS)

Guia para observabilidade da aplicação **Autoservice** nos ambientes AWS (EKS), complementando o [setup local](datadog-local.md).

## Arquitetura

```text
┌─────────────────┐     traces/logs/metrics     ┌──────────────────┐
│  autoservice    │ ──────────────────────────► │ Datadog Agent    │
│  (pod EKS)      │   DD_AGENT_HOST=hostIP      │ (DaemonSet Helm) │
└─────────────────┘                             └────────┬─────────┘
                                                         │
                                                         ▼
                                                Datadog Cloud (env tag)
```

A **API key fica só no Agent** (Terraform + Secret K8s). O pod da aplicação não precisa de `DD_API_KEY`.

## 1. Instalar o Agent no EKS (OBS-04)

Repositório: `autoservice-infra-k8s`

1. Adicione o secret `DD_API_KEY` no GitHub Actions do repo infra (já usado pelo workflow Terraform).
2. Em `terraform/environments/homolog/terraform.tfvars` (ou prod):

```hcl
enable_datadog_agent = true
dd_site              = "datadoghq.com"
```

3. Exporte a key localmente (nunca commite):

```powershell
$env:TF_VAR_dd_api_key = "sua-api-key"
cd C:\Users\Rafael\code\autoservice-infra-k8s\terraform\environments\homolog
terraform init
terraform apply
```

O módulo `terraform/modules/datadog` cria namespace `datadog`, Secret e `helm_release` com APM, logs e DogStatsD habilitados.

## 2. Deploy da aplicação com tags Datadog

Repositório: `autoservice` — manifests em `k8s/`

| Variável | Homolog | Produção |
|----------|---------|----------|
| `DD_ENV` | `homolog` | `prod` |
| `DD_SERVICE` | `autoservice` | `autoservice` |
| `DD_TRACE_ENABLED` | `true` | `true` |
| `DD_AGENT_HOST` | `status.hostIP` (no Deployment) | idem |

Antes do deploy em **prod**, altere em `k8s/10-configmap-app.yaml` e nos labels `tags.datadoghq.com/env` do `k8s/30-deployment-app.yaml`.

O `Dockerfile` ativa o `dd-java-agent` quando `DD_TRACE_ENABLED=true` **ou** `DD_API_KEY` está definida (compatível com local).

## 3. Validar no Datadog

Após deploy:

1. **APM → Services** — filtre `env:homolog` ou `env:prod`, `service:autoservice`
2. **Logs** — filtre `service:autoservice env:homolog`
3. **Metrics** — `autoservice.ordem_servico.*` com tag `env`

Importe dashboard e monitors (se ainda não fez):

```powershell
$env:DD_API_KEY = "sua-api-key"
$env:DD_APP_KEY = "sua-application-key"
powershell -ExecutionPolicy Bypass -File .\scripts\import-datadog-dashboard.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\import-datadog-monitors.ps1
```

Nos monitors, adicione `env:homolog` ou `env:prod` às queries — ver [monitors/README.md](monitors/README.md).

## 4. Lambda de autenticação (OBS-07)

Repositório: `autoservice-lambda-auth`

O `serverless.yml` inclui variáveis `DD_*` e layers Datadog (Python + Extension). Para habilitar:

```powershell
$env:DD_API_KEY = "sua-api-key"
$env:DD_TRACE_ENABLED = "true"
serverless deploy --stage homolog
```

Atualize os ARNs das layers conforme a [documentação Datadog](https://docs.datadoghq.com/serverless/libraries_integrations/extension/) se a versão mudar.

## 5. CI/CD (próximo passo — F3-DEPLOY-01)

O workflow atual do `autoservice` ainda faz deploy em k3d local. Quando migrar para EKS:

- Secret `DD_API_KEY` no GitHub (só infra, se Agent via Terraform)
- Pipeline aplica manifests `k8s/` no cluster correto
- Homolog: branch `develop` → EKS homolog
- Prod: branch `main` → EKS prod

## Checklist homolog

- [ ] `enable_datadog_agent = true` aplicado no Terraform homolog
- [ ] Pods `datadog-agent` Running no namespace `datadog`
- [ ] App deployada com `DD_ENV=homolog` e traces visíveis no APM
- [ ] Monitors com filtro `env:homolog`
- [ ] Lambda auth com `DD_TRACE_ENABLED=true` (opcional nesta fase)
