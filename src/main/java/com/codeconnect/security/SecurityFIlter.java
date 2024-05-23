package com.codeconnect.security;

import com.codeconnect.security.exception.ErroAoAutenticarUsuarioException;
import com.codeconnect.security.exception.ErroAoRecuperarTokenExpection;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class SecurityFIlter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Inicio filtrar requisicao");

            var tokenJwt = recuperarToken(request);

            if (tokenJwt != null) {
                String assunto = tokenService.obterAssuntoDoToken(tokenJwt);

                Usuario usuario = usuarioRepository.findByEmail(assunto).orElseThrow(UsuarioNaoEncontradoException::new);

                UsuarioDetailsImpl usuarioDetalhe = new UsuarioDetailsImpl(usuario);
                UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(usuarioDetalhe, null, usuarioDetalhe.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(autenticacao);

                log.info("Autenticação realizada com sucesso");
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            throw new ErroAoAutenticarUsuarioException();
        }

    }

    private String recuperarToken(HttpServletRequest request) {
        try {
            var autorizacao = request.getHeader("Authorization");

            if (autorizacao != null) {
                return autorizacao.replace("Bearer", "");
            }

            log.info("Cabeçalho 'Authorization' não encontrado ou vazio.");

            return null;

        } catch (Exception exception) {
            throw new ErroAoRecuperarTokenExpection();
        }
    }

}