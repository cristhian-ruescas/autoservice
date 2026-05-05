package com.autoservice.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void geraTokenEPermiteExtrairSubject() {
        UserDetails user = User.withUsername("maria").password("x").authorities("ROLE_USER").build();
        String token = jwtUtil.generateToken(user);
        assertEquals("maria", jwtUtil.extractUsername(token));
    }

    @Test
    void tokenValidoQuandoUsuarioConfereENaoExpirou() {
        UserDetails user = User.withUsername("joao").password("x").authorities("ROLE_USER").build();
        String token = jwtUtil.generateToken(user);
        assertTrue(jwtUtil.isTokenValid(token, user));
    }

    @Test
    void tokenInvalidoQuandoUsuarioDiferente() {
        UserDetails geradoPara = User.withUsername("a").password("x").authorities("ROLE_USER").build();
        UserDetails outro = User.withUsername("b").password("x").authorities("ROLE_USER").build();
        String token = jwtUtil.generateToken(geradoPara);
        assertFalse(jwtUtil.isTokenValid(token, outro));
    }

    @Test
    void tokenComChaveErrada_disparaAoValidarOuExtrairSubject() {
        UserDetails user = User.withUsername("exp").password("x").authorities("ROLE_USER").build();
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(SignatureAlgorithm.HS256, "outra-chave")
                .compact();
        assertThrows(SignatureException.class, () -> jwtUtil.extractUsername(token));
        assertThrows(SignatureException.class, () -> jwtUtil.isTokenValid(token, user));
    }
}
