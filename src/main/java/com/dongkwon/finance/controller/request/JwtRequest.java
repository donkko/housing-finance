package com.dongkwon.finance.controller.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class JwtRequest {
    private @NotBlank String id;
    private @NotBlank String pw;
}
