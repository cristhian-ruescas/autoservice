# Importa o dashboard compartilhado para a org Datadog.
# Requer DD_API_KEY e DD_APP_KEY (Organization Settings -> Application Keys).
#
# Uso:
#   $env:DD_API_KEY = "..."
#   $env:DD_APP_KEY = "..."
#   .\scripts\import-datadog-dashboard.ps1

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$DashboardFile = Join-Path $Root "docs\observability\dashboards\autoservice-tech-challenge.json"
$Site = if ($env:DD_SITE) { $env:DD_SITE } else { "datadoghq.com" }

if (-not $env:DD_API_KEY) {
    Write-Error "Defina DD_API_KEY (Organization Settings -> API Keys)."
}
if (-not $env:DD_APP_KEY) {
    Write-Error "Defina DD_APP_KEY (Organization Settings -> Application Keys)."
}

$body = Get-Content -Raw -Path $DashboardFile
$uri = "https://api.$Site/api/v1/dashboard"

$response = Invoke-RestMethod -Method Post -Uri $uri -Body $body -ContentType "application/json" -Headers @{
    "DD-API-KEY"     = $env:DD_API_KEY
    "DD-APPLICATION-KEY" = $env:DD_APP_KEY
}

Write-Host "Dashboard criado com sucesso."
Write-Host "ID: $($response.id)"
Write-Host "URL: https://app.$Site/dashboard/$($response.id)"
