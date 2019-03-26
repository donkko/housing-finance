package com.dongkwon.finance.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.ibm.icu.text.CharsetDetector;

@Service
public class CsvService {
    private final CsvMapper mapper;

    public CsvService() {
        mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
    }

    public MappingIterator<String[]> readCsvWithHeader(InputStream inputStream) throws IOException {
        final CharsetDetector detector = new CharsetDetector();
        final Reader unicodeReader = detector.getReader(new BufferedInputStream(inputStream), null);
        return mapper.readerFor(String[].class).readValues(unicodeReader);
    }
}
