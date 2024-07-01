package com.codeconnect.usuario.dto;

import com.codeconnect.redesocial.dto.RedeSocialRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UsuarioEditarResquest {

    @Schema(description = "Profissão do usuário", example = "Engenheiro de Software")
    private String profissao;

    @Schema(description = "País do usuário", example = "Brasil")
    private String pais;

    @Schema(description = "Estado do usuário", example = "São Paulo")
    private String estado;

    @Schema(description = "Redes sociais do usuário", example = "GitHub")
    @JsonProperty("redes_sociais")
    private List<RedeSocialRequest> redesSociais;

}