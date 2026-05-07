-- POPULATE cadastro.pessoa
INSERT INTO cadastro.pessoa (id, email, senha, telefone)
VALUES ('11111111-1111-1111-1111-111111111111', 'admin@autoservice.local', 'senha', '11999999999'),
       ('22222222-2222-2222-2222-222222222222', 'cliente2@autoservice.local', 'senha', '11999999998'),
       ('33333333-3333-3333-3333-333333333333', 'cliente3@autoservice.local', 'senha',
        '11999999997') ON CONFLICT (id) DO NOTHING;

-- POPULATE cadastro.pessoa_fisica
INSERT INTO cadastro.pessoa_fisica (id, cpf, nome)
VALUES ('11111111-1111-1111-1111-111111111111', '123.456.789-00', 'Admin User'),
       ('22222222-2222-2222-2222-222222222222', '987.654.321-00', 'Cliente Dois'),
       ('33333333-3333-3333-3333-333333333333', '111.222.333-44', 'Cliente Três') ON CONFLICT (id) DO NOTHING;

-- POPULATE cadastro.cliente
INSERT INTO cadastro.cliente (id, data_cadastro, pessoa_id)
VALUES ('11111111-1111-1111-1111-111111111111', '2024-01-01', '11111111-1111-1111-1111-111111111111'),
       ('22222222-2222-2222-2222-222222222222', '2024-01-02', '22222222-2222-2222-2222-222222222222'),
       ('33333333-3333-3333-3333-333333333333', '2024-01-03',
        '33333333-3333-3333-3333-333333333333') ON CONFLICT (id) DO NOTHING;

-- POPULATE cadastro.veiculo
INSERT INTO cadastro.veiculo (id, placa, marca, modelo, ano, proprietario_id)
VALUES ('77777777-7777-7777-7777-777777777777', 'ABC1A23', 'Fiat', 'Uno', 2020, '11111111-1111-1111-1111-111111111111'),
       ('88888888-8888-8888-8888-888888888888', 'DEF4B56', 'VW', 'Gol', 2021,
        '22222222-2222-2222-2222-222222222222') ON CONFLICT (id) DO NOTHING;

-- POPULATE estoque.peca
INSERT INTO estoque.peca (id, descricao, codigo, marca, quantidade_estoque, valor_unitario)
VALUES ('99999999-9999-9999-9999-999999999999', 'Filtro de óleo', 'FILTRO-001', 'Fiat', 10, 50.00),
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Pastilha de freio', 'PASTILHA-001', 'Bosch', 20,
        80.00) ON CONFLICT (id) DO NOTHING;

-- POPULATE cadastro.servico_cadastro
INSERT INTO cadastro.servico_cadastro (id, nome, descricao, valor_referencia)
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Alinhamento', 'Alinhamento de direção', 120.00),
       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Troca de óleo', 'Troca de óleo do motor',
        90.00) ON CONFLICT (id) DO NOTHING;

-- POPULATE workorder.ordem_servico
INSERT INTO workorder.ordem_servico (id, cliente_id, veiculo_id, status, valor_total)
VALUES ('dddddddd-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111',
        '77777777-7777-7777-7777-777777777777', 'OPEN', 200.00) ON CONFLICT (id) DO NOTHING;

-- POPULATE workorder.ordem_servico_item_servico
INSERT INTO workorder.ordem_servico_item_servico (id, ordem_servico_id, servico_id, valor)
VALUES ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 120.00) ON CONFLICT (id) DO NOTHING;

-- POPULATE workorder.ordem_servico_item_peca
INSERT INTO workorder.ordem_servico_item_peca (id, ordem_servico_id, peca_id, quantidade, valor_unitario)
VALUES ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
        '99999999-9999-9999-9999-999999999999', 1, 50.00) ON CONFLICT (id) DO NOTHING;

-- Exemplo de inserção de usuário admin (senha: 'admin123' hashada com BCrypt)
INSERT INTO cadastro.usuario (id, email, senha, role)
VALUES ('11111111-1111-1111-1111-111111111111', 'admin@autoservice.local',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoO5rG6W1dQ6rQ/6bY8b5l1y3Zp6F6bY8G', 'ADMIN') ON CONFLICT (email) DO NOTHING;