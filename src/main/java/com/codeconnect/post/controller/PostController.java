package com.codeconnect.post.controller;

import com.codeconnect.post.dto.PostCurtidaResponse;
import com.codeconnect.post.dto.PostTotalDeCurtidaResponse;
import com.codeconnect.post.dto.PostRecenteDetalheResponse;
import com.codeconnect.post.dto.PostRequest;
import com.codeconnect.post.dto.PostResponse;
import com.codeconnect.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "API para gerenciamento de posts do usuário")
public class PostController {

    @Autowired
    private PostService service;

    @Operation(
        summary = "Cadastrar uma nova postagem",
        description = "Cadastrar uma nova postagem para o usuário atualmente logado com base nos dados fornecidos",
        responses = {
            @ApiResponse(responseCode = "201", description = "Postagem criada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @PostMapping
    public ResponseEntity<PostResponse> cadastrar(@RequestBody @Valid PostRequest postRequest) {
        PostResponse postResponse = service.cadastrar(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @Operation(
        summary = "Lista os posts do usuário",
        description = "lista de posts do usuário atualmente logado",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de posts recuperada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping
    public ResponseEntity<List<PostResponse>> listar() {
        List<PostResponse> postagens = service.listar();

        return ResponseEntity.ok(postagens);
    }

    @Operation(
        summary = "Lista os posts recentes do usuário logado",
        description = "Lista de posts do usuário logado e de seus amigos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de posts recentes recuperada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping("/recentes")
    public ResponseEntity<List<PostRecenteDetalheResponse>> recentes() {
        List<PostRecenteDetalheResponse> postagensRecentes = service.recentes();

        return ResponseEntity.ok(postagensRecentes);
    }

    @Operation(
        summary = "Lista os posts do perfil do usuario ou amigo com base no seu id",
        description = "Lista os posts do usuário ou amigo com base no seu id",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de posts recuperada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<List<PostResponse>> listarPostsUsuarioAmigo(@PathVariable UUID id) {
        List<PostResponse> usuario = service.listarPostsUsuarioAmigo(id);

        return ResponseEntity.ok(usuario);
    }

    @Operation(
        summary = "Adicionar uma nova curtida a um post",
        description = "Registra uma nova curtida com base no id fornecido",
        responses = {
            @ApiResponse(responseCode = "200", description = "Curtida realizada com sucesso"),
            @ApiResponse(responseCode = "409", description = "O usuário já curtiu esse Post")
        }
    )
    @PostMapping("/{id}/curtir")
    public ResponseEntity<PostCurtidaResponse> curtir(@PathVariable UUID id) {
        PostCurtidaResponse response = service.curtir(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Remover curtida no post",
        description = "Remove uma curtida de um post com base no id do post",
        responses = {
            @ApiResponse(responseCode = "204", description = "Curtida removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado remover a curtida")
        }
    )
    @DeleteMapping("/{id}/remover-curtida")
    public ResponseEntity<PostCurtidaResponse> removerCurtida(@PathVariable UUID id) {
        service.removerCurtida(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Obter total de curtidas de um post",
        description = "Retorna o total de curtidas de um post com base no ID fornecido",
        responses = {
            @ApiResponse(responseCode = "200", description = "Quantidade de curtidas retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Post não encontrado")
        }
    )
    @GetMapping("/{id}/total-curtidas")
    public ResponseEntity<PostTotalDeCurtidaResponse> totalCurtida(@PathVariable UUID id) {
        PostTotalDeCurtidaResponse totalCurtidas = service.totalCurtida(id);

        return ResponseEntity.ok(totalCurtidas);
    }

}