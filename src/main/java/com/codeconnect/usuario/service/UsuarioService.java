package com.codeconnect.usuario.service;

import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.dto.AmigoDetalheResponse;
import com.codeconnect.usuario.dto.AmigoResponse;
import com.codeconnect.usuario.dto.UsuarioResponse;
import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.exception.ErroAoCadastrarUsuarioException;
import com.codeconnect.usuario.exception.UsuarioJaExistenteException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.repository.AmigoRepository;
import com.codeconnect.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AmigoRepository amigoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public UsuarioResponse cadastrar(UsuarioResquest usuarioResquest) {
        log.info("Inicio cadastro usuario");

        if (usuarioRepository.findByEmail(usuarioResquest.getEmail()).isPresent()) {
            log.error("Usuario com email {} já existe", usuarioResquest.getEmail());

            throw new UsuarioJaExistenteException();
        }

        try {
            String senha = passwordEncoder.encode(usuarioResquest.getSenha());

            Usuario usuario = Usuario.builder()
                .nome(usuarioResquest.getNome())
                .email(usuarioResquest.getEmail())
                .senha(senha)
                .build();

            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            log.info("Usuario {} cadastrado com sucesso", usuario.getNome());

            return UsuarioResponse.builder()
                .id(usuarioSalvo.getId())
                .nome(usuarioSalvo.getNome())
                .email(usuarioSalvo.getEmail())
                .build();
        } catch (Exception exception) {
            log.error("Erro ao cadastrar usuario: {}", exception.getMessage());

            throw new ErroAoCadastrarUsuarioException();
        }
    }

    public AmigoResponse listarAmigos() {
        Usuario usuario = tokenService.obterUsuarioToken();

        List<AmigoDetalheResponse> amigoDetalheResponse = usuario.getUsuarioAmigos().stream()
            .map(amigo -> AmigoDetalheResponse.builder()
                .nome(amigo.getAmigo().getNome())
                .build())
            .collect(Collectors.toList());

        int totalAmigos = amigoDetalheResponse.size();

        return AmigoResponse.builder()
            .amigos(amigoDetalheResponse)
            .total(totalAmigos)
            .build();
    }

}