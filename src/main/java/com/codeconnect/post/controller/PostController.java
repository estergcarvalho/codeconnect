package com.codeconnect.post.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "API para gerenciamento de posts do usuário")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(
        summary = "Cria um nova postagem",
        description = "Cria um nova postagem para o usuário atualmente logado com base nos dados fornecidos",
        responses = {
            @ApiResponse(responseCode = "201", description = "Postagema criada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @PostMapping
    public ResponseEntity<PostResponse> salvar(@RequestBody @Valid PostRequest postRequest) {
        PostResponse postResponse = postService.salvar(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

}