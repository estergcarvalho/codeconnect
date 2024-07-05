package com.codeconnect.redesocial.dto;

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
public class RedeSocialRequest {

    @Schema(description = "Nome da rede social", example = "Github")
    @NotBlank(message = "Nome da rede social não deve ser nulo ou vazio")
    private String nome;

    @Schema(description = "Link da rede social", example = "https://www.github.com/joao")
    @NotBlank(message = "Link da rede social não deve ser nulo ou vazio")
    private String link;

}