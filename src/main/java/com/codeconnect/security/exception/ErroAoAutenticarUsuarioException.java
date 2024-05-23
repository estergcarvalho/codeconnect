package com.codeconnect.security.exception;

public class ErroAoAutenticarUsuarioException extends RuntimeException {

    public ErroAoAutenticarUsuarioException() {
        super("Erro ao autenticar o usuario");
    }
}
