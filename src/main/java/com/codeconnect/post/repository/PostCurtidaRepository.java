package com.codeconnect.post.repository;

import com.codeconnect.post.model.Post;
import com.codeconnect.post.model.PostCurtida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostCurtidaRepository extends JpaRepository<PostCurtida, UUID> {

    Optional<PostCurtida> findByPostIdAndUsuarioId(UUID postId, UUID usuarioId);

    long countByPost(Post post);

}