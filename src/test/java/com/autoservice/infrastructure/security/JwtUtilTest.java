package com.autoservice.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.Instant;

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
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("segredo-local-com-tamanho-suficiente-para-testes"));

        assertEquals(List.of("CUSTOMER"), jwtUtil.extractRoles(token));
        assertEquals("autoservice-auth", jwtUtil.extractIssuer(token));
        assertEquals("ATIVO", jwtUtil.extractCustomerStatus(token));
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void recusaTokenSemExpiracao() {
        final var jwtUtil = new JwtUtil("segredo-teste", "autoservice-auth");
        final var token = JWT.create()
                .withSubject("39053344705")
                .withIssuer("autoservice-auth")
                .sign(Algorithm.HMAC256("segredo-teste"));

        assertFalse(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.validateToken(token, "39053344705"));
        assertThrows(JWTVerificationException.class, () -> jwtUtil.extractUsername(token));
    }

    @Test
    void recusaTokenComExpiracaoNula() {
        final var jwtUtil = new JwtUtil("segredo-teste", "autoservice-auth");
        final var token = JWT.create()
                .withSubject("39053344705")
                .withIssuer("autoservice-auth")
                .withNullClaim("exp")
                .sign(Algorithm.HMAC256("segredo-teste"));

        assertFalse(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.validateToken(token, "39053344705"));
        assertThrows(JWTVerificationException.class, () -> jwtUtil.extractUsername(token));
    }

    @Test
    void recusaTokenExpirado() {
        final var jwtUtil = new JwtUtil("segredo-teste", "autoservice-auth");
        final var token = JWT.create()
                .withSubject("39053344705")
                .withIssuer("autoservice-auth")
                .withExpiresAt(Instant.now().minusSeconds(60))
                .sign(Algorithm.HMAC256("segredo-teste"));

        assertFalse(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.validateToken(token, "39053344705"));
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
