package com.autoservice.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    public static final String ISSUER_ADMIN = "autoservice-admin";
    public static final String ISSUER_AUTH = "autoservice-auth";
    public static final String CLAIM_CPF = "cpf";

    private static final long EXPIRATION_ADMIN_MS = 1000L * 60 * 60 * 6;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final Algorithm algorithm;

    public JwtUtil(@Value("${autoservice.jwt.secret:}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret key not set. Defina a propriedade autoservice.jwt.secret.");
        }
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(ISSUER_ADMIN)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_ADMIN_MS))
                .sign(algorithm);
    }

    public boolean validateToken(String token, String username) {
        try {
            final DecodedJWT jwt = decodeToken(token);
            if (isClienteToken(jwt)) {
                return false;
            }
            final String subject = jwt.getSubject();
            return subject.equals(username) && !isTokenExpired(jwt);
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateClienteToken(String token) {
        try {
            final DecodedJWT jwt = decodeToken(token);
            return isClienteToken(jwt) && !isTokenExpired(jwt) && jwt.getSubject() != null && !jwt.getSubject().isBlank();
        } catch (Exception e) {
            logger.warn("Cliente token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isClienteToken(DecodedJWT jwt) {
        return ISSUER_AUTH.equals(jwt.getIssuer());
    }

    public String extractUsername(String token) {
        return extractClaim(token, DecodedJWT::getSubject);
    }

    public String extractCpf(String token) {
        return extractClaim(token, jwt -> jwt.getClaim(CLAIM_CPF).asString());
    }

    public DecodedJWT decodeToken(String token) {
        final JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    public <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolver) {
        return claimsResolver.apply(decodeToken(token));
    }

    private boolean isTokenExpired(DecodedJWT jwt) {
        final Date expiration = jwt.getExpiresAt();
        return expiration != null && expiration.before(new Date());
    }
}
