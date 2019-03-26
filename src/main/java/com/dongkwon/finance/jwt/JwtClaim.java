package com.dongkwon.finance.jwt;

import java.time.Instant;

import lombok.Value;

@Value(staticConstructor = "of")
public class JwtClaim {
    private final String id;
    private final Instant expiresAt;
}
