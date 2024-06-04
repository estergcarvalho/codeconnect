package com.codeconnect.login.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "Tipo de token")
    @JsonProperty("token_type")
    private String tokenType;

    @Schema(description = "Tempo de expiração em segundos")
    @JsonProperty("expires_in")
    private Long expiresIn;

}