package com.exemplo.trabalho.repository;

import com.exemplo.trabalho.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> { // id (Long) é a PK

    // Essencial para o Spring Security carregar o usuário pelo nome de usuário
    Optional<Usuario> findByUsername(String username);
}