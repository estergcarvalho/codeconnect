package com.codeconnect.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioPerfilDetalheResponse {

    @Schema(description = "Nome do usuário", example = "João")
    private String nome;

    @Schema(description = "Imagem do perfil usuário")
    private String imagem;

    @Schema(description = "Tipo de imagem do perfil do usuário", example = "imagem/png")
    @JsonProperty("tipo_imagem")
    private String tipoImagem;

}