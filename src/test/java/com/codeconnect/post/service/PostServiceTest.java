package com.codeconnect.post.service;

import com.codeconnect.post.dto.PostRecenteDetalheResponse;
import com.codeconnect.post.dto.PostRecenteResponse;
import com.codeconnect.post.dto.PostRequest;
import com.codeconnect.post.dto.PostResponse;
import com.codeconnect.post.exception.ErroAoSalvarPostException;
import com.codeconnect.post.model.Post;
import com.codeconnect.post.repository.PostRepository;
import com.codeconnect.security.service.TokenService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

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

        PostResponse postResponse = postService.salvar(postagemRequest);

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

        assertThrows(ErroAoSalvarPostException.class, () -> postService.salvar(postagemRequest));
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
            .id(usuarioId)
            .usuario(Usuario.builder().id(usuarioId).build())
            .dataCriacao(dataCriacao)
            .descricao("Boa tarde, lendo um livro super legal 'Por trás de uma lógica', super indico")
            .build();

        Post postagemDois = Post.builder()
            .id(UUID.randomUUID())
            .usuario(Usuario.builder().id(usuarioId).build())
            .dataCriacao(dataCriacao)
            .descricao("Bom dia rede, hoje quero compartilhar meu novo projeto Java, está bem legal")
            .build();

        List<Post> postagens = List.of(postagemUm, postagemDois);

        usuario.getPosts().addAll(postagens);

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);

        List<PostResponse> listaPostagens = postService.listar();

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

        List<PostRecenteDetalheResponse> recentes = postService.recentes();

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
    @DisplayName("Deve listar postagens do usuário visitado")
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

        List<PostResponse> postsUsuario = postService.listarPostsUsuarioAmigo(idUsuario);

        assertNotNull(postsUsuario);
        assertEquals(1, postsUsuario.size());
        assertEquals(descricao, postsUsuario.getFirst().getDescricao());
        assertEquals(dataCriacao, postsUsuario.getFirst().getDataCriacao());
    }

}