package com.codeconnect.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Schema(description = "Email do usuário", example = "joao@teste.com")
    @NotBlank(message = "E-mail do usuário não deve ser nulo ou vazio")
    private String email;

    @Schema(description = "Senha do usuário", example = "Abc123")
    @NotBlank(message = "Senha do usuário não deve ser nula ou vazia")
    private String senha;

}