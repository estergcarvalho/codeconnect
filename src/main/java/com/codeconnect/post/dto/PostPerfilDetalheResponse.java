package com.codeconnect.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PostPerfilDetalheResponse {

    @Schema(description = "Id usuário", example = "b2adf87b-b98a-49e3-af3f-57f8e5ac467d")
    private UUID id;

    @Schema(description = "Descrição da postagem usuário", example = "Bom dia rede, hoje quero compartilhar meu novo projeto.")
    private String descricao;

    @Schema(description = "Data de criação postagem usuário", example = "2024-06-13T18:50:09.719+00:00")
    @JsonProperty("data_criacao")
    private Timestamp dataCriacao;

    @Schema(description = "Retorna verdadeiro se o post foi curtido pelo usuário logado", example = "true")
    private boolean curtido;

    @Schema(description = "Retorna o total de curtidas do post", example = "10")
    private PostTotalDeCurtidaResponse totalCurtidas;

    @Schema(description = "Retorna o total de comentários do post", example = "28")
    private PostTotalDeComentarioResponse totalComentarios;

    @Schema(description = "Lista de comentários do post")
    private List<PostComentarioResponse> comentarios;

}