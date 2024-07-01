package com.codeconnect.post.service;

import com.codeconnect.post.dto.PostRecenteDetalheResponse;
import com.codeconnect.post.dto.PostRecenteDetalheUsuarioResponse;
import com.codeconnect.post.dto.PostRecenteResponse;
import com.codeconnect.post.dto.PostRequest;
import com.codeconnect.post.dto.PostResponse;
import com.codeconnect.post.exception.ErroAoSalvarPostException;
import com.codeconnect.post.model.Post;
import com.codeconnect.post.repository.PostRepository;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class PostService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PostRepository repository;

    public PostResponse salvar(PostRequest postRequest) {
        log.info("Iniciando salvamento da postagem");

        try {
            Usuario usuario = tokenService.obterUsuarioToken();

            Timestamp dataCriacao = new Timestamp(System.currentTimeMillis());

            Post post = Post.builder()
                .usuario(usuario)
                .dataCriacao(dataCriacao)
                .descricao(postRequest.getDescricao())
                .build();

            Post salvarPost = repository.save(post);

            log.info("Postagem salva com sucesso");

            return PostResponse.builder()
                .id(salvarPost.getId())
                .dataCriacao(salvarPost.getDataCriacao())
                .descricao(salvarPost.getDescricao())
                .build();
        } catch (Exception exception) {
            log.error("Erro ao salvar postagem", exception);

            throw new ErroAoSalvarPostException();
        }
    }

    public List<PostResponse> listar() {
        log.info("Iniciando a lista de post do usuario");

        Usuario usuario = tokenService.obterUsuarioToken();

        List<Post> postagens = repository.findAllByUsuarioId(usuario.getId());

        return postagens.stream()
            .map(post -> PostResponse.builder()
                .id(post.getId())
                .dataCriacao(post.getDataCriacao())
                .descricao(post.getDescricao())
                .build())
            .toList();
    }

    public List<PostRecenteDetalheResponse> recentes() {
        log.info("Iniciando a lista de post do usuário");

        Usuario usuario = tokenService.obterUsuarioToken();

        List<PostRecenteResponse> postRecentes = repository.recentes(usuario.getId());

        return postRecentes.stream()
            .map(post -> PostRecenteDetalheResponse.builder()
                .id(post.getId())
                .dataCriacao(post.getDataCriacao())
                .descricao(post.getDescricao())
                .usuario(PostRecenteDetalheUsuarioResponse.builder()
                    .id(post.getId())
                    .nome(post.getUsuarioNome())
                    .profissao(post.getProfissao())
                    .build())
                .build())
            .toList();
    }

}