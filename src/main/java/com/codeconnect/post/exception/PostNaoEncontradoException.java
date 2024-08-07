package com.codeconnect.post.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class PostNaoEncontradoException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        detalheProblema.setTitle("Post não encontrado");

        return detalheProblema;
    }

}