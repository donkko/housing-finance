package com.dongkwon.finance.jwt;

import java.time.Instant;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class TokenProvider {
    public static final String CLAIM_NAME_ID = "id";

    private final Long tokenValidityInSeconds;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;

    public TokenProvider(JwtSettings jwtSettings) {
        tokenValidityInSeconds = jwtSettings.getTokenValidityInSeconds();
        algorithm = Algorithm.HMAC256(jwtSettings.getSecret());
        jwtVerifier = JWT.require(algorithm).build();
    }

    public String generateToken(String id) {
        final Long now = Instant.now().getEpochSecond();
        final Instant expiresAt = Instant.ofEpochSecond(now + tokenValidityInSeconds);

        return JWT.create()
                  .withClaim(CLAIM_NAME_ID, id)
                  .withExpiresAt(Date.from(expiresAt))
                  .sign(algorithm);
    }

    public JwtClaim verifyToken(String token) {
        final DecodedJWT decodedJwt = jwtVerifier.verify(token);
        final String id = decodedJwt.getClaim(CLAIM_NAME_ID).asString();
        final Instant expiresAt = decodedJwt.getExpiresAt().toInstant();

        return JwtClaim.of(id, expiresAt);
    }
}
