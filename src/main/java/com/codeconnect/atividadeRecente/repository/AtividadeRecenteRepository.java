package com.codeconnect.atividadeRecente.repository;

import com.codeconnect.atividadeRecente.model.AtividadeRecente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AtividadeRecenteRepository extends JpaRepository<AtividadeRecente, UUID> {

}