package com.codeconnect.usuario.controller;

import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID ID_USUARIO = UUID.randomUUID();
    private static final String NOME_USUARIO = "Teste";
    private static final String EMAIL_USUARIO = "teste@teste.com.br";
    private static final String SENHA_USUARIO = "$2y$10$9S9ivlvoxeZX8.UQx4PiReUle758Ux8py.Os.YACoQOaZtv6e0vdK";

    @Test
    @DisplayName("Deve cadastrar um usuário")
    public void deveCadastrarUsuario() throws Exception {
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

        when(usuarioRepository.save(any())).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(ID_USUARIO.toString()))
            .andExpect(jsonPath("$.nome").value(NOME_USUARIO))
            .andExpect(jsonPath("$.email").value(EMAIL_USUARIO));
    }

}