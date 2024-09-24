package com.codeconnect.atividaderecente.service;

import com.codeconnect.atividaderecente.dto.AtividadeRecenteRequest;
import com.codeconnect.atividaderecente.dto.AtividadeRecenteResponse;
import com.codeconnect.atividaderecente.model.AtividadeRecente;
import com.codeconnect.atividaderecente.repository.AtividadeRecenteRepository;
import com.codeconnect.post.exception.PostNaoEncontradoException;
import com.codeconnect.post.model.Post;
import com.codeconnect.post.repository.PostRepository;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AtividadeRecenteService {

    @Autowired
    private AtividadeRecenteRepository repository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TokenService tokenService;

    public AtividadeRecenteResponse cadastrar(AtividadeRecenteRequest atividadeRecenteRequest) {
        log.info("Iniciando o cadastro da atividade recente");

        Usuario usuario = tokenService.obterUsuarioToken();

        Post post = postRepository.findById(atividadeRecenteRequest.getPostId())
            .orElseThrow(PostNaoEncontradoException::new);

        Optional<AtividadeRecente> atividadeExistente = repository.findByUsuarioIdAndPostIdAndAtividade(
            usuario.getId(), post.getId(), atividadeRecenteRequest.getAtividade()
        );

        if (atividadeExistente.isPresent()) {
            log.info("Atividade já cadastrada.");

            return AtividadeRecenteResponse.builder()
                .id(atividadeExistente.get().getId())
                .nome(usuario.getNome())
                .dataCriacao(atividadeExistente.get().getDataCriacao())
                .atividade(atividadeExistente.get().getAtividade())
                .build();
        }

        AtividadeRecente atividadeRecente = AtividadeRecente.builder()
            .id(UUID.randomUUID())
            .usuario(usuario)
            .post(post)
            .atividade(atividadeRecenteRequest.getAtividade())
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        repository.save(atividadeRecente);

        return AtividadeRecenteResponse.builder()
            .id(atividadeRecente.getId())
            .nome(usuario.getNome())
            .atividade(atividadeRecente.getAtividade())
            .dataCriacao(atividadeRecente.getDataCriacao())
            .build();
    }

}