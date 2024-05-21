package com.codeconnect.service;

import com.codeconnect.repository.UsuarioRepository;
import com.codeconnect.dto.UsuarioResponse;
import com.codeconnect.dto.UsuarioResquest;
import com.codeconnect.exception.UsuarioNaoEncontradoException;
import com.codeconnect.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioResponse cadastrar(UsuarioResquest usuarioResquest) {
        log.info("Inicio cadastro usuario");

        try {
            Usuario usuario = Usuario.builder()
                .nome(usuarioResquest.getNome())
                .email(usuarioResquest.getEmail())
                .senha(usuarioResquest.getSenha())
                .build();

            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            log.info("Usuario {} cadastrado com sucesso", usuario.getNome());

            return UsuarioResponse.builder()
                .id(usuarioSalvo.getId())
                .nome(usuarioSalvo.getNome())
                .email(usuarioSalvo.getEmail())
                .senha(usuarioSalvo.getSenha())
                .build();
        } catch (Exception e) {
            log.error("Erro ao cadastrar usuario: {}", e.getMessage());

            throw new UsuarioNaoEncontradoException();
        }
    }

}