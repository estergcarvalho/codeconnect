package com.codeconnect.usuario.dto;

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

    private List<UsuarioAmigoDetalheResponse> amigos;
    private int total;

}