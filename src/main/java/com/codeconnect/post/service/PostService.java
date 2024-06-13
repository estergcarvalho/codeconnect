package com.codeconnect.post.service;

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

@Service
@Slf4j
public class PostService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PostRepository postRepository;

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

            Post salvarPost = postRepository.save(post);

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

}