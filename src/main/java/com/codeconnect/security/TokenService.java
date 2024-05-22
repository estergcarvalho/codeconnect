package com.codeconnect.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.codeconnect.security.exception.ErroAoCriarTokenException;
import com.codeconnect.security.exception.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("{api.security.token.secret}")
    private String tokenSenha;

    private static final String EMISSOR = "codeconnect-api";

    public String gerarToken(UsuarioDetailsImpl usuario) throws ErroAoCriarTokenException {
        try {
            var assinatura = Algorithm.HMAC256(tokenSenha);

            return JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(usuario.getUsername())
                .sign(assinatura);
        } catch (Exception exception) {
            throw new ErroAoCriarTokenException();
        }
    }

    public String obterAssuntoDoToken(String token) throws TokenInvalidoException {
        try {
            var assinatura = Algorithm.HMAC256(tokenSenha);

            return JWT.require(assinatura)
                .withIssuer(EMISSOR)
                .build()
                .verify(token)
                .getSubject();
        } catch (Exception exception) {
            throw new TokenInvalidoException();
        }
    }

}