package com.codeconnect.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @Schema(description = "Descrição de postagem usuário", example = "Bom dia rede, hoje quero compartilhar meu novo projeto.")
    @NotBlank(message = "Descrição de postagem do usuário não deve ser nulo ou vazio")
    private String descricao;

}