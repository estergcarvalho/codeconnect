package com.codeconnect.post.service;

import com.codeconnect.post.dto.PostCurtidaRequest;
import com.codeconnect.post.dto.PostCurtidaResponse;
import com.codeconnect.post.dto.PostQuantidadeDeCurtidaResponse;
import com.codeconnect.post.exception.PostCurtidaJaExistenteException;
import com.codeconnect.post.exception.PostCurtidaNaoEncontradaException;
import com.codeconnect.post.exception.PostNaoEncontradoException;
import com.codeconnect.post.exception.UsuarioNaoAutorizadoParaCurtirException;
import com.codeconnect.post.exception.UsuarioNaoAutorizadoParaRemoverCurtidaException;
import com.codeconnect.post.model.Post;
import com.codeconnect.post.model.PostCurtida;
import com.codeconnect.post.repository.PostCurtidaRepository;
import com.codeconnect.post.repository.PostRepository;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.model.UsuarioAmigo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@Slf4j
public class PostCurtidaService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PostCurtidaRepository repository;

    @Autowired
    private PostRepository postRepository;

    public PostCurtidaResponse curtir(PostCurtidaRequest postCurtidaRequest) {
        log.info("Iniciando a curtida no post");

        Usuario usuarioLogado = tokenService.obterUsuarioToken();

        Post post = postRepository.findById(postCurtidaRequest.getPostId())
            .orElseThrow(PostNaoEncontradoException::new);

        Usuario postUsuario = post.getUsuario();
        boolean isUsuarioLogado = usuarioLogado.getId().equals(postUsuario.getId());

        boolean isAmigoUsuario = false;

        for (UsuarioAmigo usuarioAmigo : usuarioLogado.getAmigos()) {
            if (usuarioAmigo.getAmigo().getId().equals(postUsuario.getId())) {
                isAmigoUsuario = true;
                break;
            }
        }

        if (!isUsuarioLogado && !isAmigoUsuario) {
            throw new UsuarioNaoAutorizadoParaCurtirException();
        }

        boolean jaCurtiu = repository.existsByPostAndUsuario(post, usuarioLogado);

        if (jaCurtiu) {
            throw new PostCurtidaJaExistenteException();
        }

        Timestamp data = new Timestamp(System.currentTimeMillis());

        PostCurtida postCurtida = PostCurtida.builder()
            .post(post)
            .usuario(usuarioLogado)
            .data(data)
            .build();

        repository.save(postCurtida);

        return PostCurtidaResponse.builder()
            .id(postCurtida.getId())
            .post(postCurtida.getPost().getDescricao())
            .usuario(postCurtida.getUsuario().getNome())
            .data(data)
            .build();
    }

    public void removerCurtida(UUID curtidaId) {
        log.info("Iniciando a exclusão da curtida do post");

        Usuario usuarioLogado = tokenService.obterUsuarioToken();

        PostCurtida postCurtida = repository.findById(curtidaId)
            .orElseThrow(PostCurtidaNaoEncontradaException::new);

        if (!postCurtida.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new UsuarioNaoAutorizadoParaRemoverCurtidaException();
        }

        repository.delete(postCurtida);

        log.info("Curtida removida com sucesso");
    }

    public PostQuantidadeDeCurtidaResponse quantidadeCurtidas(UUID postId) {
        log.info("Iniciando a contagem de curtidas do post");

        Post post = postRepository.findById(postId)
            .orElseThrow(PostNaoEncontradoException::new);

        var totalCurtida = repository.countByPost(post);

        return PostQuantidadeDeCurtidaResponse.builder()
            .quantidadeCurtida(totalCurtida)
            .build();
    }

}