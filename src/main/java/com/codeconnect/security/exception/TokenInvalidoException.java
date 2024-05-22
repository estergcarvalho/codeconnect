package com.codeconnect.security.exception;

public class TokenInvalidoException extends RuntimeException {

    public TokenInvalidoException() {
        super("Token invalido");
    }

}