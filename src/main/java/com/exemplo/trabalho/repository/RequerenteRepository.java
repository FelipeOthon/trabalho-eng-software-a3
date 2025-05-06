package com.exemplo.trabalho.repository;

import com.exemplo.trabalho.model.Requerente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RequerenteRepository extends JpaRepository<Requerente, String> { // CPF (String) é a PK

    Optional<Requerente> findByEmail(String email);
}