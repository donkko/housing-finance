package com.dongkwon.finance.exception;

public class IdAlreadyUsedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IdAlreadyUsedException(String message) {
        super(message);
    }
}
