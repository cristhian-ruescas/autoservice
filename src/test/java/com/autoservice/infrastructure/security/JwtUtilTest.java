package com.autoservice.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil")
class JwtUtilTest {

    @Test
    @DisplayName("Gera token e extrai dados do usuário")
    void geraTokenEExtraiDados() {
        final var jwtUtil = new JwtUtil("segredo-local-com-tamanho-suficiente-para-testes");

        final var token = jwtUtil.generateToken("admin@autoservice.local");

        assertEquals("admin@autoservice.local", jwtUtil.extractUsername(token));
        assertNotNull(jwtUtil.extractExpiration(token));
        assertTrue(jwtUtil.validateToken(token, "admin@autoservice.local"));
        assertFalse(jwtUtil.validateToken(token, "outro@autoservice.local"));
    }

    @Test
    @DisplayName("Extrai roles e issuer de token emitido pela Lambda")
    void extraiRolesEIssuer() {
        final var jwtUtil = new JwtUtil("segredo-local-com-tamanho-suficiente-para-testes", "autoservice-auth");

        final var token = com.auth0.jwt.JWT.create()
                .withSubject("39053344705")
                .withClaim("cpf", "39053344705")
                .withClaim("customer_status", "ATIVO")
                .withClaim("roles", List.of("CUSTOMER"))
                .withIssuer("autoservice-auth")
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("segredo-local-com-tamanho-suficiente-para-testes"));

        assertEquals(List.of("CUSTOMER"), jwtUtil.extractRoles(token));
        assertEquals("autoservice-auth", jwtUtil.extractIssuer(token));
        assertEquals("ATIVO", jwtUtil.extractCustomerStatus(token));
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("Recusa token inválido")
    void recusaTokenInvalido() {
        final var jwtUtil = new JwtUtil("segredo-local-com-tamanho-suficiente-para-testes");

        assertFalse(jwtUtil.validateToken("token-invalido", "admin@autoservice.local"));
    }

    @Test
    @DisplayName("Falha sem secret configurada")
    void falhaSemSecretConfigurada() {
        assertThrows(IllegalStateException.class, () -> new JwtUtil(""));
        assertThrows(IllegalStateException.class, () -> new JwtUtil(null));
    }
}
