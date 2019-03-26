package com.dongkwon.finance.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PredictionRequest {
    private @NotBlank String bank;
    private @NotNull Integer month;
}
