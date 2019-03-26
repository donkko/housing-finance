package com.dongkwon.finance.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dongkwon.finance.controller.response.CommonResponse;
import com.dongkwon.finance.service.CsvService;
import com.dongkwon.finance.service.InstituteSupportBatchService;
import com.fasterxml.jackson.databind.MappingIterator;

@RestController
@RequestMapping("/file")
public class CsvController {
    private final CsvService csvService;
    private final InstituteSupportBatchService instituteSupportBatchService;

    public CsvController(CsvService csvService,
                         InstituteSupportBatchService instituteSupportBatchService) {
        this.csvService = csvService;
        this.instituteSupportBatchService = instituteSupportBatchService;
    }

    @PostMapping("/csv/upload")
    public CommonResponse<String> upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        final MappingIterator<String[]> csvIterator =
                csvService.readCsvWithHeader(multipartFile.getInputStream());
        instituteSupportBatchService.deleteCurrentDataAndSaveNewData(csvIterator);

        return CommonResponse.with("success");
    }
}
