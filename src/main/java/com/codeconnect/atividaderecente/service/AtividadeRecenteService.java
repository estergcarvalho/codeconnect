package com.codeconnect.atividaderecente.service;

import com.codeconnect.atividaderecente.dto.AtividadeRecenteRequest;
import com.codeconnect.atividaderecente.dto.AtividadeRecenteResponse;
import com.codeconnect.atividaderecente.enums.AtividadeEnum;
import com.codeconnect.atividaderecente.model.AtividadeRecente;
import com.codeconnect.atividaderecente.repository.AtividadeRecenteRepository;
import com.codeconnect.post.exception.PostNaoEncontradoException;
import com.codeconnect.post.model.Post;
import com.codeconnect.post.repository.PostRepository;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.model.UsuarioAmigo;
import com.codeconnect.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    public List<AtividadeRecenteResponse> listar() {
        log.info("Iniciando a listagem de atividades recentes");

        Usuario usuarioLogado = tokenService.obterUsuarioToken();

        boolean isAmigoUsuario = false;
        for (UsuarioAmigo usuarioAmigo : usuarioLogado.getAmigos()) {
            if (usuarioAmigo.getId().equals(
                usuarioLogado.getId()) && usuarioAmigo.getStatus() == UsuarioAmigoStatusEnum.AMIGO) {
                isAmigoUsuario = true;
                break;
            }
        }

        List<AtividadeRecenteResponse> atividadesRecentes = new ArrayList<>();

        if (isAmigoUsuario || usuarioLogado.getId().equals(usuarioLogado.getId())) {
            List<AtividadeEnum> atividades = Arrays.asList(AtividadeEnum.CURTIDA, AtividadeEnum.COMENTARIO);

            atividadesRecentes = repository.findByUsuarioIdAndAtividadeIn(usuarioLogado.getId(), atividades).stream()
                .map(atividade -> AtividadeRecenteResponse.builder()
                    .id(atividade.getId())
                    .atividade(atividade.getAtividade())
                    .nome(usuarioLogado.getNome())
                    .dataCriacao(atividade.getDataCriacao())
                    .build())
                .toList();
        }

        return atividadesRecentes;
    }

}