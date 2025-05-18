package com.exemplo.trabalho.repository;

import com.exemplo.trabalho.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Certifique-se que este import existe
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByPrestadorCpf(String cpf);

    List<Servico> findByCategoriaIdCategoria(Integer idCategoria);

    // Este é o método para buscar todos os serviços com prestador e categoria carregados
    // Certifique-se de que ele NÃO tem parâmetros se a query JPQL não os usa.
    @Query("SELECT s FROM Servico s LEFT JOIN FETCH s.prestador p LEFT JOIN FETCH s.categoria c")
    List<Servico> findAllWithPrestadorAndCategoria(); // << SEM PARÂMETRO INTEGER AQUI

    // Se você tinha uma versão deste método com um parâmetro Integer, remova ou corrija-a.
    // Exemplo de como seria SE você quisesse filtrar por ID de categoria com JOIN FETCH:
    // @Query("SELECT s FROM Servico s LEFT JOIN FETCH s.prestador p LEFT JOIN FETCH s.categoria c WHERE c.idCategoria = :idCategoria")
    // List<Servico> findAllWithPrestadorAndCategoriaByCategoriaId(@Param("idCategoria") Integer idCategoria);
}