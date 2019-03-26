package com.dongkwon.finance.exception;

public class CsvParsingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CsvParsingException(String message) {
        super(message);
    }
}
