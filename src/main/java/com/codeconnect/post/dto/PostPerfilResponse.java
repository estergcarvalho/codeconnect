package com.codeconnect.post.dto;

import com.codeconnect.usuario.dto.UsuarioPerfilDetalheResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostPerfilResponse {

    @Schema(description = "Retorna dados dos usuário")
    private UsuarioPerfilDetalheResponse usuario;

    @Schema(description = "Retorna lista de posts do usuario")
    private List<PostPerfilDetalheResponse> posts;

}