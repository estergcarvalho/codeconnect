package com.codeconnect.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UsuarioResponse {

    @Schema(description = "Id usuário", example = "b2adf87b-b98a-49e3-af3f-57f8e5ac467d")
    private UUID id;

    @Schema(description = "Nome usuário", example = "Joao")
    private String nome;

    @Schema(description = "Email usuário", example = "joao@teste.com")
    private String email;

    @Schema(description = "Imagem do usuário", example = "iVBORw0KGgoAAAANSUhEUgAAAlgAAAGECAMAAADd...")
    private String imagem;

    @JsonProperty("tipo_imagem")
    @Schema(description = "Formato da imagem", example = "imagem/jpg")
    private String tipoImagem;

}