package com.exemplo.trabalho.repository;

import com.exemplo.trabalho.model.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PrestadorRepository extends JpaRepository<Prestador, String> { // CPF (String) é a PK

    // Método para buscar um prestador pelo email (útil para evitar duplicidade ou para login)
    Optional<Prestador> findByEmail(String email);
}