package com.codeconnect.post.service;

import com.codeconnect.atividaderecente.dto.AtividadeRecenteRequest;
import com.codeconnect.atividaderecente.enums.AtividadeEnum;
import com.codeconnect.atividaderecente.model.AtividadeRecente;
import com.codeconnect.atividaderecente.service.AtividadeRecenteService;
import com.codeconnect.post.dto.PostComentarioRequest;
import com.codeconnect.post.dto.PostComentarioResponse;
import com.codeconnect.post.dto.PostComentarioUsuarioDetalheResponse;
import com.codeconnect.post.dto.PostCurtidaResponse;
import com.codeconnect.post.dto.PostPerfilDetalheResponse;
import com.codeconnect.post.dto.PostPerfilResponse;
import com.codeconnect.post.dto.PostRecenteDetalheResponse;
import com.codeconnect.post.dto.PostRecenteDetalheUsuarioResponse;
import com.codeconnect.post.dto.PostRecenteResponse;
import com.codeconnect.post.dto.PostRequest;
import com.codeconnect.post.dto.PostResponse;
import com.codeconnect.post.dto.PostTotalDeComentarioResponse;
import com.codeconnect.post.dto.PostTotalDeCurtidaResponse;
import com.codeconnect.post.exception.ErroAoSalvarPostException;
import com.codeconnect.post.exception.PostCurtidaNaoEncontradaException;
import com.codeconnect.post.exception.PostNaoEncontradoException;
import com.codeconnect.post.exception.UsuarioNaoAutorizadoParaComentarException;
import com.codeconnect.post.exception.UsuarioNaoAutorizadoParaCurtirException;
import com.codeconnect.post.model.Post;
import com.codeconnect.post.model.PostComentario;
import com.codeconnect.post.model.PostCurtida;
import com.codeconnect.post.repository.PostComentarioRepository;
import com.codeconnect.post.repository.PostCurtidaRepository;
import com.codeconnect.post.repository.PostRepository;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.dto.UsuarioPerfilDetalheResponse;
import com.codeconnect.usuario.exception.UsuarioNaoEncontradoException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.model.UsuarioAmigo;
import com.codeconnect.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
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

    @Autowired
    private PostComentarioRepository postComentarioRepository;

    @Autowired
    private AtividadeRecenteService atividadeRecenteService;

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
        log.info("Iniciando a lista de posts recentes do usuário");

        Usuario usuario = tokenService.obterUsuarioToken();

        List<PostRecenteResponse> postRecentes = repository.recentes(usuario.getId());

        return postRecentes.stream()
            .map(post -> {
                List<PostComentario> postComentario = postComentarioRepository.findAllByPostId(post.getId());
                List<PostComentarioResponse> comentarioResponse = postComentario.stream()
                    .map(comentario -> PostComentarioResponse.builder()
                        .id(comentario.getId())
                        .descricao(comentario.getDescricao())
                        .dataCriacao(comentario.getDataCriacao())
                        .usuario(PostComentarioUsuarioDetalheResponse.builder()
                            .id(comentario.getUsuario().getId())
                            .nome(comentario.getUsuario().getNome())
                            .imagem(comentario.getUsuario().getImagem())
                            .tipoImagem(comentario.getUsuario().getTipoImagem())
                            .build())
                        .build())
                    .toList();

                return PostRecenteDetalheResponse.builder()
                    .id(post.getId())
                    .dataCriacao(post.getDataCriacao())
                    .descricao(post.getDescricao())
                    .usuario(PostRecenteDetalheUsuarioResponse.builder()
                        .id(post.getIdUsuario())
                        .nome(post.getUsuarioNome())
                        .profissao(post.getProfissao())
                        .imagem(post.getImagem())
                        .tipoImagem(post.getTipoImagem())
                        .build())
                    .curtido(post.getCurtido())
                    .comentarios(comentarioResponse)
                    .totalCurtidas(PostTotalDeCurtidaResponse.builder()
                        .total(post.getTotalCurtidas())
                        .build())
                    .totalComentarios(PostTotalDeComentarioResponse.builder()
                        .total(post.getTotalComentarios())
                        .build())
                    .build();
            })
            .toList();
    }

    public PostPerfilResponse listarPostsUsuarioAmigo(UUID idUsuario) {
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

        List<PostPerfilDetalheResponse> postPerfil = new ArrayList<>();

        if (isUsuarioLogado || isAmigoUsuario) {
            postPerfil = usuario.getPosts().stream()
                .map(post -> {
                    boolean isCurtido = postCurtidaRepository.findByPostIdAndUsuarioId(post.getId(), usuarioLogado.getId()).isPresent();

                    return PostPerfilDetalheResponse.builder()
                        .id(post.getId())
                        .descricao(post.getDescricao())
                        .dataCriacao(post.getDataCriacao())
                        .curtido(isCurtido)
                        .totalCurtidas(PostTotalDeCurtidaResponse.builder()
                            .total(postCurtidaRepository.countByPost(post))
                            .build())
                        .totalComentarios(PostTotalDeComentarioResponse.builder()
                            .total(postComentarioRepository.countByPost(post))
                            .build())
                        .comentarios(post.getComentarios().stream()
                            .map(comentario -> PostComentarioResponse.builder()
                                .id(comentario.getId())
                                .descricao(comentario.getDescricao())
                                .dataCriacao(comentario.getDataCriacao())
                                .usuario(PostComentarioUsuarioDetalheResponse.builder()
                                    .id(comentario.getUsuario().getId())
                                    .nome(comentario.getUsuario().getNome())
                                    .imagem(comentario.getUsuario().getImagem())
                                    .tipoImagem(comentario.getUsuario().getTipoImagem())
                                    .build())
                                .build())
                            .toList())
                        .build();
                })
                .toList();
        }

        UsuarioPerfilDetalheResponse perfil = UsuarioPerfilDetalheResponse.builder()
            .nome(usuario.getNome())
            .imagem(usuario.getImagem())
            .tipoImagem(usuario.getTipoImagem())
            .build();

        return PostPerfilResponse.builder()
            .usuario(perfil)
            .posts(postPerfil)
            .build();
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

        AtividadeRecenteRequest atividadeRecenteRequest = AtividadeRecenteRequest.builder()
            .postId(postCurtida.getPost().getId())
            .atividade(AtividadeEnum.CURTIDA)
            .build();

        atividadeRecenteService.cadastrar(atividadeRecenteRequest);

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

    public PostComentarioResponse comentar(PostComentarioRequest postComentarioRequest) {
        log.info("Iniciando o comentário no post: {}", postComentarioRequest);

        Usuario usuarioLogado = tokenService.obterUsuarioToken();

        Post post = repository.findById(postComentarioRequest.getId())
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
            throw new UsuarioNaoAutorizadoParaComentarException();
        }

        Timestamp dataCriacao = new Timestamp(System.currentTimeMillis());

        PostComentario comentario = PostComentario.builder()
            .usuario(usuarioLogado)
            .post(post)
            .descricao(postComentarioRequest.getDescricao())
            .dataCriacao(dataCriacao)
            .build();

        postComentarioRepository.save(comentario);

        AtividadeRecenteRequest atividadeRecenteRequest = AtividadeRecenteRequest.builder()
            .postId(comentario.getPost().getId())
            .atividade(AtividadeEnum.COMENTARIO)
            .build();

        atividadeRecenteService.cadastrar(atividadeRecenteRequest);

        return PostComentarioResponse.builder()
            .id(comentario.getId())
            .descricao(comentario.getDescricao())
            .dataCriacao(comentario.getDataCriacao())
            .usuario(PostComentarioUsuarioDetalheResponse.builder()
                .id(usuarioLogado.getId())
                .nome(usuarioLogado.getNome())
                .imagem(usuarioLogado.getImagem())
                .tipoImagem(usuarioLogado.getTipoImagem())
                .build())
            .build();
    }

    public PostTotalDeComentarioResponse totalComentario(UUID postId) {
        log.info("Iniciando a contagem de comentários do post: {}", postId);

        Post post = repository.findById(postId)
            .orElseThrow(PostNaoEncontradoException::new);

        var totalComentario = postComentarioRepository.countByPost(post);

        return PostTotalDeComentarioResponse.builder()
            .total(totalComentario)
            .build();
    }

    public List<PostComentarioResponse> listarComentarios(UUID postId) {
        log.info("Iniciando a listagem dos comentários do post");

        Usuario usuarioLogado = tokenService.obterUsuarioToken();

        List<PostComentario> comentarios = postComentarioRepository.findAllByPostId(postId);

        return comentarios.stream()
            .map(comentario -> PostComentarioResponse.builder()
                .id(comentario.getId())
                .descricao(comentario.getDescricao())
                .dataCriacao(comentario.getDataCriacao())
                .usuario(PostComentarioUsuarioDetalheResponse.builder()
                    .id(usuarioLogado.getId())
                    .nome(usuarioLogado.getNome())
                    .imagem(usuarioLogado.getImagem())
                    .tipoImagem(usuarioLogado.getTipoImagem())
                    .build())
                .build())
            .toList();
    }

}