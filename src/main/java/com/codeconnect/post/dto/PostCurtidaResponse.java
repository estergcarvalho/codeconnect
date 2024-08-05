package com.codeconnect.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Builder
public class PostCurtidaResponse {

    @Schema(description = "Id da curtida" , example = "5af30329-1154-47de-8b70-8fdc660fa256")
    private UUID id;

    @Schema(description = "Id do post curtido" , example = "5af30329-1154-47de-8b70-8fdc660fa288")
    private String post;

    @Schema(description = "Usuário que curtiu o post" , example = "João")
    private String usuario;

    @Schema(description = "Data da curtida" , example = "2024-08-02T18:50:09.719+00:00")
    private Timestamp data;

}