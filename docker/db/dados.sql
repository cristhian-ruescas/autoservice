-- Usuário para autenticação (ajuste o hash para um gerado por BCrypt)
INSERT INTO customer."user" (username, password) VALUES ('admin', '$2a$10$7QJQwQwQwQwQwQwQwQwQwOQwQwQwQwQwQwQwQwQwQwQwQwQwQwQwQwQwQwQw');
INSERT INTO customer.user_roles (user_id, role) VALUES (1, 'ADMIN');

-- Cliente Pessoa Física (CPF válido: 11144477735)
INSERT INTO cadastro.cliente (id, data_cadastro, pessoa_id)
VALUES ('1', CURRENT_DATE, '1');
INSERT INTO cadastro.pessoa (id, email, phone_number)
VALUES ('1', 'joao@email.com', '11999999999');
INSERT INTO cadastro.pessoa_fisica (id, nome, cpf)
VALUES ('1', 'João da Silva', '11144477735');

-- Cliente Pessoa Jurídica (CNPJ válido: 12345678000195)
INSERT INTO cadastro.cliente (id, data_cadastro, pessoa_id)
VALUES ('2', CURRENT_DATE, '2');
INSERT INTO cadastro.pessoa (id, email, phone_number)
VALUES ('2', 'empresa@email.com', '1133334444');
INSERT INTO cadastro.pessoa_juridica (id, razao_social, cnpj)
VALUES ('2', 'Empresa Exemplo Ltda', '12345678000195');

-- Veículo (placa válida: BRA2E19)
INSERT INTO customer.vehicle (id, plate, brand, model, manufacture_year, customer_id)
VALUES ('1', 'BRA2E19', 'Fiat', 'Uno', 2015, '1');

-- Peça
INSERT INTO catalog.part (id, name, quantity, unit_price)
VALUES ('1', 'Filtro de óleo', 10, 29.90);

-- Serviço
INSERT INTO catalog.service_catalog (id, name, description, base_price)
VALUES ('1', 'Troca de óleo', 'Troca de óleo do motor', 99.90);

-- Ordem de serviço
INSERT INTO workorder.workorder (id, customer_id, vehicle_id, status, creation_date)
VALUES ('1', '1', '1', 'ABERTA', CURRENT_DATE);

