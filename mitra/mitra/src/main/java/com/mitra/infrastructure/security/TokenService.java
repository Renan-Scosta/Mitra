package com.mitra.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
public class TokenService {

    @Value("${api.security.token.secret:my-super-secret-mitra-key}")
    private String secret;

    public String generateToken(String email, Long userId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("mitra-api")
                    .withSubject(email)
                    .withClaim("userId", userId)
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            log.info("JWT generated for email={} userId={}", email, userId);
            return token;
        } catch (JWTCreationException exception) {
            log.error("JWT generation error for email={} userId={}", email, userId, exception);
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("mitra-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            log.warn("JWT verification failed: {}", exception.getMessage());
            return "";
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
    }
}

