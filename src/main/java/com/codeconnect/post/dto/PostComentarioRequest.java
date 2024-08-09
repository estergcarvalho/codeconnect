package com.codeconnect.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PostComentarioRequest {

    @Schema(description = "Id do post", example = "b2adf87b-b98a-49e3-af3f-57f8e5ac467d")
    private UUID id;

    @Schema(description = "Descrição de postagem usuário", example = "Bom dia rede, hoje quero compartilhar meu novo projeto.")
    @NotBlank(message = "Descrição de postagem do usuário não deve ser nulo ou vazio")
    private String descricao;

}