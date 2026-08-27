package com.autoservice.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil")
class JwtUtilTest {

    private static final String SECRET = "segredo-local-com-tamanho-suficiente-para-testes";

    @Test
    @DisplayName("Gera token admin com issuer e extrai dados do usuário")
    void geraTokenEExtraiDados() {
        final var jwtUtil = new JwtUtil(SECRET);

        final var token = jwtUtil.generateToken("admin@autoservice.local");

        assertEquals("admin@autoservice.local", jwtUtil.extractUsername(token));
        assertNotNull(jwtUtil.extractExpiration(token));
        assertEquals(JwtUtil.ISSUER_ADMIN, jwtUtil.decodeToken(token).getIssuer());
        assertTrue(jwtUtil.validateToken(token, "admin@autoservice.local"));
        assertFalse(jwtUtil.validateToken(token, "outro@autoservice.local"));
        assertFalse(jwtUtil.isClienteToken(jwtUtil.decodeToken(token)));
    }

    @Test
    @DisplayName("Valida token de cliente com iss autoservice-auth")
    void validaTokenCliente() {
        final var jwtUtil = new JwtUtil(SECRET);
        final var token = JWT.create()
                .withSubject("cliente-id-1")
                .withIssuer(JwtUtil.ISSUER_AUTH)
                .withClaim(JwtUtil.CLAIM_CPF, "52998224725")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3_600_000))
                .sign(Algorithm.HMAC256(SECRET));

        assertTrue(jwtUtil.validateClienteToken(token));
        assertTrue(jwtUtil.isClienteToken(jwtUtil.decodeToken(token)));
        assertEquals("52998224725", jwtUtil.extractCpf(token));
        assertFalse(jwtUtil.validateToken(token, "cliente-id-1"));
    }

    @Test
    @DisplayName("Recusa token inválido")
    void recusaTokenInvalido() {
        final var jwtUtil = new JwtUtil(SECRET);

        assertFalse(jwtUtil.validateToken("token-invalido", "admin@autoservice.local"));
        assertFalse(jwtUtil.validateClienteToken("token-invalido"));
    }

    @Test
    @DisplayName("Falha sem secret configurada")
    void falhaSemSecretConfigurada() {
        assertThrows(IllegalStateException.class, () -> new JwtUtil(""));
        assertThrows(IllegalStateException.class, () -> new JwtUtil(null));
    }
}
