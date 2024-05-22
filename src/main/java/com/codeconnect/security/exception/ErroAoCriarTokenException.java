package com.codeconnect.security.exception;

public class ErroAoCriarTokenException extends RuntimeException {

    public ErroAoCriarTokenException() {
        super("Erro ao criar token");
    }

}