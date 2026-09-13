# Importa os monitors versionados para a org Datadog.
# Requer DD_API_KEY e DD_APP_KEY (Organization Settings -> Application Keys).
#
# Uso (Windows PowerShell 5.1+):
#   $env:DD_API_KEY = "..."
#   $env:DD_APP_KEY = "..."
#   powershell -ExecutionPolicy Bypass -File .\scripts\import-datadog-monitors.ps1

$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Web.Extensions

$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$MonitorsFile = Join-Path $Root "docs\observability\monitors\autoservice-monitors.json"
$Site = if ($env:DD_SITE) { $env:DD_SITE } else { "datadoghq.com" }

if (-not $env:DD_API_KEY) {
    Write-Error "Defina DD_API_KEY (Organization Settings -> API Keys)."
}
if (-not $env:DD_APP_KEY) {
    Write-Error "Defina DD_APP_KEY (Organization Settings -> Application Keys)."
}

$utf8 = New-Object System.Text.UTF8Encoding $false
$jsonText = [System.IO.File]::ReadAllText($MonitorsFile, $utf8)

$serializer = New-Object System.Web.Script.Serialization.JavaScriptSerializer
$serializer.MaxJsonLength = 104857600
$monitors = $serializer.DeserializeObject($jsonText)

if ($null -eq $monitors -or $monitors.Count -eq 0) {
    Write-Error "Nenhum monitor encontrado em $MonitorsFile"
}

$uri = "https://api.$Site/api/v1/monitor"
$headers = @{
    "DD-API-KEY"         = $env:DD_API_KEY
    "DD-APPLICATION-KEY" = $env:DD_APP_KEY
}

foreach ($monitor in $monitors) {
    $body = $serializer.Serialize($monitor)
    try {
        $response = Invoke-RestMethod -Method Post -Uri $uri -Body $body -ContentType "application/json; charset=utf-8" -Headers $headers
        Write-Host "Monitor criado: $($monitor['name']) (id=$($response.id))"
    }
    catch {
        Write-Error "Falha ao criar monitor '$($monitor['name'])': $($_.Exception.Message)"
    }
}

Write-Host "Concluido. Acesse: https://app.$Site/monitors/manage"
