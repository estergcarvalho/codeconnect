package com.codeconnect.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAmigoResponse {

    @Schema(description = "Lista de detalhes dos amigos", example = "Nome: João, idAmigo: 550e8400-e29b-41d4-a716-446655440000, statusRelacionamento: AMIGO")
    private List<UsuarioAmigoDetalheResponse> amigos;

    @Schema(description = "Total de amigos", example = "5")
    private int total;

}