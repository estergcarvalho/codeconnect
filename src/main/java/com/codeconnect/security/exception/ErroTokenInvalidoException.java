package com.codeconnect.security.exception;

public class ErroTokenInvalidoException extends RuntimeException {

    public ErroTokenInvalidoException() {
        super("Token invalido");
    }

}