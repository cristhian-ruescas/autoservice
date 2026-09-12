package com.autoservice.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil")
class JwtUtilTest {

    private static final String SECRET = "segredo-local-com-tamanho-suficiente-para-testes";

    @Test
    void aceitaTokenDaLambdaComCpfRolesIssuerEExpiracao() {
        final var jwtUtil = new JwtUtil(SECRET, "autoservice-auth");
        final var token = JWT.create()
                .withSubject("39053344705")
                .withIssuer("autoservice-auth")
                .withClaim("roles", List.of("CUSTOMER"))
                .withClaim("customer_status", "ATIVO")
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(SECRET));

        assertTrue(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.validateToken(token, "39053344705"));
        assertEquals("39053344705", jwtUtil.extractUsername(token));
        assertEquals("autoservice-auth", jwtUtil.extractIssuer(token));
        assertEquals(List.of("CUSTOMER"), jwtUtil.extractRoles(token));
        assertEquals("ATIVO", jwtUtil.extractCustomerStatus(token));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ausente", "nula", "expirada", "issuer-incorreto", "assinatura-incorreta"})
    void recusaTokenSemExpiracaoValidaOuSemEmissorConfiavel(final String caso) {
        final var jwtUtil = new JwtUtil(SECRET, "autoservice-auth");
        final var builder = JWT.create()
                .withSubject("39053344705")
                .withIssuer(caso.equals("issuer-incorreto") ? "outro-emissor" : "autoservice-auth");
        if (caso.equals("nula")) {
            builder.withNullClaim("exp");
        } else if (!caso.equals("ausente")) {
            builder.withExpiresAt(Instant.now().plusSeconds(caso.equals("expirada") ? -60 : 3600));
        }
        final var token = builder.sign(Algorithm.HMAC256(
                caso.equals("assinatura-incorreta") ? "outro-segredo" : SECRET));

        assertFalse(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.validateToken(token, "39053344705"));
        assertThrows(JWTVerificationException.class, () -> jwtUtil.extractUsername(token));
    }

    @Test
    @DisplayName("Gera token e extrai dados do usuário")
    void geraTokenEExtraiDados() {
        final var jwtUtil = new JwtUtil("segredo-local-com-tamanho-suficiente-para-testes");

        final var token = jwtUtil.generateToken("admin@autoservice.local");

        assertEquals("admin@autoservice.local", jwtUtil.extractUsername(token));
        assertNotNull(jwtUtil.extractExpiration(token));
        assertEquals("autoservice-auth", jwtUtil.extractIssuer(token));
        assertEquals(List.of(), jwtUtil.extractRoles(token));
        assertTrue(jwtUtil.validateToken(token, "admin@autoservice.local"));
        assertFalse(jwtUtil.validateToken(token, "outro@autoservice.local"));
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

    @Test
    void falhaSemIssuerConfigurado() {
        assertThrows(IllegalStateException.class, () -> new JwtUtil(SECRET, ""));
        assertThrows(IllegalStateException.class, () -> new JwtUtil(SECRET, null));
    }
}
