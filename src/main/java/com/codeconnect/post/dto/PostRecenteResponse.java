package com.codeconnect.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;

import java.sql.Timestamp;
import java.util.UUID;

public interface PostRecenteResponse {

    @Schema(description = "Id do post", example = "b2adf87b-b98a-49e3-af3f-57f8e5ac467d")
    UUID getId();

    @Schema(description = "Descrição da postagem usuário", example = "Bom dia rede, hoje quero compartilhar meu novo projeto.")
    @Column(name = "descricao")
    String getDescricao();

    @Schema(description = "Data de criação postagem usuário", example = "2024-06-13T18:50:09.719+00:00")
    @Column(name = "datacriacao")
    @JsonProperty("data_criacao")
    Timestamp getDataCriacao();

    @Schema(description = "Id usuário", example = "b2adf87b-b98a-49e3-af3f-57f8e5ac467d")
    @JsonProperty("id_usuario")
    @Column(name = "idusuario")
    UUID getIdUsuario();

    @Schema(description = "Nome usuário", example = "João")
    @JsonProperty("nome_usuario")
    @Column(name = "usuarionome")
    String getUsuarioNome();

    @Schema(description = "Profissão do usuário", example = "Desenvolvedor")
    @Column(name = "profissao")
    String getProfissao();

    @Schema(description = "Imagem perfil do usuário", example = "iVBORw0KGgoAAAANSUhEUgAAAlgAAAGECAMAAADd...")
    @Column(name = "imagem")
    String getImagem();

    @Schema(description = "Tipo de imagem do perfil do usuário", example = "imagem/png")
    @JsonProperty("tipo_imagem")
    @Column(name = "tipo_imagem")
    String getTipoImagem();

    @Schema(description = "Retorna verdadeiro se o post foi curtido pelo usuário logado", example = "true")
    boolean getCurtido();

    @Schema(description = "Retorna o total de curtidas do post", example = "10")
    Long getTotalCurtidas();

    @Schema(description = "Retorna o total de comentários do post", example = "28")
    Long getTotalComentarios();

}