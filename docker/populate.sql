-- Usuario admin local para testar autenticacao JWT.
-- Senha: admin123

INSERT INTO cadastro.usuario (id, email, senha, role)
VALUES ('11111111-1111-1111-1111-111111111111',
        'admin@autoservice.local',
        '$2a$10$XZMSbuJY22lBx/ioRaGjr.TRuU/6hvj5CdX9KJurzgKTisbWi8a42',
        'ADMIN')
ON CONFLICT (email) DO UPDATE
SET senha = EXCLUDED.senha,
    role  = EXCLUDED.role;
