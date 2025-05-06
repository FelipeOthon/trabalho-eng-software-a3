package com.exemplo.trabalho.repository;

import com.exemplo.trabalho.model.Requisicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RequisicaoRepository extends JpaRepository<Requisicao, Long> { // ID_Requisicao (Long) é a PK

    List<Requisicao> findByRequerenteCpf(String cpf);

    List<Requisicao> findByCategoriaIdCategoria(Integer idCategoria);
}