# Dashboard Datadog — Autoservice

Dashboard compartilhado na org Datadog para o Tech Challenge Fase 3.

## Conteúdo

| Widget | Requisito PDF |
|--------|---------------|
| Latência P95 por endpoint | ✅ |
| Volume de requisições | ✅ (proxy até custom metric de OS) |
| Erros 5xx | ✅ |
| Visão APM do serviço | ✅ |
| Logs JSON | ✅ |
| Volume diário de OS | ✅ `autoservice.ordem_servico.volume_diario` |
| Tempo médio por fase | ✅ `autoservice.ordem_servico.tempo_fase` |
| CPU/memória K8s | ⏳ pendente (EKS) |

## Importar (recomendado — via UI)

1. Acesse [Dashboards → New Dashboard](https://app.datadoghq.com/dashboard/lists)
2. Clique no ícone **⚙ Configure** (canto superior direito)
3. **Import dashboard JSON**
4. Cole o conteúdo de `autoservice-tech-challenge.json`
5. Salve

O dashboard fica **visível para todos os membros da org** Datadog. Compartilhe a URL:

```
https://app.datadoghq.com/dashboard/<id>
```

## Importar via script (API)

Requer **Application Key** além da API Key:

1. [Organization Settings → Application Keys](https://app.datadoghq.com/organization-settings/application-keys) → **New Key**
2. Execute:

```powershell
$env:DD_API_KEY = "sua-api-key"
$env:DD_APP_KEY = "sua-application-key"

# Windows: se der erro de script não assinado, use:
powershell -ExecutionPolicy Bypass -File .\scripts\import-datadog-dashboard.ps1
```

## Filtros

- `env`: `local`, `homolog`, `prod`
- `service`: `autoservice`
