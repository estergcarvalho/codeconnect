package com.codeconnect.usuario.controller;

import com.codeconnect.usuario.dto.UsuarioAmigoResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResquest;
import com.codeconnect.usuario.dto.UsuarioPerfilResponse;
import com.codeconnect.usuario.dto.UsuarioResponse;
import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuário", description = "Api para operações relacionadas ao usuário")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

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
        UsuarioResponse usuarioResponse = service.cadastrar(usuarioResquest);

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
    public ResponseEntity<UsuarioAmigoResponse> listarAmigos() {
        UsuarioAmigoResponse amigo = service.listarAmigos();

        return ResponseEntity.ok(amigo);
    }

    @Operation(
        summary = "Atualiza dados do usuário",
        description = "Atualiza os detalhes de um usuario com base no ID fornecido",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrado ")
        }
    )
    @PutMapping
    public ResponseEntity<UsuarioEditarResponse> editar(@RequestBody UsuarioEditarResquest usuarioEditarResquest) {
        UsuarioEditarResponse usuario = service.editar(usuarioEditarResquest);

        return ResponseEntity.ok(usuario);
    }

    @Operation(
        summary = "Retorna o perfil do usuario dado o id",
        description = "Retorna o perfil do usuario dado o id fornecido",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário localizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não localizado")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPerfilResponse> buscarPorId(@PathVariable UUID id) {
        UsuarioPerfilResponse usuario = service.buscarPorId(id);

        return ResponseEntity.ok(usuario);
    }

    @Operation(
        summary = "Salvar foto do usuário logado",
        description = "Salvar foto com base no usuário logado",
        responses = {
            @ApiResponse(responseCode = "200" , description = "Foto foi salva com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição, foto inválida"),
            @ApiResponse(responseCode = "500",description = "Erro interno servidor" )
        }
    )
    @PostMapping(value = "/salvarFoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponse> salvarFoto(@RequestParam(value = "foto") MultipartFile foto) throws IOException {
        UsuarioResponse usuario = service.salvarFoto(foto);

        return ResponseEntity.ok(usuario);

    }

}