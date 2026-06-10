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
            return subject.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
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

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }
}
