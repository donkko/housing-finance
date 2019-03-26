package com.dongkwon.finance.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class TokenProviderTest {
    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;
    private TokenProvider tokenProvider;

    @Before
    public void init() {
        final JwtSettings jwtSettings = new JwtSettings();
        jwtSettings.setSecret("superSuperSecret");
        jwtSettings.setTokenValidityInSeconds(86400L);

        algorithm = Algorithm.HMAC256(jwtSettings.getSecret());
        jwtVerifier = JWT.require(algorithm).build();
        tokenProvider = new TokenProvider(jwtSettings);
    }

    @Test
    public void createTokenTest() {
        // when
        String token = tokenProvider.generateToken("aaa");

        // then
        final DecodedJWT decodedJwt =jwtVerifier.verify(token);
        final String id = decodedJwt.getClaim(TokenProvider.CLAIM_NAME_ID).asString();
        final Instant expiresAt = decodedJwt.getExpiresAt().toInstant();
        assertThat(id).isEqualTo("aaa");
        assertThat(expiresAt.isAfter(Instant.now())).isTrue();
    }

    @Test(expected = TokenExpiredException.class)
    public void verifyExpiredTokenTest() {
        // given
        String expiredToken = JWT.create()
                                 .withClaim(TokenProvider.CLAIM_NAME_ID, "bbb")
                                 .withExpiresAt(Date.from(Instant.now().minusSeconds(1L)))
                                 .sign(algorithm);

        // when
        tokenProvider.verifyToken(expiredToken);

        // then
        // Expect TokenExpiredException to be thrown
    }
}
