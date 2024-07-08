package com.codeconnect.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioResquest {

    @Schema(description = "Nome usuário", example = "Joao")
    @NotBlank(message = "Nome do usuário não deve ser nulo ou vazio")
    private String nome;

    @Schema(description = "Email usuário", example = "joao@teste.com")
    @NotBlank(message = "E-mail do usuário não deve ser nulo ou vazio")
    private String email;

    @Schema(description = "Senha usuário", example = "Abc123")
    @NotBlank(message = "Senha do usuário não deve ser nula ou vazia")
    private String senha;

    @Schema(description = "Foto do usuário", example = "foto.png")
    private String foto;

}