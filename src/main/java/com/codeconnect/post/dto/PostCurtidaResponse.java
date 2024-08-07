package com.codeconnect.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PostCurtidaResponse {

    @Schema(description = "Id da curtida" , example = "5af30329-1154-47de-8b70-8fdc660fa256")
    private UUID id;

}