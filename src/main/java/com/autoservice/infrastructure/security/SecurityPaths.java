package com.autoservice.infrastructure.security;

public final class SecurityPaths {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/health",
            "/live",
            "/ready",
            "/actuator/health",
            "/actuator/health/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public static final String ATENDIMENTO_ABERTURA = "/atendimentos";
    public static final String CLIENTE_BY_CPF = "/clientes/cpf/*";
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
