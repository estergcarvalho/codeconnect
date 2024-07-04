package com.codeconnect.usuario.dto;

import com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPerfilRequest {

    @Schema(description = "Nome usuário", example = "Joao")
    @NotBlank(message = "Nome do usuário não deve ser nulo ou vazio")
    private String nome;

    @Schema(description = "Profissão do usuário", example = "Engenheiro de Software")
    private String profissao;

    @Schema(description = "País do usuário", example = "Brasil")
    private String pais;

    @Schema(description = "Estado do usuário", example = "São Paulo")
    private String estado;

    @Schema(description = "Retornar usuario logado")
    @JsonProperty("usuario_logado")
    private boolean usuarioLogado;

    @Schema(description = "Retorna status do relacionamento de amizade")
    @JsonProperty("status_relacionamento")
    @Enumerated(EnumType.ORDINAL)
    private UsuarioAmigoStatusEnum statusRelacionamento;

}