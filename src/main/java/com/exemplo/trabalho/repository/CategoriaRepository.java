package com.exemplo.trabalho.repository;

import com.exemplo.trabalho.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Opcional, para métodos de busca personalizados

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // JpaRepository<NomeDaEntidade, TipoDaChavePrimariaDaEntidade>

    // Exemplo de método de consulta personalizado (opcional):
    // List<Categoria> findByNomeCategoriaContainingIgnoreCase(String nomeFragmento);
}