package com.codeconnect.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CodeConnectException extends RuntimeException {

    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        detalheProblema.setTitle("CodeConnect Erro Interno");

        return detalheProblema;
    }

}