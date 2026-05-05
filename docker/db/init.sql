CREATE SCHEMA IF NOT EXISTS cadastro;
CREATE SCHEMA IF NOT EXISTS servico;
CREATE SCHEMA IF NOT EXISTS estoque;
CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS catalog;
CREATE SCHEMA IF NOT EXISTS workorder;

-- Example tables for each schema
CREATE TABLE IF NOT EXISTS cadastro.usuario (
	id SERIAL PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS servico.servico (
	id SERIAL PRIMARY KEY,
	descricao VARCHAR(255) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS estoque.produto (
	id SERIAL PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	quantidade INT NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customer.cliente (
	id SERIAL PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	email VARCHAR(100),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS catalog.item (
	id SERIAL PRIMARY KEY,
	nome VARCHAR(100) NOT NULL,
	preco DECIMAL(10,2) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workorder.ordem_servico (
	id SERIAL PRIMARY KEY,
	descricao VARCHAR(255) NOT NULL,
	status VARCHAR(50),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

SELECT schema_name
FROM information_schema.schemata
WHERE schema_name IN ('cadastro', 'servico', 'estoque', 'customer', 'catalog', 'workorder');
