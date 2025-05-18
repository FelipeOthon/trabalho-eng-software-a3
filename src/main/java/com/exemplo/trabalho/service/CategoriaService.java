package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria salvarCategoria(Categoria categoria) {
        // Poderia adicionar validações aqui antes de salvar
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodasCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    public void deletarCategoria(Integer id) {
        categoriaRepository.deleteById(id);
    }

    // Outros métodos conforme necessário...
}