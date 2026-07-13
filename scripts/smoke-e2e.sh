#!/usr/bin/env bash
set -euo pipefail

APP_URL="${APP_URL:-http://localhost:8088}"
FAIL=0

pass() { echo "  OK  $1"; }
fail() { echo "  FAIL $1"; FAIL=1; }

echo "=== Smoke E2E - $APP_URL ==="

# 1. Swagger
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL/swagger-ui.html")
[[ "$CODE" == "200" || "$CODE" == "302" ]] && pass "Swagger UI ($CODE)" || fail "Swagger UI ($CODE)"

# 2. OpenAPI
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL/v3/api-docs")
[[ "$CODE" == "200" ]] && pass "OpenAPI ($CODE)" || fail "OpenAPI ($CODE)"

# 3. Login
LOGIN=$(curl -s -X POST "$APP_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@autoservice.local","senha":"admin123"}')
TOKEN=$(echo "$LOGIN" | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null || true)
[[ -n "$TOKEN" ]] && pass "Login JWT" || { fail "Login JWT - $LOGIN"; exit 1; }

AUTH="Authorization: Bearer $TOKEN"

# 4. Tipos veículo
CODE=$(curl -s -o /dev/null -w "%{http_code}" -H "$AUTH" "$APP_URL/tipos-veiculo")
[[ "$CODE" == "200" ]] && pass "GET /tipos-veiculo ($CODE)" || fail "GET /tipos-veiculo ($CODE)"

# 5. Cadastrar peça
PECA=$(curl -s -w "\n%{http_code}" -X POST "$APP_URL/pecas" -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"descricao":"Peca smoke","codigo":"SMK-001","marca":"Test","valorUnitario":10.5}')
PECA_CODE=$(echo "$PECA" | tail -1)
[[ "$PECA_CODE" == "201" ]] && pass "POST /pecas ($PECA_CODE)" || fail "POST /pecas ($PECA_CODE)"

# 6. Abrir atendimento
PLACA="SMK$(date +%s | tail -c 5)K88"
ATEND=$(curl -s -w "\n%{http_code}" -X POST "$APP_URL/atendimentos" -H "$AUTH" -H "Content-Type: application/json" \
  -d "{\"tipoPessoa\":\"FISICA\",\"nome\":\"Smoke Test\",\"cpf\":\"52998224725\",\"telefone\":\"11999990000\",\"email\":\"smoke@test.com\",\"placa\":\"$PLACA\",\"marca\":\"VW\",\"modelo\":\"Gol\",\"ano\":2019,\"itens\":[{\"tipo\":\"SERVICO\",\"descricao\":\"Revisao\",\"valorUnitario\":99.9}]}")
ATEND_CODE=$(echo "$ATEND" | tail -1)
[[ "$ATEND_CODE" == "201" ]] && pass "POST /atendimentos ($ATEND_CODE)" || fail "POST /atendimentos ($ATEND_CODE) - $(echo "$ATEND" | head -1)"

# 7. Métricas
CODE=$(curl -s -o /dev/null -w "%{http_code}" -H "$AUTH" "$APP_URL/ordens-servico/metricas/tempo-execucao")
[[ "$CODE" == "200" ]] && pass "GET metricas ($CODE)" || fail "GET metricas ($CODE)"

echo "=== Resultado: $([[ $FAIL -eq 0 ]] && echo PASS || echo FAIL) ==="
exit $FAIL
