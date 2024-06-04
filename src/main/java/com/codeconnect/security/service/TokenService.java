package com.codeconnect.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.codeconnect.login.dto.LoginResponse;
import com.codeconnect.security.exception.ErroAoCriarTokenException;
import com.codeconnect.security.exception.ErroTokenInvalidoException;
import com.codeconnect.security.model.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class TokenService {

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

            log.info("Token gerado com sucesso");

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

            var assunto = JWT.require(assinatura)
                .withIssuer(EMISSOR)
                .build()
                .verify(token)
                .getSubject();

            log.info("Assunto do token obtido com sucesso");

            return assunto;
        } catch (Exception exception) {
            log.error("Erro ao obter o assunto do token");

            throw new ErroTokenInvalidoException();
        }
    }

}