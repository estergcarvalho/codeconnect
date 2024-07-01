package com.codeconnect.usuario.dto;

import com.codeconnect.redesocial.dto.RedeSocialResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UsuarioEditarResponse {

    @Schema(description = "Profissão do usuário", example = "Engenheiro de Software")
    private String profissao;

    @Schema(description = "País do usuário", example = "Brasil")
    private String pais;

    @Schema(description = "Estado do usuário", example = "São Paulo")
    private String estado;

    @Schema(description = "Redes sociais do usuário", example = "GitHub")
    private List<RedeSocialResponse> redesSociais;

}