package com.codeconnect.login.service;

import com.codeconnect.login.dto.LoginRequest;
import com.codeconnect.login.dto.LoginResponse;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.security.model.UserDetailsImpl;
import com.codeconnect.security.exception.ErroAoRecuperarTokenExpection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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

            return new LoginResponse(tokenService.gerarToken(detalhesDoUsuario));
        } catch (Exception e) {
            log.error("Erro ao gerar token do usuario: {}", e.getMessage());

            throw new ErroAoRecuperarTokenExpection();
        }
    }

}