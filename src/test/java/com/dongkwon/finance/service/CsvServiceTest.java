package com.dongkwon.finance.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.MappingIterator;

public class CsvServiceTest {
    private CsvService csvService;
    private File csvFile;

    @Before
    public void init() throws Exception {
        csvService = new CsvService();
        csvFile = ResourceUtils.getFile(getClass().getResource("/simpledata.csv"));
    }

    @Test
    public void readCsvWithHeaderTest() throws Exception {
        final MappingIterator<String[]> iterator = csvService.readCsvWithHeader(new FileInputStream(csvFile));

        final String[] firstRow = iterator.next();
        final String[] secondRow = iterator.next();
        final String[] thirdRow = iterator.next();
        final String[] fourthRow = iterator.next();

        // Empty line 은 제외되어야 한다
        assertThat(iterator.hasNext()).isFalse();

        final String[] firstRowExpected = {"연도 ", " 월 ", "", " A-은행 ", "B 기금", " ", ""};
        final String[] secondRowExpected = {"2005", "1", "", "846", "82", "9999", ""};
        final String[] thirdRowExpected = {"2017", "3", "", "1,991", "3,955", "9999", ""};
        final String[] fourthRowExpected = {"2017", "6", "", "3,287", "2,700", "9,999", ""};
        assertThat(firstRow).isEqualTo(firstRowExpected);
        assertThat(secondRow).isEqualTo(secondRowExpected);
        assertThat(thirdRow).isEqualTo(thirdRowExpected);
        assertThat(fourthRow).isEqualTo(fourthRowExpected);
    }
}
