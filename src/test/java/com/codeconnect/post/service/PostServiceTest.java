package com.codeconnect.post.service;

import com.codeconnect.post.dto.PostCurtidaResponse;
import com.codeconnect.post.dto.PostTotalDeCurtidaResponse;
import com.codeconnect.post.dto.PostRecenteDetalheResponse;
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
import com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.model.UsuarioAmigo;
import com.codeconnect.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PostServiceTest {

    @InjectMocks
    private PostService service;

    @Mock
    private PostRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PostCurtidaRepository postCurtidaRepository;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("Deve cadastrar postagem do usuario")
    public void deveCadastrarPostagemUsuario() {
        PostRequest postagemRequest = PostRequest.builder()
            .descricao("Programadar é muito bom")
            .build();

        Usuario usuario = Usuario.builder()
            .id(UUID.randomUUID())
            .email("teste@teste.com")
            .build();

        Post postagemSalva = Post.builder()
            .id(UUID.randomUUID())
            .usuario(usuario)
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .descricao(postagemRequest.getDescricao())
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.save(any(Post.class))).thenReturn(postagemSalva);

        PostResponse postResponse = service.cadastrar(postagemRequest);

        assertNotNull(postResponse);
        assertEquals(postagemSalva.getId(), postResponse.getId());
        assertEquals(postagemSalva.getDescricao(), postResponse.getDescricao());
        assertNotNull(postResponse.getDataCriacao());
    }

    @Test
    @DisplayName("Deve lançar ErroAoSalvarPostException ao cadastrar post")
    public void deveLancarErroAoSalvarPostException() {
        PostRequest postagemRequest = PostRequest.builder()
            .descricao("Programadar é muito bom")
            .build();

        Usuario usuario = Usuario.builder()
            .id(UUID.randomUUID())
            .email("teste@teste.com")
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.save(any(Post.class))).thenThrow(new ErroAoSalvarPostException());

        assertThrows(ErroAoSalvarPostException.class, () -> service.cadastrar(postagemRequest));
    }

    @Test
    @DisplayName("Deve retornar lista de postagens quando usuário possuir postagens")
    public void deveRetornarListaDePostagensQuandoUsuarioPossuirPostagens() {
        Timestamp dataCriacao = new Timestamp(System.currentTimeMillis());

        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
            .id(usuarioId)
            .email("teste@teste.com")
            .posts(new ArrayList<>())
            .build();

        Post postagemUm = Post.builder()
            .id(UUID.randomUUID())
            .usuario(usuario)
            .dataCriacao(dataCriacao)
            .descricao("Boa tarde, lendo um livro super legal 'Por trás de uma lógica', super indico")
            .build();

        Post postagemDois = Post.builder()
            .id(UUID.randomUUID())
            .usuario(usuario)
            .dataCriacao(dataCriacao)
            .descricao("Bom dia rede, hoje quero compartilhar meu novo projeto Java, está bem legal")
            .build();

        List<Post> postagens = List.of(postagemUm, postagemDois);

        usuario.getPosts().addAll(postagens);

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.findAllByUsuarioId(usuario.getId())).thenReturn(postagens);

        List<PostResponse> listaPostagens = service.listar();

        assertFalse(listaPostagens.isEmpty());
        assertEquals(2, listaPostagens.size());

        PostResponse postResponseUm = listaPostagens.getFirst();
        assertEquals(postagemUm.getId(), postResponseUm.getId());
        assertEquals(postagemUm.getDataCriacao(), postResponseUm.getDataCriacao());
        assertEquals(postagemUm.getDescricao(), postResponseUm.getDescricao());
        assertEquals(postagemUm.getUsuario().getId(), usuario.getId());

        PostResponse postResponseDois = listaPostagens.get(1);
        assertEquals(postagemDois.getId(), postResponseDois.getId());
        assertEquals(postagemDois.getDataCriacao(), postResponseDois.getDataCriacao());
        assertEquals(postagemDois.getDescricao(), postResponseDois.getDescricao());
        assertEquals(postagemDois.getUsuario().getId(), usuario.getId());
    }

    @Test
    @DisplayName("Deve listar posts recentes dos amigos do usuário logado")
    public void deveListarPostRecentesDosAmigos() {
        UUID usuarioId = UUID.randomUUID();
        String nomeUsuario = "João";
        UUID amigoId = UUID.randomUUID();
        String nomeAmigo = "Maria";
        String descricao = "teste descricao";
        Timestamp dataCriacao = new Timestamp(System.currentTimeMillis());

        PostRecenteResponse postRecenteResponseUsuarioMock = Mockito.mock(PostRecenteResponse.class);
        when(postRecenteResponseUsuarioMock.getId()).thenReturn(usuarioId);
        when(postRecenteResponseUsuarioMock.getIdUsuario()).thenReturn(usuarioId);
        when(postRecenteResponseUsuarioMock.getUsuarioNome()).thenReturn(nomeUsuario);
        when(postRecenteResponseUsuarioMock.getDescricao()).thenReturn(descricao);
        when(postRecenteResponseUsuarioMock.getDataCriacao()).thenReturn(dataCriacao);

        PostRecenteResponse postRecenteResponseAmigoMock = Mockito.mock(PostRecenteResponse.class);
        when(postRecenteResponseAmigoMock.getId()).thenReturn(amigoId);
        when(postRecenteResponseAmigoMock.getIdUsuario()).thenReturn(amigoId);
        when(postRecenteResponseAmigoMock.getUsuarioNome()).thenReturn(nomeAmigo);
        when(postRecenteResponseAmigoMock.getDescricao()).thenReturn(descricao);
        when(postRecenteResponseAmigoMock.getDataCriacao()).thenReturn(dataCriacao);

        Usuario usuarioLogado = Usuario.builder()
            .id(usuarioId)
            .nome(nomeUsuario)
            .email("usuario@teste.com")
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioLogado);
        when(repository.recentes(usuarioLogado.getId())).thenReturn(List.of(postRecenteResponseUsuarioMock, postRecenteResponseAmigoMock));

        List<PostRecenteDetalheResponse> recentes = service.recentes();

        assertNotNull(recentes);
        assertEquals(2, recentes.size());

        PostRecenteDetalheResponse primeiroPost = recentes.getFirst();
        assertEquals(usuarioId, primeiroPost.getId());
        assertEquals(usuarioId, primeiroPost.getUsuario().getId());
        assertEquals(nomeUsuario, primeiroPost.getUsuario().getNome());
        assertEquals(descricao, primeiroPost.getDescricao());
        assertEquals(dataCriacao, primeiroPost.getDataCriacao());

        PostRecenteDetalheResponse segundoPost = recentes.get(1);
        assertEquals(amigoId, segundoPost.getId());
        assertEquals(amigoId, segundoPost.getUsuario().getId());
        assertEquals(nomeAmigo, segundoPost.getUsuario().getNome());
        assertEquals(descricao, segundoPost.getDescricao());
        assertEquals(dataCriacao, segundoPost.getDataCriacao());
    }

    @Test
    @DisplayName("Deve listar postagens do usuário logado ou de um amigo do usuario")
    public void deveListarPostagensUsuarioVisitado() {
        UUID idUsuario = UUID.randomUUID();
        UUID idUsuarioLogado = UUID.randomUUID();
        String descricao = "post do usuário que esto visitando o seu perfil";
        Timestamp dataCriacao = new Timestamp(System.currentTimeMillis());

        Usuario usuario = Usuario.builder()
            .id(idUsuario)
            .posts(List.of(
                Post.builder()
                    .id(UUID.randomUUID())
                    .dataCriacao(dataCriacao)
                    .descricao(descricao)
                    .build()
            ))
            .build();

        Usuario usuarioLogado = Usuario.builder()
            .id(idUsuarioLogado)
            .posts(List.of(
                Post.builder()
                    .id(UUID.randomUUID())
                    .dataCriacao(dataCriacao)
                    .descricao("post do usuario logado")
                    .build()
            ))
            .amigos(List.of(UsuarioAmigo.builder()
                .amigo(usuario)
                .build()))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioLogado);
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        List<PostResponse> postsUsuario = service.listarPostsUsuarioAmigo(idUsuario);

        assertNotNull(postsUsuario);
        assertEquals(1, postsUsuario.size());
        assertEquals(descricao, postsUsuario.getFirst().getDescricao());
        assertEquals(dataCriacao, postsUsuario.getFirst().getDataCriacao());
    }

    @Test
    @DisplayName("Deve curtir um post")
    public void deveCurtirUmPost() {
        var postId = UUID.randomUUID();
        var usuarioId = UUID.randomUUID();
        var amigoId = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
            .id(usuarioId)
            .nome("Ester")
            .amigos(new ArrayList<>())
            .build();

        Usuario amigo = Usuario.builder()
            .id(amigoId)
            .nome("Amigo")
            .amigos(new ArrayList<>())
            .build();

        usuario.getAmigos().add(
            UsuarioAmigo.builder()
            .id(UUID.randomUUID())
            .usuario(usuario)
            .amigo(amigo)
            .status(UsuarioAmigoStatusEnum.AMIGO)
            .build()
        );

        Post post = Post.builder()
            .id(postId)
            .usuario(usuario)
            .descricao("Estou feliz em compartilhar que vou começar em um novo emprego")
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.findById(postId)).thenReturn(Optional.of(post));
        when(postCurtidaRepository.findByPostIdAndUsuarioId(post.getId(), usuario.getId())).thenReturn(Optional.empty());

        PostCurtidaResponse postCurtidaResponse = service.curtir(postId);

        assertNotNull(postCurtidaResponse);
        verify(postCurtidaRepository, times(1)).save(any(PostCurtida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o post não for encontrado")
    public void deveLancarExcecaoPostNaoEncontrado() {
        var postNaoExistente = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
            .id(UUID.randomUUID())
            .nome("Ester")
            .amigos(Collections.emptyList())
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.findById(postNaoExistente)).thenReturn(Optional.empty());

        assertThrows(PostNaoEncontradoException.class, () -> service.curtir(postNaoExistente));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não for autorizado a curtir o post")
    public void deveLancarExcecaoUsuarioNaoAutorizadoParaCurtir() {
        var postId = UUID.randomUUID();
        var usuarioIdLogado = UUID.randomUUID();
        var usuarioIdNaoAmigo = UUID.randomUUID();

        Usuario usuarioLogado = Usuario.builder()
            .id(usuarioIdLogado)
            .nome("Ester")
            .amigos(Collections.emptyList())
            .build();

        Usuario usuarioNaoAmigo = Usuario.builder()
            .id(usuarioIdNaoAmigo)
            .build();

        Post post = Post.builder()
            .id(postId)
            .usuario(usuarioNaoAmigo)
            .descricao("Estou feliz em compartilhar que vou começar em um novo emprego")
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioLogado);
        when(repository.findById(postId)).thenReturn(Optional.of(post));
        when(postCurtidaRepository.findByPostIdAndUsuarioId(postId, usuarioLogado.getId())).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoAutorizadoParaCurtirException.class, () -> service.curtir(postId));
    }

    @Test
    @DisplayName("Deve remover curtida no post do usuário logado")
    public void deveRemoverCurtidaNoPost() {
        var postId = UUID.randomUUID();
        var usuarioId = UUID.randomUUID();
        var curtidaId = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
            .id(usuarioId)
            .nome("Ester")
            .amigos(Collections.emptyList())
            .build();

        Post post = Post.builder()
            .id(postId)
            .usuario(usuario)
            .descricao("Estou feliz em compartilhar que vou começar em um novo emprego")
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        PostCurtida postCurtida = PostCurtida.builder()
            .id(curtidaId)
            .post(post)
            .usuario(usuario)
            .data(new Timestamp(System.currentTimeMillis()))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(postCurtidaRepository.findByPostIdAndUsuarioId(postId, usuarioId)).thenReturn(Optional.of(postCurtida));

        service.removerCurtida(postId);

        verify(postCurtidaRepository, times(1)).delete(postCurtida);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a curtida não for encontrada para descurtir")
    public void deveLancarExcecaoPostCurtidaNaoEncontrada() {
        var idCurtidaInexistente = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
            .id(UUID.randomUUID())
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.findById(idCurtidaInexistente)).thenReturn(Optional.empty());

        assertThrows(PostCurtidaNaoEncontradaException.class, () -> service.removerCurtida(idCurtidaInexistente));
    }

    @Test
    @DisplayName("Deve contar a quantidade de curtidas no post do usuário")
    public void deveContarQuantidadeCurtidas() {
        var postId = UUID.randomUUID();

        Post post = Post.builder()
            .id(postId)
            .descricao("Estou feliz em compartilhar que vou começar em um novo emprego")
            .build();

        when(repository.findById(postId)).thenReturn(Optional.of(post));
        when(postCurtidaRepository.countByPost(post)).thenReturn(5L);

        PostTotalDeCurtidaResponse quantidadeCurtidas = service.totalCurtida(postId);

        assertNotNull(quantidadeCurtidas);
        assertEquals(5L, quantidadeCurtidas.getTotal());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o post não for encontrado ao contar curtidas")
    public void deveLancarExcecaoPostNaoEncontradoContarCurtidas() {
        var postId = UUID.randomUUID();

        when(repository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNaoEncontradoException.class, () -> service.totalCurtida(postId));
    }

}