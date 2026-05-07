-- SCHEMAS PRINCIPAIS
CREATE SCHEMA IF NOT EXISTS cadastro;
CREATE SCHEMA IF NOT EXISTS estoque;
CREATE SCHEMA IF NOT EXISTS workorder;

-- TABELA BASE DE PESSOA
CREATE TABLE IF NOT EXISTS cadastro.pessoa
(
    id
    UUID
    PRIMARY
    KEY,
    email
    VARCHAR
(
    255
),
    senha VARCHAR
(
    255
) NOT NULL,
    telefone VARCHAR
(
    20
)
    );

-- TABELA DE PESSOA FÍSICA
CREATE TABLE IF NOT EXISTS cadastro.pessoa_fisica
(
    id
    UUID
    PRIMARY
    KEY
    REFERENCES
    cadastro
    .
    pessoa
(
    id
),
    cpf VARCHAR
(
    14
) NOT NULL,
    nome VARCHAR
(
    255
) NOT NULL
    );

-- TABELA DE CLIENTE
CREATE TABLE IF NOT EXISTS cadastro.cliente
(
    id
    UUID
    PRIMARY
    KEY,
    data_cadastro
    DATE
    NOT
    NULL,
    pessoa_id
    UUID
    NOT
    NULL
    REFERENCES
    cadastro
    .
    pessoa
(
    id
)
    );

-- TABELA DE VEÍCULO
CREATE TABLE IF NOT EXISTS cadastro.veiculo
(
    id
    UUID
    PRIMARY
    KEY,
    placa
    VARCHAR
(
    10
) NOT NULL,
    marca VARCHAR
(
    50
) NOT NULL,
    modelo VARCHAR
(
    50
) NOT NULL,
    ano INT NOT NULL,
    proprietario_id UUID NOT NULL REFERENCES cadastro.cliente
(
    id
)
    );

-- TABELA DE PEÇA
CREATE TABLE IF NOT EXISTS estoque.peca
(
    id
    UUID
    PRIMARY
    KEY,
    descricao
    VARCHAR
(
    255
) NOT NULL,
    codigo VARCHAR
(
    50
) NOT NULL,
    marca VARCHAR
(
    50
) NOT NULL,
    quantidade_estoque INT NOT NULL,
    valor_unitario NUMERIC
(
    15,
    2
) NOT NULL
    );

-- TABELA DE SERVIÇO
CREATE TABLE IF NOT EXISTS cadastro.servico_cadastro
(
    id
    UUID
    PRIMARY
    KEY,
    nome
    VARCHAR
(
    255
) NOT NULL,
    descricao TEXT,
    valor_referencia NUMERIC
(
    15,
    2
) NOT NULL
    );

-- TABELA DE ORDEM DE SERVIÇO
CREATE TABLE IF NOT EXISTS workorder.ordem_servico
(
    id
    UUID
    PRIMARY
    KEY,
    cliente_id
    UUID
    NOT
    NULL
    REFERENCES
    cadastro
    .
    cliente
(
    id
),
    veiculo_id UUID NOT NULL REFERENCES cadastro.veiculo
(
    id
),
    status VARCHAR
(
    32
) NOT NULL,
    valor_total NUMERIC
(
    15,
    2
) NOT NULL
    );

-- ITENS DE SERVIÇO DA ORDEM
CREATE TABLE IF NOT EXISTS workorder.ordem_servico_item_servico
(
    id
    UUID
    PRIMARY
    KEY,
    ordem_servico_id
    UUID
    NOT
    NULL
    REFERENCES
    workorder
    .
    ordem_servico
(
    id
),
    servico_id UUID NOT NULL REFERENCES cadastro.servico_cadastro
(
    id
),
    valor NUMERIC
(
    15,
    2
) NOT NULL
    );

-- ITENS DE PEÇA DA ORDEM
CREATE TABLE IF NOT EXISTS workorder.ordem_servico_item_peca
(
    id
    UUID
    PRIMARY
    KEY,
    ordem_servico_id
    UUID
    NOT
    NULL
    REFERENCES
    workorder
    .
    ordem_servico
(
    id
),
    peca_id UUID NOT NULL REFERENCES estoque.peca
(
    id
),
    quantidade INT NOT NULL,
    valor_unitario NUMERIC
(
    15,
    2
) NOT NULL
    );

-- USUÁRIO DO SISTEMA
CREATE TABLE IF NOT EXISTS cadastro.usuario
(
    id
    UUID
    PRIMARY
    KEY,
    email
    VARCHAR
(
    255
) NOT NULL UNIQUE,
    senha VARCHAR
(
    255
) NOT NULL,
    role VARCHAR
(
    50
) NOT NULL
    );