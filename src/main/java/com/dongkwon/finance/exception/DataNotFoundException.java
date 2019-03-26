package com.dongkwon.finance.exception;

public class DataNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException() {
        super("데이터가 없습니다.");
    }
}
