package com.codeconnect.post.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UsuarioNaoAutorizadoParaCurtirException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        detalheProblema.setTitle("Usuário não tem permissão para curtir este post");

        return detalheProblema;
    }

}