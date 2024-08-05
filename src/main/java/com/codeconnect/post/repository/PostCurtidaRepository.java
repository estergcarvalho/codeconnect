package com.codeconnect.post.repository;

import com.codeconnect.post.model.Post;
import com.codeconnect.post.model.PostCurtida;
import com.codeconnect.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostCurtidaRepository extends JpaRepository<PostCurtida, UUID> {

    boolean existsByPostAndUsuario(Post post, Usuario usuario);

    long countByPost(Post post);

}