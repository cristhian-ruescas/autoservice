#!/usr/bin/env bash
set -euo pipefail

REPORT_TASK_FILE="${REPORT_TASK_FILE:-target/sonar/report-task.txt}"

report_task_value() {
  local key="$1"

  if [[ -f "$REPORT_TASK_FILE" ]]; then
    grep "^${key}=" "$REPORT_TASK_FILE" | cut -d= -f2- || true
  fi
}

SONAR_URL="${SONAR_URL:-$(report_task_value serverUrl)}"
SONAR_URL="${SONAR_URL:-http://localhost:9000}"
SONAR_PROJECT_KEY="${SONAR_PROJECT_KEY:-$(report_task_value projectKey)}"
SONAR_PROJECT_KEY="${SONAR_PROJECT_KEY:-com.autoservice:autoservice}"
REPORT_OUTPUT="${REPORT_OUTPUT:-docs/security/sonar-vulnerability-report.md}"
RAW_OUTPUT_DIR="${RAW_OUTPUT_DIR:-target/sonar-security}"

if [[ -z "${SONAR_TOKEN:-}" ]]; then
  echo "SONAR_TOKEN is required. Example: export SONAR_TOKEN=<token>" >&2
  exit 1
fi

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required to call the SonarQube API." >&2
  exit 1
fi

if ! command -v python3 >/dev/null 2>&1; then
  echo "python3 is required to generate the Markdown report." >&2
  exit 1
fi

mkdir -p "$RAW_OUTPUT_DIR" "$(dirname "$REPORT_OUTPUT")"

url_encode() {
  python3 -c 'import sys, urllib.parse; print(urllib.parse.quote(sys.argv[1], safe=""))' "$1"
}

api_get() {
  local path="$1"
  local output="$2"

  echo "GET ${SONAR_URL}${path}" >&2
  curl --fail --silent --show-error \
    --user "${SONAR_TOKEN}:" \
    "${SONAR_URL}${path}" \
    --output "$output"
}

api_get_optional() {
  local path="$1"
  local output="$2"
  local fallback_message="$3"

  echo "GET ${SONAR_URL}${path}" >&2
  if ! curl --fail --silent --show-error \
    --user "${SONAR_TOKEN}:" \
    "${SONAR_URL}${path}" \
    --output "$output"; then
    printf '{"api_error": "%s", "hotspots": []}\n' "$fallback_message" > "$output"
    echo "Warning: ${fallback_message}" >&2
  fi
}

echo "Fetching SonarQube security data for project '${SONAR_PROJECT_KEY}'..."

ENCODED_PROJECT_KEY="$(url_encode "$SONAR_PROJECT_KEY")"

api_get \
  "/api/issues/search?componentKeys=${ENCODED_PROJECT_KEY}&types=VULNERABILITY&ps=500" \
  "${RAW_OUTPUT_DIR}/vulnerabilities.json"

api_get_optional \
  "/api/hotspots/search?projectKey=${ENCODED_PROJECT_KEY}&ps=500" \
  "${RAW_OUTPUT_DIR}/security-hotspots.json" \
  "Could not fetch Security Hotspots. The token may not have permission to browse security hotspot data."

api_get \
  "/api/measures/component?component=${ENCODED_PROJECT_KEY}&metricKeys=bugs,vulnerabilities,security_hotspots,code_smells,coverage,duplicated_lines_density,alert_status" \
  "${RAW_OUTPUT_DIR}/measures.json"

api_get \
  "/api/qualitygates/project_status?projectKey=${ENCODED_PROJECT_KEY}" \
  "${RAW_OUTPUT_DIR}/quality-gate.json"

export SONAR_URL SONAR_PROJECT_KEY REPORT_OUTPUT RAW_OUTPUT_DIR

python3 <<'PY'
import json
import os
import subprocess
from datetime import datetime
from pathlib import Path

sonar_url = os.environ["SONAR_URL"].rstrip("/")
project_key = os.environ["SONAR_PROJECT_KEY"]
report_output = Path(os.environ["REPORT_OUTPUT"])
raw_dir = Path(os.environ["RAW_OUTPUT_DIR"])


def load_json(name):
    with (raw_dir / name).open(encoding="utf-8") as file:
        return json.load(file)


def git_value(args, default="N/A"):
    try:
        return subprocess.check_output(
            ["git", *args],
            text=True,
            stderr=subprocess.DEVNULL,
        ).strip() or default
    except Exception:
        return default


def measure_map(payload):
    measures = payload.get("component", {}).get("measures", [])
    return {item.get("metric"): item.get("value", "N/A") for item in measures}


def text(value):
    if value is None or value == "":
        return "N/A"
    return str(value).replace("|", "\\|").replace("\n", " ")


