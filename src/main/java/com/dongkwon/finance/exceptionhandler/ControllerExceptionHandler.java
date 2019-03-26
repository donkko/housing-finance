package com.dongkwon.finance.exceptionhandler;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.dongkwon.finance.controller.response.ErrorResponse;
import com.dongkwon.finance.exception.IdAlreadyUsedException;
import com.dongkwon.finance.exception.InvalidTokenException;
import com.dongkwon.finance.exception.TokenNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({
            TokenNotFoundException.class,
            InvalidTokenException.class
    })
    protected ResponseEntity<Object> handleJwtAuthException(HttpServletRequest request, Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ErrorResponse.of(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IdAlreadyUsedException.class)
    protected ResponseEntity<Object> handleConflictError(HttpServletRequest request, Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ErrorResponse.of(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ErrorResponse.of(ex.getMessage()), headers, status);
    }
}
