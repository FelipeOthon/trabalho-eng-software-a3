package com.exemplo.trabalho.repository;

import com.exemplo.trabalho.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> { // idEmpresa (Long) é a PK

    Optional<Empresa> findByCnpj(String cnpj);

    List<Empresa> findByNomeContainingIgnoreCase(String nome);
}