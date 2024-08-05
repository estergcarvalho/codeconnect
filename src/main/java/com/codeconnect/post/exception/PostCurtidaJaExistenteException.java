package com.codeconnect.post.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class PostCurtidaJaExistenteException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        detalheProblema.setTitle("O usuário já curtiu esse Post");

        return detalheProblema;
    }

}