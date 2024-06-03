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
    private String access_token;

    @Schema(description = "Tipo de token")
    private String token_type;

    @Schema(description = "Tempo de expiração em segundos")
    private Long expires_in;

}