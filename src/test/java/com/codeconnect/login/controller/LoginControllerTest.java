package com.codeconnect.login.controller;

import com.codeconnect.login.dto.LoginRequest;
import com.codeconnect.login.service.LoginService;
import com.codeconnect.security.model.UserDetailsImpl;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoginService loginService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private static final String EMAIL_VALIDO = "teste@teste.com";
    private static final String SENHA_VALIDA = "$2y$10$9S9ivlvoxeZX8.UQx4PiReUle758Ux8py.Os.YACoQOaZtv6e0vdK";

    @Test
    @DisplayName("Deve autenticar o usuário e retornar o token")
    public void deveAutenticarUsuarioERetornarToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest(EMAIL_VALIDO, SENHA_VALIDA);

        Usuario usuario = Usuario.builder()
            .email(EMAIL_VALIDO)
            .senha(SENHA_VALIDA)
            .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(new Usuario());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString());
    }

}