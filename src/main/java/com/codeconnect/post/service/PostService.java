package com.codeconnect.post.service;

import com.codeconnect.post.dto.PostCurtidaResponse;
import com.codeconnect.post.dto.PostTotalDeCurtidaResponse;
import com.codeconnect.post.dto.PostRecenteDetalheResponse;
import com.codeconnect.post.dto.PostRecenteDetalheUsuarioResponse;
import com.codeconnect.post.dto.PostRecenteResponse;
import com.codeconnect.post.dto.PostRequest;
import com.codeconnect.post.dto.PostResponse;
import com.codeconnect.post.exception.ErroAoSalvarPostException;
import com.codeconnect.post.exception.PostCurtidaNaoEncontradaException;
import com.codeconnect.post.exception.PostNaoEncontradoException;
import com.codeconnect.post.exception.UsuarioNaoAutorizadoParaCurtirException;
import com.codeconnect.post.model.Post;
import com.codeconnect.post.model.PostCurtida;
import com.codeconnect.post.repository.PostCurtidaRepository;
import com.codeconnect.post.repository.PostRepository;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.exception.UsuarioNaoEncontradoException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.model.UsuarioAmigo;
import com.codeconnect.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PostService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PostRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostCurtidaRepository postCurtidaRepository;

    public PostResponse cadastrar(PostRequest postRequest) {
        log.info("Iniciando cadastro da postagem");

        try {
            Usuario usuario = tokenService.obterUsuarioToken();

            Timestamp dataCriacao = new Timestamp(System.currentTimeMillis());

            Post post = Post.builder()
                .usuario(usuario)
                .dataCriacao(dataCriacao)
                .descricao(postRequest.getDescricao())
                .build();

            Post salvarPost = repository.save(post);

            return PostResponse.builder()
                .id(salvarPost.getId())
                .nome(usuario.getNome())
                .profissao(usuario.getProfissao())
                .dataCriacao(salvarPost.getDataCriacao())
                .descricao(salvarPost.getDescricao())
                .imagem(usuario.getImagem())
                .tipoImagem(usuario.getTipoImagem())
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
        log.info("Iniciando a lista de post recentes do usuario");

        Usuario usuario = tokenService.obterUsuarioToken();

        List<PostRecenteResponse> postRecentes = repository.recentes(usuario.getId());

        return postRecentes.stream()
            .map(post -> PostRecenteDetalheResponse.builder()
                .id(post.getId())
                .dataCriacao(post.getDataCriacao())
                .descricao(post.getDescricao())
                .curtido(post.getCurtido())
                .usuario(PostRecenteDetalheUsuarioResponse.builder()
                    .id(post.getIdUsuario())
                    .nome(post.getUsuarioNome())
                    .profissao(post.getProfissao())
                    .imagem(post.getImagem())
                    .tipoImagem(post.getTipoImagem())
                    .build())
                .build())
            .toList();
    }

    public List<PostResponse> listarPostsUsuarioAmigo(UUID idUsuario) {
        log.info("Iniciando a listagem de posts do perfil do usuario e amigo");

        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(UsuarioNaoEncontradoException::new);

        Usuario usuarioLogado = tokenService.obterUsuarioToken();
        boolean isUsuarioLogado = usuarioLogado.getId().equals(idUsuario);
        boolean isAmigoUsuario = false;

        for (UsuarioAmigo usuarioAmigo : usuarioLogado.getAmigos()) {
            if (usuarioAmigo.getAmigo().getId().equals(idUsuario)) {
                isAmigoUsuario = true;
                break;
            }
        }

        if (isUsuarioLogado || isAmigoUsuario) {
            return usuario.getPosts().stream()
                .map(post -> PostResponse.builder()
                    .id(post.getId())
                    .nome(usuario.getNome())
                    .dataCriacao(post.getDataCriacao())
                    .descricao(post.getDescricao())
                    .imagem(usuario.getImagem())
                    .tipoImagem(usuario.getTipoImagem())
                    .build())
                .toList();
        }

        return List.of();
    }

    public PostCurtidaResponse curtir(UUID postId) {
        log.info("Iniciando a curtida no post: {}", postId);

        Usuario usuarioLogado = tokenService.obterUsuarioToken();

        Post post = repository.findById(postId)
            .orElseThrow(PostNaoEncontradoException::new);

        var curtida = postCurtidaRepository.findByPostIdAndUsuarioId(post.getId(), usuarioLogado.getId());
        if (curtida.isPresent()) {
            return PostCurtidaResponse.builder()
                .id(curtida.get().getId())
                .build();
        }

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

        Timestamp data = new Timestamp(System.currentTimeMillis());

        PostCurtida postCurtida = PostCurtida.builder()
            .post(post)
            .usuario(usuarioLogado)
            .data(data)
            .build();

        postCurtidaRepository.save(postCurtida);

        return PostCurtidaResponse.builder()
            .id(postCurtida.getId())
            .build();
    }

    public void removerCurtida(UUID postId) {
        log.info("Iniciando a exclusão da curtida do post: {}", postId);

        Usuario usuarioLogado = tokenService.obterUsuarioToken();

        PostCurtida postCurtida = postCurtidaRepository.findByPostIdAndUsuarioId(postId, usuarioLogado.getId())
            .orElseThrow(PostCurtidaNaoEncontradaException::new);

        postCurtidaRepository.delete(postCurtida);

        log.info("Curtida removida com sucesso");
    }

    public PostTotalDeCurtidaResponse totalCurtida(UUID postId) {
        log.info("Iniciando a contagem de curtidas do post: {}", postId);

        Post post = repository.findById(postId)
            .orElseThrow(PostNaoEncontradoException::new);

        var totalCurtida = postCurtidaRepository.countByPost(post);

        return PostTotalDeCurtidaResponse.builder()
            .total(totalCurtida)
            .build();
    }

}