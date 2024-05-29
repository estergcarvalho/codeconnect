package com.codeconnect.usuario.dto;

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
public class UsuarioResquest {

    @Schema(description = "Nome usuario", example = "Joao")
    @NotBlank(message = "Nome do usuario não deve ser nulo ou vazio")
    private String nome;

    @Schema(description = "Email usuario", example = "joao@teste.com")
    @NotBlank(message = "E-mail do usuario não deve ser nulo ou vazio")
    private String email;

    @Schema(description = "Senha usuario", example = "Abc123")
    @NotBlank(message = "Senha do usuario não deve ser nula ou vazia")
    private String senha;

}