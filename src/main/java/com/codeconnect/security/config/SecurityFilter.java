package com.codeconnect.security.config;

import com.codeconnect.security.model.UserDetailsImpl;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.exception.UsuarioNaoEncontradoException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Inicio filtrar requisicao");

        var tokenJwt = recuperarToken(request);

        if (tokenJwt != null) {
            String assunto = tokenService.validarToken(tokenJwt);

            Usuario usuario = usuarioRepository.findByEmail(assunto)
                .orElseThrow(UsuarioNaoEncontradoException::new);

            UserDetailsImpl usuarioDetalhe = new UserDetailsImpl(usuario);
            UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(usuarioDetalhe, null, usuarioDetalhe.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(autenticacao);

            log.info("Autenticação realizada com sucesso");
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var autorizacao = request.getHeader("Authorization");

        if (autorizacao != null) {
            return autorizacao.replace("Bearer ", "");
        }

        log.error("Cabeçalho 'Authorization' não encontrado ou vazio.");

        return null;
    }

}