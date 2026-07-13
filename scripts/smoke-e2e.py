#!/usr/bin/env python3
"""Smoke E2E regressivo contra app rodando (Docker ou local)."""
import json
import os
import random
import sys
import time
import urllib.error
import urllib.request

BASE = os.environ.get("APP_URL", "http://localhost:8088")
FAIL = 0


def req(method, path, body=None, token=None):
    headers = {"Content-Type": "application/json"} if body is not None else {}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    data = json.dumps(body).encode() if body is not None else None
    request = urllib.request.Request(f"{BASE}{path}", data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(request, timeout=20) as response:
            return response.status, response.read().decode()
    except urllib.error.HTTPError as error:
        return error.code, error.read().decode()


def ok(name, cond, detail=""):
    global FAIL
    if cond:
        print(f"  OK  {name}")
    else:
        print(f"  FAIL {name} {detail}")
        FAIL = 1


print(f"=== Smoke E2E {BASE} ===")

for _ in range(30):
    try:
        urllib.request.urlopen(f"{BASE}/swagger-ui.html", timeout=3)
        break
    except Exception:
        time.sleep(2)
else:
    print("  FAIL app nao respondeu")
    sys.exit(1)

code, _ = req("GET", "/swagger-ui.html")
ok("Swagger UI", code in (200, 302), f"({code})")

code, _ = req("GET", "/v3/api-docs")
ok("OpenAPI", code == 200, f"({code})")

code, login_body = req("POST", "/auth/login", {"username": "admin@autoservice.local", "password": "admin123"})
token = json.loads(login_body).get("token", "") if code == 200 else ""
ok("Login JWT", bool(token), login_body[:120])

code, peca_body = req(
    "POST",
    "/pecas",
    {
        "descricao": "Peca smoke",
        "codigo": f"SMK{int(time.time()) % 100000}",
        "marca": "Test",
        "valorUnitario": 10.5,
    },
    token=token,
)
peca_id = json.loads(peca_body).get("id", "") if code == 201 else ""
ok("POST /pecas", code == 201, f"({code})")

placa = f"ATD{random.randint(0, 9)}K{random.randint(10, 99):02d}"
atend_body = {
    "tipoPessoa": "FISICA",
    "nome": "Smoke Test",
    "cpf": "52998224725",
    "telefone": "11912345678",
    "email": "smoke@test.com",
    "placa": placa,
    "marca": "Fiat",
    "modelo": "Uno",
    "ano": 2015,
    "cor": "Branco",
    "kilometragem": 85000,
    "relato": "Teste regressivo E2E",
    "itens": [
        {"tipo": "SERVICO", "descricao": "Diagnostico", "quantidade": 1, "valorUnitario": 120.0},
        {"tipo": "PECA", "pecaId": peca_id, "quantidade": 1, "valorUnitario": 10.5},
    ],
}
code, atend_resp = req("POST", "/atendimentos", atend_body, token=token)
os_id = json.loads(atend_resp).get("ordemServicoId", "") if code == 201 else ""
ok("POST /atendimentos", code == 201, f"({code}) {atend_resp[:120]}")

code, itens_body = req("GET", f"/ordens-servico/{os_id}/itens", token=token)
itens = json.loads(itens_body) if code == 200 else []
ok("GET /ordens-servico/{id}/itens", code == 200 and len(itens) >= 2, f"({code}) count={len(itens)}")

code, andamento_body = req("GET", f"/ordens-servico/{os_id}/andamento")
ok("GET /andamento (publico)", code == 200, f"({code})")

code, _ = req("GET", "/ordens-servico/metricas/tempo-medio-execucao", token=token)
ok("GET metricas", code == 200, f"({code})")

code, _ = req("GET", "/clientes", token=token)
ok("GET /clientes", code == 200, f"({code})")

print("=== Resultado:", "PASS" if FAIL == 0 else "FAIL", "===")
sys.exit(FAIL)
