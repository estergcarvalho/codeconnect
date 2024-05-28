package com.codeconnect.usuario.service;

import com.codeconnect.usuario.dto.UsuarioResponse;
import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.repository.UsuarioRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private static final UUID ID_USUARIO = UUID.randomUUID();
    private static final String NOME_USUARIO = "Teste";
    private static final String EMAIL_USUARIO = "teste@teste.com.br";
    private static final String SENHA_USUARIO = "$2y$10$9S9ivlvoxeZX8.UQx4PiReUle758Ux8py.Os.YACoQOaZtv6e0vdK";

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

        when(passwordEncoder.encode(usuarioRequest.getSenha())).thenReturn(SENHA_USUARIO);
        when(usuarioRepository.save(any())).thenReturn(usuario);

        UsuarioResponse usuarioResponse = usuarioService.cadastrar(usuarioRequest);

        assertEquals(ID_USUARIO, usuarioResponse.getId());
        assertEquals(NOME_USUARIO, usuarioResponse.getNome());
        assertEquals(EMAIL_USUARIO, usuarioResponse.getEmail());
    }

}