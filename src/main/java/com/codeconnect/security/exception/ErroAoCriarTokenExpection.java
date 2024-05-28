package com.codeconnect.security.exception;

public class ErroAoCriarTokenExpection extends RuntimeException {

    public ErroAoCriarTokenExpection() {
        super("Erro ao recuperar token");
    }

}