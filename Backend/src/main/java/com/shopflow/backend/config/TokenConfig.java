package com.shopflow.backend.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.shopflow.backend.entity.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Component
public class TokenConfig {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim("userId", user.getId().toString())
                .withSubject(user.getEmail())
                .withClaim("role",user.getRole().name())
                .withExpiresAt(Instant.now().plusSeconds(86400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decode = JWT.require(algorithm)
                    .build()
                    .verify(token);

            String userId = decode.getClaim("userId").asString();
            String email = decode.getSubject();
            String role = decode.getClaim("role").asString();

            if (userId == null || email == null || role == null) {
                return Optional.empty();
            }

            return Optional.of(JWTUserData.builder()
                    .userId(UUID.fromString(userId))
                    .email(email)
                    .Role(role)
                    .build());

        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }
}