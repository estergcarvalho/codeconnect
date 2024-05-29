package com.codeconnect.usuario.exception;

import com.codeconnect.exceptionhandler.CodeConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UsuarioJaExistenteException extends CodeConnectException {

    @Override
    public ProblemDetail handleProblemDetail() {
        ProblemDetail detalheProblema = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        detalheProblema.setTitle("Usuário já existente");

        return detalheProblema;
    }

}