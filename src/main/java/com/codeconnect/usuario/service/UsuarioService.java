package com.codeconnect.usuario.service;

import com.codeconnect.redesocial.dto.RedeSocialResponse;
import com.codeconnect.redesocial.model.RedeSocial;
import com.codeconnect.redesocial.repository.RedeSocialRepository;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.dto.UsuarioAmigoDetalheResponse;
import com.codeconnect.usuario.dto.UsuarioAmigoResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResquest;
import com.codeconnect.usuario.dto.UsuarioResponse;
import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum;
import com.codeconnect.usuario.exception.ErroAoCadastrarUsuarioException;
import com.codeconnect.usuario.exception.UsuarioJaExistenteException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.repository.UsuarioAmigoRepository;
import com.codeconnect.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioAmigoRepository usuarioAmigoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public UsuarioResponse cadastrar(UsuarioResquest usuarioResquest) {
        log.info("Inicio cadastro usuario");

        if (repository.findByEmail(usuarioResquest.getEmail()).isPresent()) {
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

            Usuario usuarioSalvo = repository.save(usuario);

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

    public UsuarioAmigoResponse listarAmigos() {
        Usuario usuario = tokenService.obterUsuarioToken();

        List<UsuarioAmigoDetalheResponse> usuarioAmigoDetalheResponse = usuario.getAmigos().stream()
            .filter(status -> status.getStatus() == UsuarioAmigoStatusEnum.AMIGO)
            .map(amigo -> UsuarioAmigoDetalheResponse.builder()
                .nome(amigo.getAmigo().getNome())
                .build())
            .collect(toList());

        int totalAmigos = usuarioAmigoDetalheResponse.size();

        return UsuarioAmigoResponse.builder()
            .amigos(usuarioAmigoDetalheResponse)
            .total(totalAmigos)
            .build();
    }

    public UsuarioEditarResponse editar(UsuarioEditarResquest usuarioEditarResquest) {
        Usuario usuario = tokenService.obterUsuarioToken();

        usuario.setProfissao(usuarioEditarResquest.getProfissao());
        usuario.setPais(usuarioEditarResquest.getPais());
        usuario.setEstado(usuarioEditarResquest.getEstado());

        List<RedeSocial> redesSociaisRequest = usuarioEditarResquest.getRedesSociais().stream()
            .map(redeSocial -> RedeSocial.builder()
                .usuario(usuario)
                .nome(redeSocial.getNome())
                .link(redeSocial.getLink())
                .build())
            .toList();

        usuario.getRedesSociais().clear();
        usuario.getRedesSociais().addAll(redesSociaisRequest);

        Usuario usuarioEditado = repository.save(usuario);

        return UsuarioEditarResponse.builder()
            .profissao(usuarioEditado.getProfissao())
            .pais(usuarioEditado.getPais())
            .estado(usuarioEditado.getEstado())
            .redesSociais(usuarioEditado.getRedesSociais().stream()
                .map(redeSocial -> RedeSocialResponse.builder()
                    .nome(redeSocial.getNome())
                    .link(redeSocial.getLink())
                    .build())
                .toList())
            .build();
    }

}