def issue_location(issue):
    component = issue.get("component", "")
    file_path = component.split(":", 1)[-1]
    line = issue.get("line")
    return f"{file_path}:{line}" if line else file_path


def hotspot_location(hotspot):
    component = hotspot.get("component", "")
    file_path = component.split(":", 1)[-1]
    line = hotspot.get("line")
    return f"{file_path}:{line}" if line else file_path


vulnerabilities = load_json("vulnerabilities.json")
hotspots = load_json("security-hotspots.json")
measures = measure_map(load_json("measures.json"))
quality_gate = load_json("quality-gate.json").get("projectStatus", {})

issues = vulnerabilities.get("issues", [])
security_hotspots = hotspots.get("hotspots", [])
hotspots_api_error = hotspots.get("api_error")
quality_gate_status = quality_gate.get("status", measures.get("alert_status", "N/A"))

branch = git_value(["rev-parse", "--abbrev-ref", "HEAD"])
commit = git_value(["rev-parse", "--short", "HEAD"])
generated_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
project_url = f"{sonar_url}/dashboard?id={project_key}"

lines = [
    "# Relatório de vulnerabilidades - SonarQube",
    "",
    "## Contexto",
    "",
    "Este relatório registra a análise de segurança do código-fonte feita com SonarQube.",
    "",
    "| Campo | Valor |",
    "| --- | --- |",
    f"| Projeto | `{project_key}` |",
    f"| URL do SonarQube | {project_url} |",
    f"| Branch | `{branch}` |",
    f"| Commit | `{commit}` |",
    f"| Gerado em | {generated_at} |",
    "",
    "## Resumo da análise",
    "",
    "| Métrica | Resultado |",
    "| --- | ---: |",
    f"| Quality Gate | {text(quality_gate_status)} |",
    f"| Bugs | {text(measures.get('bugs'))} |",
    f"| Vulnerabilidades | {text(measures.get('vulnerabilities'))} |",
    f"| Security Hotspots | {text(measures.get('security_hotspots'))} |",
    f"| Code Smells | {text(measures.get('code_smells'))} |",
    f"| Cobertura | {text(measures.get('coverage'))}% |",
    f"| Duplicação | {text(measures.get('duplicated_lines_density'))}% |",
    "",
    "## Vulnerabilidades",
    "",
]

if issues:
    lines.extend([
        "| Severidade | Status | Regra | Local | Mensagem |",
        "| --- | --- | --- | --- | --- |",
    ])
    for issue in issues:
        lines.append(
            "| {severity} | {status} | {rule} | {location} | {message} |".format(
                severity=text(issue.get("severity")),
                status=text(issue.get("status")),
                rule=text(issue.get("rule")),
                location=text(issue_location(issue)),
                message=text(issue.get("message")),
            )
        )
else:
    lines.append("Nenhuma vulnerabilidade foi identificada pelo SonarQube na análise atual.")

lines.extend([
    "",
    "## Security Hotspots",
    "",
])

if hotspots_api_error:
    lines.extend([
        "Não foi possível consultar Security Hotspots pela API com o token informado.",
        "",
        f"Motivo registrado pelo script: `{text(hotspots_api_error)}`",
    ])
elif security_hotspots:
    lines.extend([
        "| Probabilidade | Status | Local | Mensagem |",
        "| --- | --- | --- | --- |",
    ])
    for hotspot in security_hotspots:
        lines.append(
            "| {probability} | {status} | {location} | {message} |".format(
                probability=text(hotspot.get("vulnerabilityProbability")),
                status=text(hotspot.get("status")),
                location=text(hotspot_location(hotspot)),
                message=text(hotspot.get("message")),
            )
        )
else:
    lines.append("Nenhum security hotspot foi identificado pelo SonarQube na análise atual.")

lines.extend([
    "",
    "## Evidências geradas",
    "",
    f"- JSON bruto de vulnerabilidades: `{raw_dir / 'vulnerabilities.json'}`",
    f"- JSON bruto de security hotspots: `{raw_dir / 'security-hotspots.json'}`",
    f"- JSON bruto de metricas: `{raw_dir / 'measures.json'}`",
    f"- JSON bruto do quality gate: `{raw_dir / 'quality-gate.json'}`",
    "",
    "## Observações",
    "",
    "- A análise do SonarQube é uma verificação estática automatizada.",
    "- Resultado sem vulnerabilidades não substitui revisão manual, testes de segurança ou pentest.",
    "- Security hotspots, quando existirem, devem ser revisados por uma pessoa desenvolvedora antes de serem aceitos.",
    "",
])

report_output.write_text("\n".join(lines), encoding="utf-8")
print(f"Report generated at {report_output}")
PY

echo "Raw API responses saved at ${RAW_OUTPUT_DIR}"
