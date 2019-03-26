package com.dongkwon.finance.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dongkwon.finance.controller.request.PredictionRequest;
import com.dongkwon.finance.controller.response.AmountsByYearResponse;
import com.dongkwon.finance.controller.response.MinMaxAmountResponse;
import com.dongkwon.finance.model.Prediction;
import com.dongkwon.finance.controller.response.TopBankResponse;
import com.dongkwon.finance.service.InstituteSupportService;

@RestController
@RequestMapping("/api/data")
public class DataController {
    public static final String OWEHWAN_INSTITUTE_NAME = "외환은행";
    private final InstituteSupportService instituteSupportService;

    public DataController(InstituteSupportService instituteSupportService) {
        this.instituteSupportService = instituteSupportService;
    }

    @GetMapping("/institutes")
    public List<String> getInstitutes() {
        return instituteSupportService.getAllInstituteNames();
    }

    @GetMapping("/amounts-by-year")
    public AmountsByYearResponse getAmountsByYear() {
        return AmountsByYearResponse.of(
                "주택금융 공급현황",
                instituteSupportService.getAmountsByYear()
        );
    }

    @GetMapping("/top-bank")
    public TopBankResponse getTopBank() {
        return TopBankResponse.from(instituteSupportService.getTopInstitute());
    }

    @GetMapping("/institutes/oehwan/min-max-amount")
    public MinMaxAmountResponse getMinMaxAmountOfOehwan() {
        return MinMaxAmountResponse.from(instituteSupportService.getMinMaxAmount(OWEHWAN_INSTITUTE_NAME));
    }

    @PostMapping("/predict2018")
    public Prediction predict2018(@Valid @RequestBody PredictionRequest request) {
        return instituteSupportService.predict2018(request.getBank(), request.getMonth());
    }
}
