package com.codeconnect.atividaderecente.repository;

import com.codeconnect.atividaderecente.model.AtividadeRecente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AtividadeRecenteRepository extends JpaRepository<AtividadeRecente, UUID> {

}