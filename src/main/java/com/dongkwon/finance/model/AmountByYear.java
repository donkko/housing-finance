package com.dongkwon.finance.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class AmountByYear {
    public static AmountByYear of(Integer year, Double totalAmount, Map<String, String> detailAmount) {
        return new AmountByYear(year + "년", String.valueOf(totalAmount.intValue()), detailAmount);
    }
    private String year;
    private String totalAmount;
    @JsonProperty("detail_amount")
    private Map<String, String> detailAmount;
}
