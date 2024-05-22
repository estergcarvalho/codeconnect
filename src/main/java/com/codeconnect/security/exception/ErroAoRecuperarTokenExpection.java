package com.codeconnect.security.exception;

public class ErroAoRecuperarTokenExpection extends RuntimeException {

    public ErroAoRecuperarTokenExpection() {
        super("Erro ao recuperar token");
    }

}