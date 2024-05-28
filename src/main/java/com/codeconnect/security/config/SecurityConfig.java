package com.codeconnect.security.config;

import com.codeconnect.security.exception.ErroConfiguracaoSegurancaException;
import com.codeconnect.security.exception.ErroGerenciamentoDeAutenticaoException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain filtroDeSegurancaChain(HttpSecurity httpSecurity) {
        try {
            log.info("Iniciando configuracao da cadeia de filtros de segurança");

            SecurityFilterChain filtroDeSeguranca = httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(autorizacaoRequisicoes -> {
                    autorizacaoRequisicoes.requestMatchers(HttpMethod.POST, "/login").permitAll();
                    autorizacaoRequisicoes.requestMatchers(HttpMethod.POST, "/usuarios").permitAll();
                    autorizacaoRequisicoes.anyRequest().authenticated();
                })
                .sessionManagement(configuracaoSessao -> configuracaoSessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

                .build();

            log.info("Cadeia de filtros de segurança configurada com sucesso");

            return filtroDeSeguranca;
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new ErroConfiguracaoSegurancaException();
        }
    }

    @Bean
    public AuthenticationManager gerenciadorDeAutenticacao(AuthenticationConfiguration configuracao) {
        try {
            return configuracao.getAuthenticationManager();
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new ErroGerenciamentoDeAutenticaoException();
        }
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }

}