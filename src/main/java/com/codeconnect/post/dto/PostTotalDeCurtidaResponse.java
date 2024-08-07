package com.codeconnect.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostTotalDeCurtidaResponse {

    @Schema(description = "Retorna a quantidade de curtidas do post", example = "10")
    private Long total;

}