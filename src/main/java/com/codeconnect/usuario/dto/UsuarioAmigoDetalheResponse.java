package com.codeconnect.usuario.dto;

import com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAmigoDetalheResponse {

    @Schema(description = "Nome do amigo", example = "João")
    private String nome;

    @Schema(description = "ID do amigo", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID idAmigo;

    @Schema(description = "Status do relacionamento com o amigo", example = "AMIGO")
    private UsuarioAmigoStatusEnum statusRelacionamento;

}