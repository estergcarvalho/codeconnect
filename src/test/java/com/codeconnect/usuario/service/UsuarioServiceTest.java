package com.codeconnect.usuario.service;

import com.codeconnect.redesocial.dto.RedeSocialRequest;
import com.codeconnect.redesocial.model.RedeSocial;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.dto.UsuarioAmigoResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResquest;
import com.codeconnect.usuario.dto.UsuarioPerfilResponse;
import com.codeconnect.usuario.dto.UsuarioResponse;
import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum;
import com.codeconnect.usuario.exception.ErroFormatoImagemUsuarioNaoAceitoException;
import com.codeconnect.usuario.exception.UsuarioJaExistenteException;
import com.codeconnect.usuario.exception.UsuarioNaoEncontradoException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.model.UsuarioAmigo;
import com.codeconnect.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum.AMIGO;
import static com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum.PENDENTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private static final UUID ID_USUARIO_AMIGO = UUID.randomUUID();
    private static final UUID ID_USUARIO_PENDENTE = UUID.randomUUID();
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

    @Test
    @DisplayName("Deve retornar perfil de um amigo com redes sociais")
    public void deveRetornarPerfilDeUmAmigo() {
        UUID idUsuario = UUID.randomUUID();

        Usuario usuarioNaoLogado = Usuario.builder()
            .id(idUsuario)
            .nome(NOME_USUARIO)
            .build();

        UsuarioAmigo amigo = UsuarioAmigo.builder()
            .amigo(Usuario.builder()
                .id(ID_USUARIO_AMIGO)
                .nome("Ester")
                .email("ester@teste.com")
                .redesSociais(Collections.singletonList(
                    RedeSocial.builder()
                        .nome(REDES_SOCIAIS)
                        .link(LINK)
                        .build()
                ))
                .build()
            )
            .status(UsuarioAmigoStatusEnum.AMIGO)
            .build();

        List<UsuarioAmigo> usuarioAmigos = Collections.singletonList(amigo);
        usuarioNaoLogado.setAmigos(usuarioAmigos);

        Usuario usuarioAmigo = Usuario.builder()
            .id(ID_USUARIO_AMIGO)
            .nome("Ester")
            .email("ester@teste.com")
            .profissao(PROFISSAO)
            .pais(PAIS)
            .estado(ESTADO)
            .redesSociais(Collections.singletonList(
                RedeSocial.builder()
                    .nome(REDES_SOCIAIS)
                    .link(LINK)
                    .build()
            ))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioNaoLogado);
        when(usuarioRepository.findById(ID_USUARIO_AMIGO)).thenReturn(Optional.of(usuarioAmigo));

        UsuarioPerfilResponse usuarioPerfilResponse = usuarioService.buscarPorId(ID_USUARIO_AMIGO);

        assertEquals(ID_USUARIO_AMIGO, usuarioPerfilResponse.getId());
        assertEquals("Ester", usuarioPerfilResponse.getNome());
        assertEquals("ester@teste.com", usuarioPerfilResponse.getEmail());
        assertEquals(PROFISSAO, usuarioPerfilResponse.getProfissao());
        assertEquals(ESTADO, usuarioPerfilResponse.getEstado());
        assertEquals(PAIS, usuarioPerfilResponse.getPais());
        assertFalse(usuarioPerfilResponse.isUsuarioLogado());
        assertEquals(UsuarioAmigoStatusEnum.AMIGO, usuarioPerfilResponse.getStatusRelacionamento());
        assertEquals(1, usuarioPerfilResponse.getRedesSociais().size());
        assertEquals(REDES_SOCIAIS, usuarioPerfilResponse.getRedesSociais().getFirst().getNome());
        assertEquals(LINK, usuarioPerfilResponse.getRedesSociais().getFirst().getLink());
    }

    @Test
    @DisplayName("Deve retornar perfil de um usuário com relacionamento pendente")
    public void deveRetornarPerfilDeUmUsuarioPendente() {
        UUID idUsuario = UUID.randomUUID();

        Usuario usuarioNaoLogado = Usuario.builder()
            .id(idUsuario)
            .nome(NOME_USUARIO)
            .build();

        UsuarioAmigo amizadePendente = UsuarioAmigo.builder()
            .amigo(Usuario.builder()
                .id(ID_USUARIO_PENDENTE)
                .nome("Maria").build()
            )
            .status(UsuarioAmigoStatusEnum.PENDENTE)
            .build();

        List<UsuarioAmigo> usuarioAmigos = Collections.singletonList(amizadePendente);
        usuarioNaoLogado.setAmigos(usuarioAmigos);

        Usuario usuarioPendente = Usuario.builder()
            .id(ID_USUARIO_PENDENTE)
            .nome("Maria")
            .email("maria@teste.com")
            .profissao(PROFISSAO)
            .pais(PAIS)
            .estado(ESTADO)
            .redesSociais(Collections.singletonList(
                RedeSocial.builder()
                    .nome(REDES_SOCIAIS)
                    .link(LINK)
                    .build()
            ))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioNaoLogado);
        when(usuarioRepository.findById(ID_USUARIO_PENDENTE)).thenReturn(Optional.of(usuarioPendente));

        UsuarioPerfilResponse usuarioPerfilResponse = usuarioService.buscarPorId(ID_USUARIO_PENDENTE);

        assertEquals(ID_USUARIO_PENDENTE, usuarioPerfilResponse.getId());
        assertEquals("Maria", usuarioPerfilResponse.getNome());
        assertEquals("maria@teste.com", usuarioPerfilResponse.getEmail());
        assertEquals(PROFISSAO, usuarioPerfilResponse.getProfissao());
        assertEquals(ESTADO, usuarioPerfilResponse.getEstado());
        assertEquals(PAIS, usuarioPerfilResponse.getPais());
        assertFalse(usuarioPerfilResponse.isUsuarioLogado());
        assertEquals(UsuarioAmigoStatusEnum.PENDENTE, usuarioPerfilResponse.getStatusRelacionamento());
        assertEquals(1, usuarioPerfilResponse.getRedesSociais().size());
        assertEquals(REDES_SOCIAIS, usuarioPerfilResponse.getRedesSociais().getFirst().getNome());
        assertEquals(LINK, usuarioPerfilResponse.getRedesSociais().getFirst().getLink());
    }

    @Test
    @DisplayName("Deve retornar perfil de usuário quando id corresponde ao usuário logado")
    public void deveRetornarPerfilUsuarioLogado() {
        UUID idUsuario = UUID.randomUUID();

        Usuario usuarioLogado = Usuario.builder()
            .id(idUsuario)
            .nome(NOME_USUARIO)
            .email(EMAIL_USUARIO)
            .senha(SENHA_USUARIO)
            .redesSociais(Collections.singletonList(
                RedeSocial.builder()
                    .nome(REDES_SOCIAIS)
                    .link(LINK)
                    .build()
            ))
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuarioLogado);
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioLogado));

        UsuarioPerfilResponse usuarioPerfilResponse = usuarioService.buscarPorId(idUsuario);

        assertTrue(usuarioPerfilResponse.isUsuarioLogado());
        assertNull(usuarioPerfilResponse.getStatusRelacionamento());
        assertEquals(usuarioLogado.getId(), usuarioPerfilResponse.getId());
        assertEquals(usuarioLogado.getNome(), usuarioPerfilResponse.getNome());
        assertEquals(usuarioLogado.getEmail(), usuarioPerfilResponse.getEmail());
        assertEquals(1, usuarioPerfilResponse.getRedesSociais().size());
        assertEquals(REDES_SOCIAIS, usuarioPerfilResponse.getRedesSociais().getFirst().getNome());
        assertEquals(LINK, usuarioPerfilResponse.getRedesSociais().getFirst().getLink());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário quando id do usuário inválido")
    public void deveLancarExcecaoAoBuscarUsuarioNaoEncontrado() {
        UUID idUsuario = UUID.randomUUID();

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.buscarPorId(idUsuario));
    }

    @Test
    @DisplayName("Deve adicionar imagem do usuário com formato aceito")
    public void deveAdicionarImagemUsuario() {
        byte[] imagemBytes = "ana".getBytes();
        String tipoImagem = "image/jpeg";

        MultipartFile imagem = new MockMultipartFile(
            "file",
            "ana.jpg",
            tipoImagem,
            imagemBytes
        );

        String imagemBase64 = Base64.getEncoder().encodeToString(imagemBytes);

        Usuario usuario = Usuario.builder()
            .id(ID_USUARIO)
            .nome(NOME_USUARIO)
            .email(EMAIL_USUARIO)
            .imagem(imagemBase64)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse usuarioResponse = usuarioService.adicionarImagem(imagem);

        assertEquals(usuario.getId(), usuarioResponse.getId());
        assertEquals(usuario.getNome(), usuarioResponse.getNome());
        assertEquals(usuario.getEmail(), usuarioResponse.getEmail());
        assertEquals(usuario.getImagem(), usuarioResponse.getImagem());
    }

    @Test
    @DisplayName("Deve lançar exceção para formato de imagem não aceito")
    public void deveLancarExcecaoParaFormatoImagemNaoAceito() {
        byte[] imagemBytes = "ana.svg".getBytes();

        MultipartFile imagem = new MockMultipartFile(
            "ana",
            "ana.svg",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            imagemBytes);

        Usuario usuario = Usuario.builder()
            .id(ID_USUARIO)
            .build();

        when(tokenService.obterUsuarioToken()).thenReturn(usuario);

        assertThrows(ErroFormatoImagemUsuarioNaoAceitoException.class, () -> {
            usuarioService.adicionarImagem(imagem);
        });
    }

}