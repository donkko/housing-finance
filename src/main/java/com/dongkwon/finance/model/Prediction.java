package com.dongkwon.finance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Prediction {
    private final String bank;
    @JsonFormat(shape = Shape.STRING)
    private final Integer year;
    @JsonFormat(shape = Shape.STRING)
    private final Integer month;
    @JsonFormat(shape = Shape.STRING)
    private final Integer amount;
}
