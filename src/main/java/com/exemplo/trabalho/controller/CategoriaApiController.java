package com.exemplo.trabalho.controller; // ou com.exemplo.trabalho.controller.api

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categorias") // Novo endpoint base para categorias
@CrossOrigin(origins = "*") // Ajuste para produção
public class CategoriaApiController {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaApiController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Endpoint para LISTAR todas as categorias (usado pelo filtro do painel)
    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodasCategorias() {
        List<Categoria> categorias = categoriaService.listarTodasCategorias();
        if (categorias.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content se a lista estiver vazia
        }
        return ResponseEntity.ok(categorias);
    }

    // Endpoint para BUSCAR uma categoria por ID (opcional, mas útil)
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoriaPorId(@PathVariable Integer id) {
        Optional<Categoria> categoria = categoriaService.buscarCategoriaPorId(id);
        return categoria.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para CRIAR uma nova categoria (opcional, se você precisar de um admin para isso)
    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@RequestBody Categoria categoria) {
        try {
            Categoria novaCategoria = categoriaService.salvarCategoria(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
        } catch (Exception e) { // Pode ser uma DataIntegrityViolationException se o nome for único e já existir
            // Logar o erro: e.printStackTrace();
            return ResponseEntity.badRequest().build(); // Ou uma resposta mais detalhada
        }
    }

    // Você poderia adicionar endpoints para ATUALIZAR (@PutMapping) e DELETAR (@DeleteMapping) categorias
    // se houver uma interface administrativa para gerenciá-las.
}