package com.codeconnect.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.codeconnect.security.exception.ErroAoCriarTokenException;
import com.codeconnect.security.exception.TokenInvalidoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenService {

    @Value("{api.security.token.secret}")
    private String tokenSenha;

    private static final String EMISSOR = "codeconnect-api";

    public String gerarToken(UsuarioDetailsImpl usuario) {
        log.info("Iniciando geração do token");

        try {
            var assinatura = Algorithm.HMAC256(tokenSenha);

            var token = JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getUsername())
                .sign(assinatura);

            log.info("Token gerado com sucesso");

            return token;
        } catch (Exception exception) {
            log.error("Erro ao gerar token", exception);

            throw new ErroAoCriarTokenException();
        }
    }

    public String obterAssuntoDoToken(String token) {
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

            throw new TokenInvalidoException();
        }
    }

}