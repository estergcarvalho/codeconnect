package com.codeconnect.security.exception;

public class ErroConfiguracaoSegurancaException extends RuntimeException{

    public ErroConfiguracaoSegurancaException() {
        super("Erro na configuração da cadeia de filtros de segurança");
    }

}