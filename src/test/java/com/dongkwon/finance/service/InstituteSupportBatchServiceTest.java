package com.dongkwon.finance.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.dongkwon.finance.FinanceApplication;
import com.dongkwon.finance.domain.Institute;
import com.dongkwon.finance.domain.Support;
import com.dongkwon.finance.domain.SupportSummary;
import com.dongkwon.finance.repository.InstituteRepository;
import com.dongkwon.finance.repository.SupportRepository;
import com.dongkwon.finance.repository.SupportSummaryRepository;
import com.fasterxml.jackson.databind.MappingIterator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FinanceApplication.class)
@Transactional
public class InstituteSupportBatchServiceTest {
    @Autowired
    InstituteSupportBatchService instituteSupportBatchService;
    @Autowired
    InstituteRepository instituteRepository;
    @Autowired
    SupportRepository supportRepository;
    @Autowired
    SupportSummaryRepository supportSummaryRepository;

    @Test
    public void setInstituteSupportsTest() throws Exception {
        // given
        CsvService csvService = new CsvService();
        File csvFile = ResourceUtils.getFile(getClass().getResource("/simpledata.csv"));
        MappingIterator<String[]> csvIterator = csvService.readCsvWithHeader(new FileInputStream(csvFile));

        // when
        instituteSupportBatchService.setInstituteSupports(csvIterator);

        // then
        List<Institute> institutes = instituteRepository.findAll();
        assertThat(institutes.size()).isEqualTo(2);
        assertThat(institutes.get(0).getName()).isEqualTo("A-은행");
        assertThat(institutes.get(1).getName()).isEqualTo("B 기금");

        List<Support> supports = supportRepository.findAll();
        assertThat(supports.size()).isEqualTo(6);
        assertThat(supports.get(0).getAmount()).isEqualTo(846.0);
        assertThat(supports.get(1).getAmount()).isEqualTo(82.0);
        assertThat(supports.get(2).getAmount()).isEqualTo(1991.0);
        assertThat(supports.get(3).getAmount()).isEqualTo(3955.0);
        assertThat(supports.get(4).getAmount()).isEqualTo(3287.0);
        assertThat(supports.get(5).getAmount()).isEqualTo(2700.0);
    }

    @Test
    public void saveInstitutesTest() {
        // given
        List<String> header = Arrays.asList("연도", "월", " 주택도시기금1)(억원)", "국민은행(억원)", "");

        // when
        List<Institute> institutes = instituteSupportBatchService.saveInstitutes(header);

        // then
        assertThat(institutes.get(0)).isNull();
        assertThat(institutes.get(1)).isNull();
        assertThat(institutes.get(2).getCode()).isPositive();
        assertThat(institutes.get(2).getName()).isEqualTo("주택도시기금1)");
        assertThat(institutes.get(3).getCode()).isPositive();
        assertThat(institutes.get(3).getName()).isEqualTo("국민은행");
        assertThat(institutes.get(4)).isNull();
    }

    @Test
    public void normalizeInstituteNameTest() {
        List<Pair<String, String>> namePairs = Arrays.asList(
                Pair.of("주택도시기금1)(억원)", "주택도시기금1)"),
                Pair.of("국민은행(억원)", "국민은행"),
                Pair.of("우리은행(억원)", "우리은행"),
                Pair.of("신한은행(억원)", "신한은행"),
                Pair.of("한국시티은행(억원)", "한국시티은행"),
                Pair.of("하나은행(억원)", "하나은행"),
                Pair.of("농협은행/수협은행(억원)", "농협은행/수협은행"),
                Pair.of("외환은행(억원)", "외환은행"),
                Pair.of("기타은행(억원)", "기타은행")
        );
        namePairs.forEach(namePair -> {
            assertThat(InstituteSupportBatchService.normalizeInstituteName(namePair.getFirst()))
                    .isEqualTo(namePair.getSecond());
        });
    }

    @Test
    public void calculateInstituteSupportSummariesTest() throws Exception {
        // given
        CsvService csvService = new CsvService();
        File csvFile = ResourceUtils.getFile(getClass().getResource("/simpledata.csv"));
        MappingIterator<String[]> csvIterator = csvService.readCsvWithHeader(new FileInputStream(csvFile));
        instituteSupportBatchService.setInstituteSupports(csvIterator);

        // when
        instituteSupportBatchService.calculateInstituteSupportSummaries();

        // then
        List<SupportSummary> supportSummaries = supportSummaryRepository.findAll();
        List<SupportSummary> sorted = supportSummaries.stream()
                                                      .sorted(Comparator.comparingDouble(SupportSummary::getSumAmount))
                                                      .collect(Collectors.toList());
        assertThat(sorted.get(0).getInstitute().getName()).isEqualTo("B 기금");
        assertThat(sorted.get(0).getYear()).isEqualTo(2005);
        assertThat(sorted.get(0).getSumAmount()).isEqualTo(82.0);
        assertThat(sorted.get(0).getAverageAmount()).isEqualTo(82.0);

        assertThat(sorted.get(1).getInstitute().getName()).isEqualTo("A-은행");
        assertThat(sorted.get(1).getYear()).isEqualTo(2005);
        assertThat(sorted.get(1).getSumAmount()).isEqualTo(846.0);
        assertThat(sorted.get(1).getAverageAmount()).isEqualTo(846.0);

        assertThat(sorted.get(2).getInstitute().getName()).isEqualTo("A-은행");
        assertThat(sorted.get(2).getYear()).isEqualTo(2017);
        assertThat(sorted.get(2).getSumAmount()).isEqualTo(5278.0);
        assertThat(sorted.get(2).getAverageAmount()).isEqualTo(2639.0);

        assertThat(sorted.get(3).getInstitute().getName()).isEqualTo("B 기금");
        assertThat(sorted.get(3).getYear()).isEqualTo(2017);
        assertThat(sorted.get(3).getSumAmount()).isEqualTo(6655.0);
        assertThat(sorted.get(3).getAverageAmount()).isEqualTo(3327.5);
    }
}
