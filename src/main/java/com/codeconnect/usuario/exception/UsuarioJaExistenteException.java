package com.codeconnect.usuario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UsuarioJaExistenteException extends RuntimeException {

    public UsuarioJaExistenteException() {
        super("Usuario já existente");
    }

}