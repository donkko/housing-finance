package com.dongkwon.finance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.dongkwon.finance.FinanceApplication;
import com.dongkwon.finance.controller.request.JwtRequest;
import com.dongkwon.finance.domain.Account;
import com.dongkwon.finance.exceptionhandler.ControllerExceptionHandler;
import com.dongkwon.finance.jwt.JwtAuthentication;
import com.dongkwon.finance.jwt.JwtClaim;
import com.dongkwon.finance.jwt.TokenProvider;
import com.dongkwon.finance.repository.AccountRepository;
import com.dongkwon.finance.service.CsvService;
import com.dongkwon.finance.service.InstituteSupportBatchService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FinanceApplication.class)
@Transactional
public class JwtControllerTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Autowired
    private JwtController jwtController;
    @Autowired
    private ControllerExceptionHandler exceptionHandler;
    @Autowired
    private CsvService csvService;
    @Autowired
    private InstituteSupportBatchService instituteSupportBatchService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenProvider tokenProvider;

    @Before
    public void setup() throws Exception {
        File csvFile = ResourceUtils.getFile(getClass().getResource("/simpledata.csv"));
        MappingIterator<String[]> csvIterator = csvService.readCsvWithHeader(new FileInputStream(csvFile));
        instituteSupportBatchService.setInstituteSupports(csvIterator);
        instituteSupportBatchService.calculateInstituteSupportSummaries();

        mockMvc = MockMvcBuilders.standaloneSetup(jwtController)
                                 .setControllerAdvice(exceptionHandler)
                                 .build();
    }

    @Test
    public void testSignUp() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setId("myId");
        request.setPw("myPw");

        mockMvc.perform(post("/api/jwt/signup")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").isString())
               .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void testSignUpDuplicatedId() throws Exception {
        Account account = new Account();
        account.setUserId("myId");
        account.setUserPasswordHash(passwordEncoder.encode("myPw"));
        accountRepository.save(account);

        JwtRequest request = new JwtRequest();
        request.setId("myId");
        request.setPw("myPw");

        mockMvc.perform(post("/api/jwt/signup")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().is4xxClientError());
    }

    @Test
    public void testSignIn() throws Exception {
        Account account = new Account();
        account.setUserId("myId");
        account.setUserPasswordHash(passwordEncoder.encode("myPw"));
        accountRepository.save(account);

        JwtRequest request = new JwtRequest();
        request.setId("myId");
        request.setPw("myPw");

        mockMvc.perform(post("/api/jwt/signin")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").isString())
               .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void testRefresh() throws Exception {
        JwtClaim jwtClaim = JwtClaim.of("myId", Instant.now());
        JwtAuthentication jwtAuthentication = new JwtAuthentication(jwtClaim);
        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);

        mockMvc.perform(post("/api/jwt/refresh"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").isString())
               .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
