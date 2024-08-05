package com.codeconnect.post.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UsuarioNaoAutorizadoParaRemoverCurtidaException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        detalheProblema.setTitle("Usuário não tem permissão para remover a curtida do post");

        return detalheProblema;
    }

}