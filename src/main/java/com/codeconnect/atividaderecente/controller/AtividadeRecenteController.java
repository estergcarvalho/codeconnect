package com.codeconnect.atividaderecente.controller;

import com.codeconnect.atividaderecente.dto.AtividadeRecenteRequest;
import com.codeconnect.atividaderecente.dto.AtividadeRecenteResponse;
import com.codeconnect.atividaderecente.service.AtividadeRecenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/atividades-recentes")
@Tag(name = "Atividades recentes", description = "Api para operações relacionadas as atividades recentes dos posts")
public class AtividadeRecenteController {

    @Autowired
    private AtividadeRecenteService service;

    @Operation(
        summary = "Cadastra uma atividade recente do post",
        description = "Cadastra uma atividade recente do post do usuário com base nos dados fornecidos",
        responses = {
            @ApiResponse(responseCode = "201", description = "Atividade recente cadastrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Post não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @PostMapping
    public ResponseEntity<AtividadeRecenteResponse> cadastrar(@RequestBody AtividadeRecenteRequest atividadeRecenteRequest) {
        AtividadeRecenteResponse atividadeRecenteResponse = service.cadastrar(atividadeRecenteRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(atividadeRecenteResponse);
    }

    @Operation(
        summary = "Lista as atividade recentes do usuário e amigos",
        description = "Lista as atividades recentes como: curtir, comentar e compartilhar do usuario e amigos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de atividades recentes recuperada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping
    public ResponseEntity<List<AtividadeRecenteResponse>> listar() {
        List<AtividadeRecenteResponse>atividadesRecentes = service.listar();

        return ResponseEntity.ok(atividadesRecentes);
    }

}