package com.codeconnect.atividadeRecente.service;

import com.codeconnect.atividadeRecente.dto.AtividadeRecenteRequest;
import com.codeconnect.atividadeRecente.dto.AtividadeRecenteResponse;
import com.codeconnect.atividadeRecente.enums.AtividadeEnum;
import com.codeconnect.atividadeRecente.model.AtividadeRecente;
import com.codeconnect.atividadeRecente.repository.AtividadeRecenteRepository;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
            .atividadeRecentes(new ArrayList<>())
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
            .atividadeEnum(AtividadeEnum.CURTIDA)
            .postId(postId)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.findByUsuarioIdAndPostIdAndAtividade(usuarioId, postId, atividadeRecenteRequest.getAtividadeEnum())).thenReturn(Optional.empty());
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
            .atividadeRecentes(new ArrayList<>())
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
            .atividadeEnum(AtividadeEnum.CURTIDA)
            .postId(postId)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(repository.findByUsuarioIdAndPostIdAndAtividade(usuarioId, postId, atividadeRecenteRequest.getAtividadeEnum())).thenReturn(Optional.of(atividadeRecenteSalvo));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(repository.save(any(AtividadeRecente.class))).thenReturn(atividadeRecenteSalvo);

        AtividadeRecenteResponse atividadeRecenteResponse = service.cadastrar(atividadeRecenteRequest);

        assertNotNull(atividadeRecenteResponse);
        verify(repository, never()).save(any(AtividadeRecente.class));
    }

}