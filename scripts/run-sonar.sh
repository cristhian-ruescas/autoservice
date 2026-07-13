#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SONAR_TOKEN="$(grep '^SONAR_TOKEN=' "$ROOT/local.variable.env" | cut -d= -f2- | tr -d '\r')"
SONAR_HOST_URL="$(grep '^SONAR_HOST_URL=' "$ROOT/local.variable.env" | cut -d= -f2- | tr -d '\r' || true)"
SONAR_HOST_URL="${SONAR_HOST_URL:-http://localhost:9000}"

cd "$ROOT"
./mvnw -B verify sonar:sonar \
  -Dsonar.host.url="$SONAR_HOST_URL" \
  -Dsonar.token="$SONAR_TOKEN" \
  --no-transfer-progress

echo "=== Sonar task ==="
cat target/sonar/report-task.txt

echo "=== Vulnerabilities ==="
curl -fsS -u "${SONAR_TOKEN}:" \
  "${SONAR_HOST_URL}/api/issues/search?projectKeys=com.autoservice:autoservice&types=VULNERABILITY&ps=50" \
  | python3 -c "import sys,json; d=json.load(sys.stdin); print('total', d.get('total',0)); [print('-', i['severity'], i.get('message','')) for i in d.get('issues',[])]"
