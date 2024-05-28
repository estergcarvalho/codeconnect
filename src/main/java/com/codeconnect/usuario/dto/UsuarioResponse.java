package com.codeconnect.usuario.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UsuarioResponse {

    private UUID id;
    private String nome;
    private String email;

}