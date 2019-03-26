package com.dongkwon.finance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.dongkwon.finance.FinanceApplication;
import com.dongkwon.finance.controller.request.PredictionRequest;
import com.dongkwon.finance.exceptionhandler.ControllerExceptionHandler;
import com.dongkwon.finance.service.CsvService;
import com.dongkwon.finance.service.InstituteSupportBatchService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FinanceApplication.class)
@Transactional
public class DataControllerTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Autowired
    private DataController dataController;
    @Autowired
    private ControllerExceptionHandler exceptionHandler;
    @Autowired
    private CsvService csvService;
    @Autowired
    private InstituteSupportBatchService instituteSupportBatchService;

    @Before
    public void setup() throws Exception {
        File csvFile = ResourceUtils.getFile(getClass().getResource("/simpledata.csv"));
        MappingIterator<String[]> csvIterator = csvService.readCsvWithHeader(new FileInputStream(csvFile));
        instituteSupportBatchService.setInstituteSupports(csvIterator);
        instituteSupportBatchService.calculateInstituteSupportSummaries();

        mockMvc = MockMvcBuilders.standaloneSetup(dataController)
                                 .setControllerAdvice(exceptionHandler)
                                 .build();
    }

    @Test
    public void testGetInstitutes() throws Exception {
        mockMvc.perform(get("/api/data/institutes"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0]").value("A-은행"))
               .andExpect(jsonPath("$[1]").value("B 기금"));
    }

    @Test
    public void testGetAmountsByYear() throws Exception {
        mockMvc.perform(get("/api/data/amounts-by-year"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("주택금융 공급현황"))
               .andExpect(jsonPath("$.amounts_by_year").isArray())
               .andExpect(jsonPath("$.amounts_by_year[0].year").value("2005년"))
               .andExpect(jsonPath("$.amounts_by_year[0].totalAmount").value("928"))
               .andExpect(jsonPath("$.amounts_by_year[0].detail_amount").exists())
               .andExpect(jsonPath("$.amounts_by_year[1].year").value("2017년"))
               .andExpect(jsonPath("$.amounts_by_year[1].totalAmount").value("11933"))
               .andExpect(jsonPath("$.amounts_by_year[1].detail_amount").exists());
    }

    @Test
    public void testGetTopBank() throws Exception {
        mockMvc.perform(get("/api/data/top-bank"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.year").value("2017"))
               .andExpect(jsonPath("$.bank").value("B 기금"));
    }

    @Test
    public void testPredict2018() throws Exception {
        PredictionRequest request = new PredictionRequest();
        request.setBank("B 기금");
        request.setMonth(3);

        mockMvc.perform(post("/api/data/predict2018")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.bank").isString())
               .andExpect(jsonPath("$.bank").isNotEmpty())
               .andExpect(jsonPath("$.year").isString())
               .andExpect(jsonPath("$.year").isNotEmpty())
               .andExpect(jsonPath("$.month").isString())
               .andExpect(jsonPath("$.month").isNotEmpty())
               .andExpect(jsonPath("$.amount").isString())
               .andExpect(jsonPath("$.amount").isNotEmpty());
    }
}
