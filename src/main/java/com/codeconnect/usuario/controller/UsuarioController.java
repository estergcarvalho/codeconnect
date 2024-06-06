package com.codeconnect.usuario.controller;

import com.codeconnect.usuario.dto.AmigoResponse;
import com.codeconnect.usuario.dto.UsuarioResponse;
import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuário", description = "Api para operações relacionadas ao usuário")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(
        summary = "Cadastra um usuário",
        description = "Cadastra um novo usuário com base nos dados fornecidos",
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Usuário já existente"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioResquest usuarioResquest) {
        UsuarioResponse usuarioResponse = usuarioService.cadastrar(usuarioResquest);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponse);
    }

    @Operation(
        summary = "Amigos do usuário",
        description = "Lista com os amigos do usuário",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de amigos encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping("/amigos")
    public ResponseEntity<AmigoResponse> listarAmigos() {
        AmigoResponse amigo = usuarioService.listarAmigos();

        return ResponseEntity.ok(amigo);
    }

}