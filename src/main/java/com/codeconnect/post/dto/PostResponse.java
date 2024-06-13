package com.codeconnect.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private UUID id;

    @JsonProperty("id_usuario")
    private UUID idUsuario;

    @JsonProperty("data_criacao")
    private Timestamp dataCriacao;

    private String descricao;

}