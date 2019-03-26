package com.dongkwon.finance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.dongkwon.finance.FinanceApplication;
import com.dongkwon.finance.exceptionhandler.ControllerExceptionHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FinanceApplication.class)
public class CsvControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private CsvController csvController;
    @Autowired
    private ControllerExceptionHandler exceptionHandler;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(csvController)
                                 .setControllerAdvice(exceptionHandler)
                                 .build();
    }

    @Test
    @Transactional
    public void testUpload() throws Exception {
        File csvFile = ResourceUtils.getFile(getClass().getResource("/simpledata.csv"));
        MockMultipartFile multipartFile = new MockMultipartFile("file", new FileInputStream(csvFile));

        mockMvc.perform(
                multipart("/file/csv/upload").file(multipartFile))
               .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testUploadWithWrongParamName() throws Exception {
        File csvFile = ResourceUtils.getFile(getClass().getResource("/simpledata.csv"));
        MockMultipartFile multipartFile = new MockMultipartFile("file2", new FileInputStream(csvFile));

        mockMvc.perform(
                multipart("/file/csv/upload").file(multipartFile))
               .andExpect(status().is4xxClientError());
    }
}
