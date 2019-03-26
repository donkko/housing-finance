package com.dongkwon.finance.controller.response;

import com.dongkwon.finance.domain.SupportSummary;

import lombok.Value;

@Value
public class TopBankResponse {
    public static TopBankResponse from(SupportSummary supportSummary) {
        return new TopBankResponse(
                String.valueOf(supportSummary.getYear()),
                supportSummary.getInstitute().getName()
        );
    }
    private final String year;
    private final String bank;
}
