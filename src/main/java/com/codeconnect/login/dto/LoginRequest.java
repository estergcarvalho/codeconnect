package com.codeconnect.login.dto;

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

    @NotBlank(message = "E-mail do usuario não deve ser nulo ou vazio")
    private String email;

    @NotBlank(message = "Senha do usuario não deve ser nula ou vazia")
    private String senha;

}