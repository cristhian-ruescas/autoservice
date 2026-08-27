# Modelo relacional (resumo Fase 3)

Justificativa PostgreSQL: ACID, FKs, valores monetários e maturidade em containers/cloud gerenciada (Neon na etapa externa).

## Entidades principais

```text
cadastro.pessoa ──┬── cadastro.pessoa_fisica (cpf UNIQUE)
                  └── cadastro.pessoa_juridica (cnpj)

cadastro.cliente (pessoa_id) ──< cadastro.veiculo
                              └──< servico.ordem_servico
                                         ├── item_servico (servico / peca)
                                         └── ordem_compra / estoque

estoque.peca ── estoque.estoque
cadastro.usuario (admin JWT legado)
```

## Índices relevantes (F3)

- `pessoa_fisica.cpf` (auth CPF / lookup)
- `veiculo.placa`
- `ordem_servico(status, data_criacao)` (listagem operacional)

## Auth CPF (consumidor externo)

A function serverless (outro repositório) deve consultar o **mesmo** schema, por exemplo:

```sql
SELECT c.id
  FROM cadastro.cliente c
  JOIN cadastro.pessoa_fisica pf ON pf.id = c.pessoa_id
 WHERE pf.cpf = $1
```

Este repositório apenas **valida** o JWT emitido (`iss=autoservice-auth`).