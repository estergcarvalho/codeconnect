package com.codeconnect.login.service;

import com.codeconnect.login.dto.LoginRequest;
import com.codeconnect.login.dto.LoginResponse;
import com.codeconnect.security.exception.ErroAoCriarTokenException;
import com.codeconnect.security.model.UserDetailsImpl;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.model.Usuario;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class LoginServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private TokenService tokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private static final String EMAIL_VALIDO = "teste@teste.com";
    private static final String SENHA_VALIDA = "$2y$10$9S9ivlvoxeZX8.UQx4PiReUle758Ux8py.Os.YACoQOaZtv6e0vdK";

    @Test
    @DisplayName("Deve autenticar usuário e retornar token")
    public void deveAutenticarUsuarioERetornarToken() {
        LoginRequest loginRequest = new LoginRequest(EMAIL_VALIDO, SENHA_VALIDA);

        UserDetailsImpl userDetails = new UserDetailsImpl(new Usuario());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        LoginResponse response = loginService.autenticarUsuario(loginRequest);

        assertNotNull(response);
        assertFalse(response.getAccessToken().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar autenticação")
    public void deveLancarExcecaoAoFalharAutenticacao() {
        LoginRequest loginRequest = new LoginRequest(EMAIL_VALIDO, SENHA_VALIDA);

        when(authenticationManager.authenticate(any())).thenThrow(new ErroAoCriarTokenException());

        assertThrows(RuntimeException.class, () -> loginService.autenticarUsuario(loginRequest));
    }

}