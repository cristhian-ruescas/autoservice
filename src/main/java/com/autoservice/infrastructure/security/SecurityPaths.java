package com.autoservice.infrastructure.security;

public final class SecurityPaths {

    public static final String ROLE_ADMIN = "ADMIN";

    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public static final String ANDAMENTO_ORDEM_SERVICO = "/ordens-servico/*/andamento";
    public static final String APROVACAO_APROVAR = "/ordens-servico/*/aprovacao/aprovar";
    public static final String APROVACAO_REPROVAR = "/ordens-servico/*/aprovacao/reprovar";

    public static final String[] ADMIN_ENDPOINTS = {
            "/atendimentos/**",
            "/ordens-servico/**",
            "/ordens-compra/**",
            "/clientes/**",
            "/veiculos/**",
            "/pecas/**",
            "/servicos/**",
            "/tipos-veiculo/**",
            "/estoques/**",
            "/integracoes/**"
    };

    private SecurityPaths() {
    }
}
