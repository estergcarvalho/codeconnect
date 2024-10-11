package com.codeconnect.atividaderecente.service;

import com.codeconnect.atividaderecente.dto.AtividadeRecenteRequest;
import com.codeconnect.atividaderecente.dto.AtividadeRecenteResponse;
import com.codeconnect.atividaderecente.enums.AtividadeEnum;
import com.codeconnect.atividaderecente.model.AtividadeRecente;
import com.codeconnect.atividaderecente.repository.AtividadeRecenteRepository;
import com.codeconnect.post.model.Post;
import com.codeconnect.post.repository.PostRepository;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.model.UsuarioAmigo;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AtividadeRecenteServiceTest {

    @InjectMocks
    AtividadeRecenteService service;

    @Mock
    AtividadeRecenteRepository repository;

    @Mock
    PostRepository postRepository;

    @Mock
    TokenService tokenService;

    @Test
    @DisplayName("Deve cadastrar uma atividade recente com sucesso")
    public void deveCadastrarUmaAtividadeRecenteComSucesso() {
        UUID usuarioId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID atividadeId = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
            .id(usuarioId)
            .email("joao@teste.com")
            .nome("Joao")
            .build();

        Post post = Post.builder()
            .id(postId)
            .usuario(usuario)
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        AtividadeRecente atividadeRecenteSalvo = AtividadeRecente.builder()
            .id(atividadeId)
            .usuario(usuario)
            .post(post)
            .atividade(AtividadeEnum.CURTIDA)
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        AtividadeRecenteRequest atividadeRecenteRequest = AtividadeRecenteRequest.builder()
            .atividade(AtividadeEnum.CURTIDA)
            .postId(postId)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.findByUsuarioIdAndPostIdAndAtividade(usuarioId, postId, atividadeRecenteRequest.getAtividade())).thenReturn(Optional.empty());
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(repository.save(any(AtividadeRecente.class))).thenReturn(atividadeRecenteSalvo);

        AtividadeRecenteResponse atividadeRecenteResponse = service.cadastrar(atividadeRecenteRequest);

        assertNotNull(atividadeRecenteResponse);
        verify(repository, times(1)).save(any(AtividadeRecente.class));
    }

    @Test
    @DisplayName("Não deve cadastrar uma atividade recente em duplicada")
    public void naoDeveSalvarUmaAtividadeRecenteDuplicada() {
        UUID usuarioId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID atividadeId = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
            .id(usuarioId)
            .email("joao@teste.com")
            .nome("Joao")
            .build();

        Post post = Post.builder()
            .id(postId)
            .usuario(usuario)
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        AtividadeRecente atividadeRecenteSalvo = AtividadeRecente.builder()
            .id(atividadeId)
            .usuario(usuario)
            .post(post)
            .atividade(AtividadeEnum.CURTIDA)
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        AtividadeRecenteRequest atividadeRecenteRequest = AtividadeRecenteRequest.builder()
            .atividade(AtividadeEnum.CURTIDA)
            .postId(postId)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.findByUsuarioIdAndPostIdAndAtividade(usuarioId, postId, atividadeRecenteRequest.getAtividade())).thenReturn(Optional.of(atividadeRecenteSalvo));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(repository.save(any(AtividadeRecente.class))).thenReturn(atividadeRecenteSalvo);

        AtividadeRecenteResponse atividadeRecenteResponse = service.cadastrar(atividadeRecenteRequest);

        assertNotNull(atividadeRecenteResponse);
        verify(repository, never()).save(any(AtividadeRecente.class));
    }

    @Test
    @DisplayName("Deve listar atividades recentes do usuário e amigos")
    public void deveListarAtividadesComAmigos() {
        UUID usuarioId = UUID.randomUUID();
        UUID amigoId = UUID.randomUUID();
        UUID atividadeId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        Usuario usuarioLogado = Usuario.builder()
            .id(usuarioId)
            .nome("Joao")
            .amigos(Collections.singletonList(
                UsuarioAmigo.builder()
                    .id(UUID.randomUUID())
                    .amigo(Usuario.builder().id(amigoId).nome("Maria").build())
                    .status(UsuarioAmigoStatusEnum.AMIGO)
                    .build()
            ))
            .build();

        Post post = Post.builder()
            .id(postId)
            .descricao("Oi devs")
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .build();

        AtividadeRecente atividade1 = AtividadeRecente.builder()
            .id(atividadeId)
            .atividade(AtividadeEnum.CURTIDA)
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .usuario(usuarioLogado)
            .post(post)
            .build();

        AtividadeRecente comentario = AtividadeRecente.builder()
            .id(UUID.randomUUID())
            .atividade(AtividadeEnum.COMENTARIO)
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .usuario(usuarioLogado)
            .post(post)
            .build();

        AtividadeRecente compartilhamento = AtividadeRecente.builder()
            .id(UUID.randomUUID())
            .atividade(AtividadeEnum.COMPARTILHAMENTO)
            .dataCriacao(new Timestamp(System.currentTimeMillis()))
            .usuario(usuarioLogado)
            .post(post)
            .build();

        List<AtividadeRecente> atividadesUsuarios = Arrays.asList(atividade1, comentario, compartilhamento);

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioLogado);
        when(repository.findByUsuarioIdIn(anyList())).thenReturn(atividadesUsuarios);

        List<AtividadeRecenteResponse> atividadesRecentes = service.listar();

        assertNotNull(atividadesRecentes);
        assertEquals(3, atividadesRecentes.size());
        assertEquals(atividadeId, atividadesRecentes.getFirst().getId());
        assertEquals(AtividadeEnum.CURTIDA, atividadesRecentes.getFirst().getAtividade());
        assertEquals("Joao", atividadesRecentes.getFirst().getNome());

        verify(repository, times(1)).findByUsuarioIdIn(anyList());
    }

}