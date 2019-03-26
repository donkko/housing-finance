package com.dongkwon.finance.controller.response;

import java.util.List;

import com.dongkwon.finance.model.AmountByYear;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value(staticConstructor = "of")
public class AmountsByYearResponse {
    private final String name;
    @JsonProperty("amounts_by_year")
    private final List<AmountByYear> amountsByYear;
}
