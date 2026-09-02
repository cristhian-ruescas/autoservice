-- Bootstrap minimo para ambiente local.
-- As demais tabelas sao mantidas pelo Hibernate via spring.jpa.hibernate.ddl-auto=update.

CREATE SCHEMA IF NOT EXISTS cadastro;
CREATE SCHEMA IF NOT EXISTS estoque;
CREATE SCHEMA IF NOT EXISTS servico;

CREATE TABLE IF NOT EXISTS cadastro.usuario
(
    id    VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role  VARCHAR(50)  NOT NULL
);
