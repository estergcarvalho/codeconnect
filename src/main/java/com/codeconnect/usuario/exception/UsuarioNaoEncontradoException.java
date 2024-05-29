package com.codeconnect.usuario.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UsuarioNaoEncontradoException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        detalheProblema.setTitle("Usuário não encontrado");

        return detalheProblema;
    }

}