package com.codeconnect.security.config;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain filtroDeSegurancaChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Iniciando configuracao da cadeia de filtros de segurança");

        SecurityFilterChain filtroDeSeguranca = httpSecurity
            .cors(cors -> cors.configurationSource(configuracaoCors()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(autorizacaoRequisicoes -> {
                autorizacaoRequisicoes.requestMatchers(HttpMethod.POST, "/login").permitAll();
                autorizacaoRequisicoes.requestMatchers(HttpMethod.POST, "/usuarios").permitAll();
                autorizacaoRequisicoes.requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**").permitAll();

                autorizacaoRequisicoes.anyRequest().authenticated();
            })
            .sessionManagement(configuracaoSessao -> configuracaoSessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException)
                -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            )
            .build();

        log.info("Cadeia de filtros de segurança configurada com sucesso");

        return filtroDeSeguranca;
    }

    @Bean
    public AuthenticationManager gerenciadorDeAutenticacao(AuthenticationConfiguration configuracao) throws Exception {
        return configuracao.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }

    public CorsConfigurationSource configuracaoCors() {
        CorsConfiguration  configuracao = new CorsConfiguration();
        configuracao.setAllowCredentials(true);
        configuracao.setAllowedOrigins(List.of("http://localhost:5500"));
        configuracao.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource urlBaseFonteConfiguracao = new UrlBasedCorsConfigurationSource();
        urlBaseFonteConfiguracao.registerCorsConfiguration("/**", configuracao);

        return urlBaseFonteConfiguracao;
    }
}