package com.codeconnect.post.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class PostCurtidaNaoEncontradaException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        detalheProblema.setTitle("Curtida do post não encontrado");

        return detalheProblema;
    }

}