package com.codeconnect.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UsuarioResponse {

    @Schema(description = "Id usuario", example = "1")
    private UUID id;

    @Schema(description = "Nome usuario", example = "Joao")
    private String nome;

    @Schema(description = "Email usuario", example = "joao@teste.com")
    private String email;

}