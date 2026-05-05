CREATE SCHEMA IF NOT EXISTS cadastro;
CREATE SCHEMA IF NOT EXISTS servico;
CREATE SCHEMA IF NOT EXISTS estoque;

SELECT schema_name
FROM information_schema.schemata
WHERE schema_name IN ('cadastro', 'servico', 'estoque');