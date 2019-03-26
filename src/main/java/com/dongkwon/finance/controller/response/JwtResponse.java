package com.dongkwon.finance.controller.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class JwtResponse {
    private String token;
}
