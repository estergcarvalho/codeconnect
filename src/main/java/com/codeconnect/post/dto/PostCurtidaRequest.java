package com.codeconnect.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCurtidaRequest {

   @Schema(description ="Id do post do usuário" , example = "5af30329-1154-47de-8b70-8fdc660fa256")
   @NotBlank(message = "O id do post não deve ser nulo ou vazio")
   @JsonProperty("post_id")
   private UUID postId;

}
