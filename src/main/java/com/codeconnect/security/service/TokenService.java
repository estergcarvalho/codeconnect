package com.codeconnect.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.codeconnect.login.dto.LoginResponse;
import com.codeconnect.security.exception.ErroAoCriarTokenException;
import com.codeconnect.security.exception.ErroTokenInvalidoException;
import com.codeconnect.security.model.UserDetailsImpl;
import com.codeconnect.usuario.exception.UsuarioNaoEncontradoException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class TokenService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${api.security.token.secret}")
    private String tokenSenha;

    private static final String EMISSOR = "codeconnect-api";

    public LoginResponse gerarToken(UserDetailsImpl usuario) {
        log.info("Iniciando geração do token");

        try {
            var assinatura = Algorithm.HMAC256(tokenSenha);
            var tempoExpiracaoToken = 3600L;
            var expiracaoToken = Instant.now().plusSeconds(tempoExpiracaoToken);

            var token = JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getUsername())
                .withExpiresAt(expiracaoToken)
                .sign(assinatura);

            return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(tempoExpiracaoToken)
                .build();
        } catch (Exception exception) {
            log.error("Erro ao gerar token", exception);

            throw new ErroAoCriarTokenException();
        }
    }

    public String validarToken(String token) {
        log.info("Iniciando a obtenção do assunto do token");

        try {
            var assinatura = Algorithm.HMAC256(tokenSenha);

            return JWT.require(assinatura)
                .withIssuer(EMISSOR)
                .build()
                .verify(token)
                .getSubject();
        } catch (Exception exception) {
            log.error("Erro ao obter token");

            throw new ErroTokenInvalidoException();
        }
    }

    public Usuario obterUsuarioToken() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        String usuarioEmail = autenticacao.getName();

        return usuarioRepository.findByEmail(usuarioEmail)
            .orElseThrow(UsuarioNaoEncontradoException::new);
    }

}