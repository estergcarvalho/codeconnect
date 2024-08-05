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
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PostCurtidaServiceTest {

    @InjectMocks
    private PostCurtidaService service;

    @Mock
    private PostCurtidaRepository repository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("Deve curtir um post do usuário")
    public void deveCurtirUmPost() {
        var postId = UUID.randomUUID();

        PostCurtidaRequest postCurtidaRequest = PostCurtidaRequest.builder()
            .postId(postId)
            .build();

        Usuario usuario = Usuario.builder()
            .id(UUID.randomUUID())
            .nome("Ester")
            .amigos(Collections.emptyList())
            .build();

        Post post = Post.builder()
            .id(postId)
            .usuario(usuario)
            .descricao("Estou feliz em compartilhar que vou começar em um novo emprego")
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(repository.existsByPostAndUsuario(post, usuario)).thenReturn(false);

        PostCurtidaResponse response = service.curtir(postCurtidaRequest);

        assertNotNull(response);
        assertEquals(post.getDescricao(), response.getPost());
        assertEquals(usuario.getNome(), response.getUsuario());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a curtida já existir")
    public void deveLancarExcecaoPostCurtidaJaExistente() {
        var postId = UUID.randomUUID();

        PostCurtidaRequest postCurtidaRequest = PostCurtidaRequest.builder()
            .postId(postId)
            .build();

        Usuario usuario = Usuario.builder()
            .id(UUID.randomUUID())
            .nome("Ester")
            .amigos(Collections.emptyList())
            .build();

        Post post = Post.builder()
            .id(postId)
            .usuario(usuario)
            .descricao("Estou feliz em compartilhar que vou começar em um novo emprego")
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(repository.existsByPostAndUsuario(post, usuario)).thenReturn(true);

        assertThrows(PostCurtidaJaExistenteException.class, () -> service.curtir(postCurtidaRequest));
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

        PostCurtidaRequest postCurtidaRequest = PostCurtidaRequest.builder()
            .postId(postNaoExistente)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(postRepository.findById(postCurtidaRequest.getPostId())).thenReturn(Optional.empty());

        assertThrows(PostNaoEncontradoException.class, () -> service.curtir(postCurtidaRequest));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não for autorizado a curtir o post")
    public void deveLancarExcecaoUsuarioNaoAutorizadoParaCurtir() {
        var postId = UUID.randomUUID();

        PostCurtidaRequest postCurtidaRequest = PostCurtidaRequest.builder()
            .postId(postId)
            .build();

        Usuario usuarioLogado = Usuario.builder()
            .id(UUID.randomUUID())
            .nome("Ester")
            .amigos(Collections.emptyList())
            .build();

        Usuario usuarioNaoamigo = Usuario.builder()
            .id(UUID.randomUUID())
            .build();

        Post post = Post.builder()
            .id(postId)
            .usuario(usuarioNaoamigo)
            .descricao("Estou feliz em compartilhar que vou começar em um novo emprego")
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioLogado);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(repository.existsByPostAndUsuario(post, usuarioLogado)).thenReturn(false);

        assertThrows(UsuarioNaoAutorizadoParaCurtirException.class, () -> service.curtir(postCurtidaRequest));
    }

    @Test
    @DisplayName("Deve remover curtida no post do usuário")
    public void deveRemoverCurtidaNoPost() {
        var curtidaId = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
            .id(UUID.randomUUID())
            .nome("Ester")
            .amigos(Collections.emptyList())
            .build();

        Post post = Post.builder()
            .id(UUID.randomUUID())
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
        when(repository.findById(curtidaId)).thenReturn(Optional.of(postCurtida));

        service.removerCurtida(curtidaId);

        verify(repository, times(1)).delete(postCurtida);
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
    @DisplayName("Deve lançar exceção quando o usuário não for autorizado a descurtir")
    public void deveLancarExcecaoUsuarioNaoAutorizadoParaRemoverCurtida() {
        var curtidaId = UUID.randomUUID();

        Usuario usuarioLogado = Usuario.builder()
            .id(UUID.randomUUID())
            .nome("Ester")
            .amigos(Collections.emptyList())
            .build();

        Usuario usuarioNaoamigo = Usuario.builder()
            .id(UUID.randomUUID())
            .build();

        PostCurtida postCurtida = PostCurtida.builder()
            .id(curtidaId)
            .usuario(usuarioNaoamigo)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioLogado);
        when(repository.findById(curtidaId)).thenReturn(Optional.of(postCurtida));

        assertThrows(UsuarioNaoAutorizadoParaRemoverCurtidaException.class, () -> service.removerCurtida(curtidaId));
    }

    @Test
    @DisplayName("Deve contar a quantidade de curtidas no post do usuário")
    public void deveContarQuantidadeCurtidas() {
        var postId = UUID.randomUUID();

        Post post = Post.builder()
            .id(postId)
            .descricao("Estou feliz em compartilhar que vou começar em um novo emprego")
            .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(repository.countByPost(post)).thenReturn(5L);

        PostQuantidadeDeCurtidaResponse quantidadeCurtidas = service.quantidadeCurtidas(postId);

        assertNotNull(quantidadeCurtidas);
        assertEquals(5L, quantidadeCurtidas.getQuantidadeCurtida());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o post não for encontrado ao contar curtidas")
    public void deveLancarExcecaoPostNaoEncontradoContarCurtidas() {
        var postId = UUID.randomUUID();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNaoEncontradoException.class, () -> service.quantidadeCurtidas(postId));
    }

}