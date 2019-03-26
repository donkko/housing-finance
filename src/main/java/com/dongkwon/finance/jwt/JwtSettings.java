package com.dongkwon.finance.jwt;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("jwt")
public class JwtSettings {
    private @NotBlank String secret;
    private @NotNull Long tokenValidityInSeconds;
}
