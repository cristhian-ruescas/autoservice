# Monitors Datadog — Autoservice



Alertas versionados para o Tech Challenge Fase 3.



## Monitors incluídos



| Monitor | Tipo | Objetivo |

|---------|------|----------|

| Taxa de erros 5xx elevada | APM query | Detecta instabilidade nas APIs |

| Health check com falha | APM query | Falha em `/actuator/health` |

| Falhas em ordens de serviço | Custom metric | `autoservice.ordem_servico.erro` |

| Ausência de requisições | APM query | Proxy de uptime quando não há tráfego |



## Importar via UI (recomendado)



1. Acesse [Monitors → New Monitor](https://app.datadoghq.com/monitors/create)

2. Escolha o tipo correspondente (Metric / APM)

3. Use as queries do arquivo `autoservice-monitors.json` como referência

4. Ajuste `@slack-autoservice` para seu canal real ou remova menções



## Importar via script (API)

Requer **Application Key** além da API Key.

> **Windows:** se aparecer erro *"não está assinado digitalmente"*, use `-ExecutionPolicy Bypass` (veja abaixo).

```powershell
cd C:\Users\Rafael\code\autoservice

$env:DD_API_KEY = "sua-api-key"
$env:DD_APP_KEY = "sua-application-key"

# Opção 1 — bypass só nesta janela (recomendado)
powershell -ExecutionPolicy Bypass -File .\scripts\import-datadog-monitors.ps1

# Opção 2 — liberar scripts só na sessão atual
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\import-datadog-monitors.ps1
```



## Métrica de erro de OS



A app incrementa `autoservice.ordem_servico.erro{tipo=domain|unexpected}` quando ocorre erro em rotas `/ordens-servico` (422 de domínio ou 500 inesperado).



## Filtros



Os monitors usam `service:autoservice`. Em homolog/prod, adicione `env:homolog` ou `env:prod` nas queries após deploy no EKS.

