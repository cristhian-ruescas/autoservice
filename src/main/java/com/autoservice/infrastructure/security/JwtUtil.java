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
    private static final long EXPIRATION = 1000L * 60 * 60 * 6;
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
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION))
                .sign(algorithm);
    }

    public boolean validateToken(String token, String username) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String subject = jwt.getSubject();
            return subject != null && subject.equals(username) && !isTokenExpired(jwt);
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateSignature(String token) {
        try {
            decodeToken(token);
            return true;
        } catch (Exception e) {
            logger.warn("Token signature validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isClientToken(String token) {
        try {
            final DecodedJWT jwt = decodeToken(token);
            final String cpfClaim = jwt.getClaim("cpf").asString();
            if (cpfClaim != null && SecurityAuth.digitsOnly(cpfClaim).length() == 11) {
                return true;
            }
            final String subject = jwt.getSubject();
            return subject != null && SecurityAuth.digitsOnly(subject).length() == 11;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractCpf(String token) {
        final DecodedJWT jwt = decodeToken(token);
        final String cpfClaim = jwt.getClaim("cpf").asString();
        if (cpfClaim != null && !cpfClaim.isBlank()) {
            return SecurityAuth.digitsOnly(cpfClaim);
        }
        return SecurityAuth.digitsOnly(jwt.getSubject());
    }

    public String extractUsername(String token) {
        return extractClaim(token, DecodedJWT::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    public <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolver) {
        DecodedJWT jwt = decodeToken(token);
        return claimsResolver.apply(jwt);
    }

    private DecodedJWT decodeToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    private boolean isTokenExpired(DecodedJWT jwt) {
        Date expiration = jwt.getExpiresAt();
        return expiration != null && expiration.before(new Date());
    }
}
