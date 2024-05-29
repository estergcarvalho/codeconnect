package com.codeconnect.login.service;

import com.codeconnect.login.dto.LoginRequest;
import com.codeconnect.login.dto.LoginResponse;
import com.codeconnect.security.exception.ErroAoCriarTokenException;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.security.model.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public LoginResponse autenticarUsuario(LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken tokenDeAutenticacao =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha());

            Authentication autenticacao = authenticationManager.authenticate(tokenDeAutenticacao);

            UserDetailsImpl detalhesDoUsuario = (UserDetailsImpl) autenticacao.getPrincipal();

            String token = tokenService.gerarToken(detalhesDoUsuario);
            Instant expiracao = tokenService.expiracaoToken();

            return LoginResponse.builder()
                .acesso_token(token)
                .tipo_token("Bearer")
                .expira_em(expiracao.getEpochSecond())
                .build();
        } catch (Exception exception) {
            log.error("Erro ao gerar token do usuario: {}", exception.getMessage());

            throw new ErroAoCriarTokenException();
        }
    }

}