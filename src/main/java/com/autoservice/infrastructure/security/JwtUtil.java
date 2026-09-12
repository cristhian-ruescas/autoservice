package com.autoservice.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final long EXPIRATION = 1000L * 60 * 60 * 6;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final Algorithm algorithm;
    private final String issuer;

    public JwtUtil(@Value("${autoservice.jwt.secret:}") String secret) {
        this(secret, "autoservice-auth");
    }

    @Autowired
    public JwtUtil(
            @Value("${autoservice.jwt.secret:}") String secret,
            @Value("${autoservice.jwt.issuer:autoservice-auth}") String issuer
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret key not set. Defina a propriedade autoservice.jwt.secret.");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalStateException("JWT issuer not set. Defina a propriedade autoservice.jwt.issuer.");
        }
        this.issuer = issuer;
    }

    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            return hasValidExpiration(decodeToken(token));
        } catch (JWTVerificationException e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            DecodedJWT jwt = decodeToken(token);
            String subject = jwt.getSubject();
            return subject != null && subject.equals(username) && hasValidExpiration(jwt);
        } catch (JWTVerificationException e) {
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

    public String extractIssuer(String token) {
        return extractClaim(token, DecodedJWT::getIssuer);
    }

    public String extractCustomerStatus(String token) {
        return extractClaim(token, jwt -> jwt.getClaim("customer_status").asString());
    }

    public List<String> extractRoles(String token) {
        Claim claim = decodeToken(token).getClaim("roles");
        List<String> roles = claim.asList(String.class);
        return roles == null ? List.of() : roles;
    }

    public <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolver) {
        DecodedJWT jwt = decodeToken(token);
        return claimsResolver.apply(jwt);
    }

    private DecodedJWT decodeToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withClaim("exp", (claim, jwt) -> claim.asDate() != null)
                .build();
        return verifier.verify(token);
    }

    private boolean hasValidExpiration(DecodedJWT jwt) {
        Date expiration = jwt.getExpiresAt();
        return expiration != null && expiration.after(new Date());
    }
}
