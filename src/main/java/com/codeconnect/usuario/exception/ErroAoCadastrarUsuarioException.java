package com.codeconnect.usuario.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ErroAoCadastrarUsuarioException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        detalheProblema.setTitle("Erro ao cadastrar usuário");

        return detalheProblema;
    }

}