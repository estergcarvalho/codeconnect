package com.codeconnect.usuario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ErroAoCadastrarUsuarioException extends RuntimeException {

    public ErroAoCadastrarUsuarioException() {
        super("Erro ao cadastrar usuario");
    }
}
