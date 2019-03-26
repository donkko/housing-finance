package com.dongkwon.finance.controller.response;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.util.Pair;

import com.dongkwon.finance.domain.SupportSummary;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class MinMaxAmountResponse {
    public static MinMaxAmountResponse from(Pair<SupportSummary, SupportSummary> minMaxPair) {
        final SupportSummary minEntity = minMaxPair.getFirst();
        final SupportSummary maxEntity = minMaxPair.getSecond();

        final List<YearAmount> yearAmounts = Arrays.asList(
                new YearAmount(
                        String.valueOf(minEntity.getYear()),
                        minEntity.getAverageAmountIntStr()
                ),
                new YearAmount(
                        String.valueOf(maxEntity.getYear()),
                        maxEntity.getAverageAmountIntStr()
                )
        );

        return new MinMaxAmountResponse(minEntity.getInstitute().getName(), yearAmounts);
    }

    private final String bank;
    @JsonProperty("support_amount")
    private final List<YearAmount> supportAmount;

    @Value
    public static class YearAmount {
        private String year;
        private String amount;
    }
}
