package com.dongkwon.finance.controller.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class ErrorResponse {
    private final String message;
}
