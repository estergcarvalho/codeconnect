package com.codeconnect.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

<<<<<<< HEAD
public abstract class CodeConnectException extends RuntimeException {
=======
public class CodeConnectException extends RuntimeException {
>>>>>>> e2508d003536e5a35df140c353a692b614c73a88

    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        detalheProblema.setTitle("CodeConnect Erro Interno");

        return detalheProblema;
    }

}