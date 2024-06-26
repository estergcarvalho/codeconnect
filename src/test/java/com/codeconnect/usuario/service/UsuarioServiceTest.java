package com.codeconnect.usuario.service;

import com.codeconnect.redesocial.dto.RedeSocialRequest;
import com.codeconnect.redesocial.model.RedeSocial;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.dto.UsuarioAmigoResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResquest;
import com.codeconnect.usuario.dto.UsuarioResponse;
import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.exception.UsuarioJaExistenteException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.model.UsuarioAmigo;
import com.codeconnect.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum.AMIGO;
import static com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum.PENDENTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    private static final UUID ID_USUARIO = UUID.randomUUID();
    private static final String NOME_USUARIO = "Joao";
    private static final String EMAIL_USUARIO = "joao@teste.com.br";
    private static final String SENHA_USUARIO = "$2y$10$9S9ivlvoxeZX8.UQx4PiReUle758Ux8py.Os.YACoQOaZtv6e0vdK";
    private static final String PROFISSAO = "Engenheiro Software";
    private static final String PAIS = "Brasil";
    private static final String ESTADO = "São Paulo";
    private static final String REDES_SOCIAIS = "GitHub";
    private static final String LINK = "https://github.com/joao";

    @Test
    @DisplayName("Deve cadastrar usuário")
    public void deveCadastrarUsuario() {
        UsuarioResquest usuarioRequest = UsuarioResquest.builder()
            .nome(NOME_USUARIO)
            .email(EMAIL_USUARIO)
            .senha(SENHA_USUARIO)
            .build();

        Usuario usuario = Usuario.builder()
            .id(ID_USUARIO)
            .nome(NOME_USUARIO)
            .email(EMAIL_USUARIO)
            .senha(SENHA_USUARIO)
            .build();

        when(usuarioRepository.findByEmail(EMAIL_USUARIO)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuarioRequest.getSenha())).thenReturn(SENHA_USUARIO);
        when(usuarioRepository.save(any())).thenReturn(usuario);

        UsuarioResponse usuarioResponse = usuarioService.cadastrar(usuarioRequest);

        assertEquals(ID_USUARIO, usuarioResponse.getId());
        assertEquals(NOME_USUARIO, usuarioResponse.getNome());
        assertEquals(EMAIL_USUARIO, usuarioResponse.getEmail());
    }

    @Test
    @DisplayName("Deve lançar UsuarioJaExistenteException ao cadastrar usuário existente")
    public void deveLancarUsuarioJaExistenteException() {
        Usuario usuario = Usuario.builder()
            .nome(NOME_USUARIO)
            .email(EMAIL_USUARIO)
            .build();

        UsuarioResquest usuarioRequest = UsuarioResquest.builder()
            .nome(NOME_USUARIO)
            .email(EMAIL_USUARIO)
            .senha(SENHA_USUARIO)
            .build();

        when(usuarioRepository.findByEmail(EMAIL_USUARIO)).thenReturn(Optional.of(usuario));

        assertThrows(UsuarioJaExistenteException.class, () -> usuarioService.cadastrar(usuarioRequest));
    }

    @Test
    @DisplayName("Deve listar amigos adicionados")
    public void deveListarAmigos() {
        UsuarioAmigo amigo = UsuarioAmigo.builder()
            .amigo(Usuario.builder()
                .nome("Ester").build()
            )
            .status(AMIGO)
            .build();

        UsuarioAmigo amizadePendente = UsuarioAmigo.builder()
            .amigo(Usuario.builder()
                .nome("Maria").build()
            )
            .status(PENDENTE)
            .build();

        List<UsuarioAmigo> usuarioAmigos = Arrays.asList(amigo, amizadePendente);

        Usuario usuario = Usuario.builder()
            .nome(NOME_USUARIO)
            .email(EMAIL_USUARIO)
            .senha(SENHA_USUARIO)
            .amigos(usuarioAmigos)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);

        UsuarioAmigoResponse usuarioAmigoResponse = usuarioService.listarAmigos();

        assertEquals(1, usuarioAmigoResponse.getAmigos().size());
        assertEquals("Ester", usuarioAmigoResponse.getAmigos().getFirst().getNome());
    }

    @Test
    @DisplayName("Deve editar dados do usuário")
    public void deveEditarDadosDoUsuario() {
        Usuario usuarioLogado = Usuario.builder()
            .id(ID_USUARIO)
            .profissao("Analista Pleno")
            .pais("Brasil")
            .estado("Rio de Janeiro")
            .redesSociais(new ArrayList<>())
            .build();

        UsuarioEditarResquest usuarioEditarResquest = UsuarioEditarResquest.builder()
            .profissao(PROFISSAO)
            .pais(PAIS)
            .estado(ESTADO)
            .redesSociais(List.of(
                RedeSocialRequest.builder()
                    .nome(REDES_SOCIAIS)
                    .link(LINK)
                    .build()
            ))
            .build();

        Usuario usuarioEditado = Usuario.builder()
            .id(ID_USUARIO)
            .profissao(PROFISSAO)
            .pais(PAIS)
            .estado(ESTADO)
            .redesSociais(List.of(
                RedeSocial.builder()
                    .usuario(usuarioLogado)
                    .nome(REDES_SOCIAIS)
                    .link(LINK)
                    .build()
            ))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioLogado);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEditado);

        UsuarioEditarResponse usuarioEditarResponse = usuarioService.editar(usuarioEditarResquest);

        assertNotNull(usuarioEditarResponse);
        assertEquals(PROFISSAO, usuarioEditarResponse.getProfissao());
        assertEquals(PAIS, usuarioEditarResponse.getPais());
        assertEquals(ESTADO, usuarioEditarResponse.getEstado());
        assertEquals(PROFISSAO, usuarioEditarResponse.getProfissao());
        assertEquals(1, usuarioEditarResponse.getRedesSociais().size());
        assertEquals(REDES_SOCIAIS, usuarioEditarResponse.getRedesSociais().getFirst().getNome());
        assertEquals(LINK, usuarioEditarResponse.getRedesSociais().getFirst().getLink());
    }

}