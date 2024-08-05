package com.codeconnect.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostQuantidadeDeCurtidaResponse {

    @Schema(description = "Retorna a quantidade de curtida do post", example = "10")
    @JsonProperty("quantidade_curtida")
    private Long quantidadeCurtida;

}