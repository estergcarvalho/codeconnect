package com.codeconnect.security.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ErroTokenInvalidoException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        detalheProblema.setTitle("Token invalido");

        return detalheProblema;
    }
}