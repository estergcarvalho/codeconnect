package com.codeconnect.exceptionhandler;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(CodeConnectException.class)
    public ProblemDetail handleCodeConnectException(CodeConnectException exception) {
        return exception.handleProblemDetail();
    }

}
