package com.codeconnect.post.service;

import com.codeconnect.post.dto.PostRequest;
import com.codeconnect.post.dto.PostResponse;
import com.codeconnect.post.exception.ErroAoSalvarPostException;
import com.codeconnect.post.model.Post;
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
import java.util.List;
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
    private PostRepository postRepository;

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
        when(postRepository.save(any(Post.class))).thenReturn(postagemSalva);

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
        when(postRepository.save(any(Post.class))).thenThrow(new ErroAoSalvarPostException());

        assertThrows(ErroAoSalvarPostException.class, () -> postService.salvar(postagemRequest));
    }

    @Test
    @DisplayName("Deve retornar lista de postagens quando usuário possuir postagens")
    public void deveRetornarListaDePostagensQuandoUsuarioPossuirPostagens() {
        Timestamp dataCriacao = new Timestamp(System.currentTimeMillis());

        UUID usuarioId = UUID.randomUUID();

        Post postagemUm = Post.builder()
            .id(UUID.randomUUID())
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

        Usuario usuario = Usuario.builder()
            .id(UUID.randomUUID())
            .email("teste@teste.com")
            .posts(postagens)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);

        List<PostResponse> listaPostagens = postService.listar();

        assertFalse(listaPostagens.isEmpty());
        assertEquals(2, listaPostagens.size());

        PostResponse postResponseUm = listaPostagens.getFirst();
        assertEquals(postagemUm.getId(), postResponseUm.getId());
        assertEquals(postagemUm.getDataCriacao(), postResponseUm.getDataCriacao());
        assertEquals(postagemUm.getDescricao(), postResponseUm.getDescricao());

        PostResponse postResponseDois = listaPostagens.get(1);
        assertEquals(postagemDois.getId(), postResponseDois.getId());
        assertEquals(postagemDois.getDataCriacao(), postResponseDois.getDataCriacao());
        assertEquals(postagemDois.getDescricao(), postResponseDois.getDescricao());
    }

}