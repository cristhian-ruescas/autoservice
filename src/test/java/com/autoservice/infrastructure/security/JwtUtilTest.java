package com.autoservice.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        assertFalse(jwtUtil.isClientToken(token));
    }

    @Test
    @DisplayName("Reconhece token de cliente com claim CPF")
    void reconheceTokenClienteComCpf() {
        final var jwtUtil = new JwtUtil("segredo-local-com-tamanho-suficiente-para-testes");
        final var token = com.auth0.jwt.JWT.create()
                .withSubject("52998224725")
                .withClaim("cpf", "52998224725")
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 3_600_000))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("segredo-local-com-tamanho-suficiente-para-testes"));

        assertTrue(jwtUtil.isClientToken(token));
        assertTrue(jwtUtil.validateSignature(token));
        assertEquals("52998224725", jwtUtil.extractCpf(token));
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
