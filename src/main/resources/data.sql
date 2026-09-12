-- Migração de dados legados: preenche telefones nulos para leitura consistente.
UPDATE cadastro.pessoa
SET telefone = '11999999999'
WHERE telefone IS NULL;

-- Seed local alinhado ao modelo JPA atual.

INSERT INTO cadastro.usuario (id, email, senha, role)
VALUES ('11111111-1111-1111-1111-111111111111',
        'admin@autoservice.local',
        '$2a$10$XZMSbuJY22lBx/ioRaGjr.TRuU/6hvj5CdX9KJurzgKTisbWi8a42',
        'ADMIN')
ON CONFLICT (email) DO UPDATE
SET senha = EXCLUDED.senha,
    role  = EXCLUDED.role;

INSERT INTO cadastro.pessoa (id, email, telefone)
VALUES ('11111111-1111-1111-1111-111111111111', 'admin@autoservice.local', '11999999999'),
       ('22222222-2222-2222-2222-222222222222', 'cliente2@autoservice.local', '11999999998'),
       ('33333333-3333-3333-3333-333333333333', 'cliente3@autoservice.local', '11999999997')
ON CONFLICT (id) DO UPDATE
SET email = EXCLUDED.email,
    telefone = EXCLUDED.telefone;

INSERT INTO cadastro.pessoa_fisica (id, cpf, nome)
VALUES ('11111111-1111-1111-1111-111111111111', '52998224725', 'Admin User'),
       ('22222222-2222-2222-2222-222222222222', '39053344705', 'Cliente Dois'),
       ('33333333-3333-3333-3333-333333333333', '11144477735', 'Cliente Tres')
ON CONFLICT (id) DO UPDATE
SET cpf = EXCLUDED.cpf,
    nome = EXCLUDED.nome;

INSERT INTO cadastro.cliente (id, data_cadastro, pessoa_id)
VALUES ('11111111-1111-1111-1111-111111111111', '2024-01-01', '11111111-1111-1111-1111-111111111111'),
       ('22222222-2222-2222-2222-222222222222', '2024-01-02', '22222222-2222-2222-2222-222222222222'),
       ('33333333-3333-3333-3333-333333333333', '2024-01-03', '33333333-3333-3333-3333-333333333333')
ON CONFLICT (id) DO UPDATE
SET data_cadastro = EXCLUDED.data_cadastro,
    pessoa_id = EXCLUDED.pessoa_id;

INSERT INTO cadastro.tipo_veiculo (id, marca, modelo, ano)
VALUES ('44444444-4444-4444-4444-444444444444', 'Fiat', 'Uno', 2020),
       ('55555555-5555-5555-5555-555555555555', 'VW', 'Gol', 2021)
ON CONFLICT (id) DO UPDATE
SET marca = EXCLUDED.marca,
    modelo = EXCLUDED.modelo,
    ano = EXCLUDED.ano;

INSERT INTO cadastro.veiculo (id, proprietario_id, tipo_veiculo_id, placa, cor, kilometragem)
VALUES ('77777777-7777-7777-7777-777777777777',
        '11111111-1111-1111-1111-111111111111',
        '44444444-4444-4444-4444-444444444444',
        'ABC1A23',
        'Prata',
        45000),
       ('88888888-8888-8888-8888-888888888888',
        '22222222-2222-2222-2222-222222222222',
        '55555555-5555-5555-5555-555555555555',
        'DEF4B56',
        'Branco',
        38000)
ON CONFLICT (id) DO UPDATE
SET proprietario_id = EXCLUDED.proprietario_id,
    tipo_veiculo_id = EXCLUDED.tipo_veiculo_id,
    placa = EXCLUDED.placa,
    cor = EXCLUDED.cor,
    kilometragem = EXCLUDED.kilometragem;

INSERT INTO estoque.estoque (id, quantidade_disponivel, quantidade_minima, localizacao)
VALUES ('66666666-6666-6666-6666-666666666666', 10, 2, 'Corredor A1'),
       ('66666666-6666-6666-6666-666666666667', 20, 4, 'Corredor B2')
ON CONFLICT (id) DO UPDATE
SET quantidade_disponivel = EXCLUDED.quantidade_disponivel,
    quantidade_minima = EXCLUDED.quantidade_minima,
    localizacao = EXCLUDED.localizacao;

