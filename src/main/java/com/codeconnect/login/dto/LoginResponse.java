package com.codeconnect.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    @Schema(description = "Token gerado")
    private String acesso_token;

    @Schema(description = "Tipo de token")
    private String tipo_token;

    @Schema(description = "Tempo de expiração em segundos")
    private Long expira_em;
}