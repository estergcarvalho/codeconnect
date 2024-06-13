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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}