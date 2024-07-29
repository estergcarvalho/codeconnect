package com.codeconnect.usuario.service;

import com.codeconnect.redesocial.dto.RedeSocialResponse;
import com.codeconnect.redesocial.model.RedeSocial;
import com.codeconnect.security.service.TokenService;
import com.codeconnect.usuario.dto.UsuarioAmigoDetalheResponse;
import com.codeconnect.usuario.dto.UsuarioAmigoResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResponse;
import com.codeconnect.usuario.dto.UsuarioEditarResquest;
import com.codeconnect.usuario.dto.UsuarioPerfilResponse;
import com.codeconnect.usuario.dto.UsuarioResponse;
import com.codeconnect.usuario.dto.UsuarioResquest;
import com.codeconnect.usuario.enums.UsuarioAmigoStatusEnum;
import com.codeconnect.usuario.exception.ErroAoAdicionarImagemUsuarioException;
import com.codeconnect.usuario.exception.ErroAoCadastrarUsuarioException;
import com.codeconnect.usuario.exception.ErroFormatoImagemUsuarioNaoAceitoException;
import com.codeconnect.usuario.exception.UsuarioJaExistenteException;
import com.codeconnect.usuario.exception.UsuarioNaoEncontradoException;
import com.codeconnect.usuario.model.Usuario;
import com.codeconnect.usuario.repository.UsuarioAmigoRepository;
import com.codeconnect.usuario.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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
        log.info("Inicio listagem de amigos");

        Usuario usuario = tokenService.obterUsuarioToken();

        List<UsuarioAmigoDetalheResponse> usuarioAmigoDetalheResponse = usuario.getAmigos().stream()
            .filter(status -> status.getStatus() == UsuarioAmigoStatusEnum.AMIGO)
            .map(amigo -> UsuarioAmigoDetalheResponse.builder()
                .nome(amigo.getAmigo().getNome())
                .idAmigo(amigo.getAmigo().getId())
                .statusRelacionamento(amigo.getStatus())
                .build())
            .toList();

        int totalAmigos = usuarioAmigoDetalheResponse.size();

        log.info("Listagem de amigos concluída com sucesso");

        return UsuarioAmigoResponse.builder()
            .amigos(usuarioAmigoDetalheResponse)
            .total(totalAmigos)
            .build();
    }

    public UsuarioEditarResponse editar(UsuarioEditarResquest usuarioEditarResquest) {
        log.info("Inicio edição de usuario");

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

        log.info("Usuario {} editado com sucesso", usuario.getNome());

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

    public UsuarioPerfilResponse buscarPorId(UUID idUsuario) {
        log.info("Inicio busca de usuario por ID");

        Usuario usuario = repository.findById(idUsuario)
            .orElseThrow(UsuarioNaoEncontradoException::new);

        Usuario usuarioLogado = tokenService.obterUsuarioToken();

        boolean isUsuarioLogado = usuarioLogado.getId().equals(idUsuario);

        UsuarioAmigoStatusEnum statusRelacionamento = null;

        if (!isUsuarioLogado) {
            List<UsuarioAmigoDetalheResponse> amigos = this.listarRelacionamentos().getAmigos();

            for (UsuarioAmigoDetalheResponse amigoDetalheResponse : amigos) {
                if (idUsuario.equals(amigoDetalheResponse.getIdAmigo())) {
                    statusRelacionamento = amigoDetalheResponse.getStatusRelacionamento();

                    log.info("Status de relacionamento encontrado: {}", statusRelacionamento);
                }
            }
        }

        List<RedeSocialResponse> redeSocialResponses = usuario.getRedesSociais().stream()
            .map(redeSocial -> RedeSocialResponse.builder()
                .nome(redeSocial.getNome())
                .link(redeSocial.getLink())
                .build())
            .toList();

        return UsuarioPerfilResponse.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .email(usuario.getEmail())
            .profissao(usuario.getProfissao())
            .estado(usuario.getEstado())
            .pais(usuario.getPais())
            .usuarioLogado(isUsuarioLogado)
            .statusRelacionamento(statusRelacionamento)
            .redesSociais(redeSocialResponses)
            .imagem(usuario.getImagem())
            .build();
    }

    public UsuarioResponse adicionarImagem(MultipartFile imagem) {
        log.info("Inicio adicionar imagem do usuario");

        try {
            String formatoImagem = imagem.getContentType();
            List<String> formatosAceitos = Arrays.asList("image/jpg", "image/jpeg", "image/png", "image/gif");

            if (!formatosAceitos.contains(formatoImagem)) {
                throw new ErroFormatoImagemUsuarioNaoAceitoException();
            }

            Usuario usuario = tokenService.obterUsuarioToken();

            String imagemUsuario = Base64.getEncoder().encodeToString(imagem.getBytes());
            usuario.setImagem(imagemUsuario);
            usuario.setTipoImagem(formatoImagem);
            repository.save(usuario);

            return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .imagem(imagemUsuario)
                .tipoImagem(formatoImagem)
                .build();
        } catch (ErroFormatoImagemUsuarioNaoAceitoException exception) {
            log.error("Erro formato de imagem não aceito: {}", exception.getMessage());

            throw new ErroFormatoImagemUsuarioNaoAceitoException();
        } catch (Exception exception) {
            log.error("Erro ao adicionar imagem do usuario: {}", exception.getMessage());

            throw new ErroAoAdicionarImagemUsuarioException();
        }

    }

    private UsuarioAmigoResponse listarRelacionamentos() {
        log.info("Inicio listagem de relacionamentos de usuario");

        Usuario usuario = tokenService.obterUsuarioToken();

        List<UsuarioAmigoDetalheResponse> usuarioAmigoDetalheResponse = usuario.getAmigos().stream()
            .map(amigo -> UsuarioAmigoDetalheResponse.builder()
                .nome(amigo.getAmigo().getNome())
                .idAmigo(amigo.getAmigo().getId())
                .statusRelacionamento(amigo.getStatus())
                .build())
            .toList();

        int totalAmigos = usuarioAmigoDetalheResponse.size();

        log.info("Listagem de relacionamentos de usuario concluída com sucesso");

        return UsuarioAmigoResponse.builder()
            .amigos(usuarioAmigoDetalheResponse)
            .total(totalAmigos)
            .build();
    }

}