INSERT INTO estoque.peca (id, descricao, codigo, marca, valor_unitario, estoque_id, tipo_veiculo_id)
VALUES ('99999999-9999-9999-9999-999999999999',
        'Filtro de oleo',
        'FILTRO-001',
        'Fiat',
        50.00,
        '66666666-6666-6666-6666-666666666666',
        '44444444-4444-4444-4444-444444444444'),
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'Pastilha de freio',
        'PASTILHA-001',
        'Bosch',
        80.00,
        '66666666-6666-6666-6666-666666666667',
        '55555555-5555-5555-5555-555555555555')
ON CONFLICT (id) DO UPDATE
SET descricao = EXCLUDED.descricao,
    codigo = EXCLUDED.codigo,
    marca = EXCLUDED.marca,
    valor_unitario = EXCLUDED.valor_unitario,
    estoque_id = EXCLUDED.estoque_id,
    tipo_veiculo_id = EXCLUDED.tipo_veiculo_id;

INSERT INTO cadastro.servico_cadastro (id, nome, descricao, valor_referencia)
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Alinhamento', 'Alinhamento de direcao', 120.00),
       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Troca de oleo', 'Troca de oleo do motor', 90.00)
ON CONFLICT (id) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    valor_referencia = EXCLUDED.valor_referencia;

INSERT INTO servico.ordem_servico (
    id,
    veiculo_id,
    status,
    data_criacao,
    relato,
    tempo_previsto_execucao_dias,
    tempo_previsto_execucao_horas,
    iniciado_em,
    finalizado_em
)
VALUES ('dddddddd-dddd-dddd-dddd-dddddddddddd',
        '77777777-7777-7777-7777-777777777777',
        'RECEBIDO',
        '2024-01-10',
        'Cliente relatou ruido ao frear',
        NULL,
        NULL,
        NULL,
        NULL)
ON CONFLICT (id) DO UPDATE
SET veiculo_id = EXCLUDED.veiculo_id,
    status = EXCLUDED.status,
    data_criacao = EXCLUDED.data_criacao,
    relato = EXCLUDED.relato,
    tempo_previsto_execucao_dias = EXCLUDED.tempo_previsto_execucao_dias,
    tempo_previsto_execucao_horas = EXCLUDED.tempo_previsto_execucao_horas,
    iniciado_em = EXCLUDED.iniciado_em,
    finalizado_em = EXCLUDED.finalizado_em;

INSERT INTO servico.item_servico (id, ordem_servico_id, tipo, descricao, peca_id, quantidade, valor_unitario)
VALUES ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        'SERVICO',
        'Alinhamento de direcao',
        NULL,
        1,
        120.00),
       ('ffffffff-ffff-ffff-ffff-ffffffffffff',
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        'PECA',
        'Filtro de oleo',
        '99999999-9999-9999-9999-999999999999',
        1,
        50.00)
ON CONFLICT (id) DO UPDATE
SET ordem_servico_id = EXCLUDED.ordem_servico_id,
    tipo = EXCLUDED.tipo,
    descricao = EXCLUDED.descricao,
    peca_id = EXCLUDED.peca_id,
    quantidade = EXCLUDED.quantidade,
    valor_unitario = EXCLUDED.valor_unitario;

INSERT INTO estoque.ordem_compra (id, status, data_compra)
VALUES ('12121212-1212-1212-1212-121212121212', 'PENDENTE', '2024-01-15')
ON CONFLICT (id) DO UPDATE
SET status = EXCLUDED.status,
    data_compra = EXCLUDED.data_compra;

INSERT INTO estoque.item_ordem_compra (id, ordem_compra_id, peca_id, quantidade)
VALUES ('13131313-1313-1313-1313-131313131313',
        '12121212-1212-1212-1212-121212121212',
        '99999999-9999-9999-9999-999999999999',
        5)
ON CONFLICT (id) DO UPDATE
SET ordem_compra_id = EXCLUDED.ordem_compra_id,
    peca_id = EXCLUDED.peca_id,
    quantidade = EXCLUDED.quantidade;

-- Corrige CPFs inválidos legados que quebram leitura via AttributeConverter.
UPDATE cadastro.pessoa_fisica SET cpf = '52998224725' WHERE cpf = '12345678900';
UPDATE cadastro.pessoa_fisica SET cpf = '39053344705' WHERE cpf = '98765432100';
UPDATE cadastro.pessoa_fisica SET cpf = '11144477735' WHERE cpf = '11122233344';
