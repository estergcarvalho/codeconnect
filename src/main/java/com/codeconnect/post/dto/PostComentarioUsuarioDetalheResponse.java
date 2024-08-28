package com.codeconnect.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PostComentarioUsuarioDetalheResponse {

    @Schema(description = "Id comentário", example = "b2adf87b-b98a-49e3-af3f-57f8e5ac467d")
    private UUID id;

    @Schema(description = "Nome do usuário", example = "João")
    private String nome;

    @Schema(description = "Imagem do perfil do usuário", example = "R0lGODlhkAGQAfcAAAIAAQwBAQwECBMDAxoFAxwLBBwLDBY...")
    private String imagem;

    @Schema(description = "Tipo de imagem do perfil do usuário", example = "imagem/png")
    @JsonProperty("tipo_imagem")
    private String tipoImagem;

}