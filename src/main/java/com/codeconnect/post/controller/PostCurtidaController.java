package com.codeconnect.post.controller;

import com.codeconnect.post.dto.PostCurtidaRequest;
import com.codeconnect.post.dto.PostCurtidaResponse;
import com.codeconnect.post.dto.PostQuantidadeDeCurtidaResponse;
import com.codeconnect.post.service.PostCurtidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.UUID;

@RestController
@RequestMapping("/curtidas")
@Tag(name = "Post Curtidas", description = "API para gerenciamento de curtidas no post do usuário")
public class PostCurtidaController {

    @Autowired
    private PostCurtidaService service;

    @Operation(
        summary = "Adicionar uma nova curtida a um post",
        description = "Registra uma nova curtida no post com base nos dados fornecidos",
        responses = {
            @ApiResponse(responseCode = "201", description = "Curtida adicionada com sucesso"),
            @ApiResponse(responseCode = "409", description = "O usuário já curtiu esse Post")
        }
    )
    @PostMapping
    public ResponseEntity<PostCurtidaResponse> curtir(@RequestBody PostCurtidaRequest postCurtidaRequest) {
        PostCurtidaResponse response = service.curtir(postCurtidaRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Remover curtida no post",
        description = "Remove uma curtida de um post com base no Id da curtida fornecido. Se a operação for bem sucedida," +
            "a curtida será removida no post do usuário",
        responses = {
            @ApiResponse(responseCode = "204", description = "Curtida removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado remover a curtida")
        }
    )
    @DeleteMapping("/{id}")
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
    @GetMapping("/{id}")
    public ResponseEntity<PostQuantidadeDeCurtidaResponse> quantidadeCurtida(@PathVariable UUID id) {
        PostQuantidadeDeCurtidaResponse totalCurtidas = service.quantidadeCurtidas(id);

        return ResponseEntity.ok(totalCurtidas);
    }

}