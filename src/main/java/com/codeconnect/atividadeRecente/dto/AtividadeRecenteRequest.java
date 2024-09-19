package com.codeconnect.atividadeRecente.dto;

import com.codeconnect.atividadeRecente.enums.AtividadeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AtividadeRecenteRequest {

    @Schema(description = "ID do post relacionado à atividade", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID postId;

    @Schema(description = "Tipo de atividade realizada pelo usuário, como Curtida, Comentário ou Compartilhamento.")
    private AtividadeEnum atividadeEnum;

}