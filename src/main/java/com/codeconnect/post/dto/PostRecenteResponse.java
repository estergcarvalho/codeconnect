package com.codeconnect.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;

import java.sql.Timestamp;
import java.util.UUID;

public interface PostRecenteResponse {

    @Schema(description = "Id do post", example = "b2adf87b-b98a-49e3-af3f-57f8e5ac467d")
    UUID getId();

    @Schema(description = "Id usuário", example = "b2adf87b-b98a-49e3-af3f-57f8e5ac467d")
    @JsonProperty("id_usuario")
    @Column(name = "idusuario")
    UUID getIdUsuario();

    @Schema(description = "Nome usuario", example = "João")
    @JsonProperty("nome_usuario")
    @Column(name = "usuarionome")
    String getUsuarioNome();

    @Schema(description = "Descrição da postagem usuário", example = "Bom dia rede, hoje quero compartilhar meu novo projeto.")
    @Column(name = "descricao")
    String getDescricao();

    @Schema(description = "Data de criação postagem usuário", example = "2024-06-13T18:50:09.719+00:00")
    @Column(name = "datacriacao")
    @JsonProperty("data_criacao")
    Timestamp getDataCriacao();

}