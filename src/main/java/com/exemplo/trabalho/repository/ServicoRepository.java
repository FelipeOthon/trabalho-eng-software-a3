package com.exemplo.trabalho.repository;

import com.exemplo.trabalho.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> { // ID_Servico (Long) é a PK

    // Buscar serviços de um prestador específico pelo CPF do prestador
    List<Servico> findByPrestadorCpf(String cpf);

    // Buscar serviços de uma categoria específica pelo ID da categoria
    List<Servico> findByCategoriaIdCategoria(Integer idCategoria);

    // Exemplo: Buscar serviços cuja descrição contenha um termo (ignorando maiúsculas/minúsculas)
    // List<Servico> findByDescricaoContainingIgnoreCase(String termo);
}