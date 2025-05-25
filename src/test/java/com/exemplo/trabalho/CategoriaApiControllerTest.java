// Local: src/test/java/com/exemplo/trabalho/controller/CategoriaApiControllerTest.java
package com.exemplo.trabalho.controller;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.service.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper; // Para converter objetos Java <-> JSON
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Para "mockar" o CategoriaService
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is; // Para fazer asserções no JSON
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // Para GET, POST, etc.
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // Para imprimir detalhes no console
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // Para status(), jsonPath(), etc.

@WebMvcTest(CategoriaApiController.class) // Diz ao Spring Boot para focar apenas na camada web (este controller)
        // e não carregar toda a aplicação.
class CategoriaApiControllerTest {

    @Autowired
    private MockMvc mockMvc; // Ferramenta principal para simular requisições HTTP

    @MockBean // Cria um mock do CategoriaService e o injeta no contexto do Spring para este teste
    private CategoriaService categoriaServiceMock;

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos Java para String JSON e vice-versa

    private Categoria categoriaExemplo1;
    private Categoria categoriaExemplo2;

    @BeforeEach
    void setUp() {
        categoriaExemplo1 = new Categoria();
        categoriaExemplo1.setIdCategoria(1);
        categoriaExemplo1.setNomeCategoria("Front-End");

        categoriaExemplo2 = new Categoria();
        categoriaExemplo2.setIdCategoria(2);
        categoriaExemplo2.setNomeCategoria("Back-End");
    }

    @DisplayName("GET /api/v1/categorias - Deve retornar lista de categorias")
    @Test
    void testListarTodasCategorias_Api() throws Exception { // "throws Exception" é necessário para mockMvc.perform
        // Arrange
        List<Categoria> listaDeCategorias = Arrays.asList(categoriaExemplo1, categoriaExemplo2);
        given(categoriaServiceMock.listarTodasCategorias()).willReturn(listaDeCategorias);

        // Act: Simula uma requisição GET para "/api/v1/categorias"
        ResultActions response = mockMvc.perform(get("/api/v1/categorias"));

        // Assert: Verifica a resposta HTTP
        response.andExpect(status().isOk()) // Espera status HTTP 200 OK
                .andDo(print()) // Imprime os detalhes da requisição/resposta (bom para debug)
                .andExpect(jsonPath("$.size()", is(listaDeCategorias.size()))) // Verifica o tamanho do array JSON
                .andExpect(jsonPath("$[0].nomeCategoria", is(categoriaExemplo1.getNomeCategoria()))) // Verifica o nome da primeira categoria
                .andExpect(jsonPath("$[1].nomeCategoria", is(categoriaExemplo2.getNomeCategoria()))); // Verifica o nome da segunda
    }

    @DisplayName("GET /api/v1/categorias/{id} - Deve retornar uma categoria pelo ID")
    @Test
    void testBuscarCategoriaPorId_Api_Existente() throws Exception {
        // Arrange
        Integer categoriaId = 1;
        given(categoriaServiceMock.buscarCategoriaPorId(categoriaId)).willReturn(Optional.of(categoriaExemplo1));

        // Act
        ResultActions response = mockMvc.perform(get("/api/v1/categorias/{id}", categoriaId));

        // Assert
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.idCategoria", is(categoriaExemplo1.getIdCategoria())))
                .andExpect(jsonPath("$.nomeCategoria", is(categoriaExemplo1.getNomeCategoria())));
    }

    @DisplayName("GET /api/v1/categorias/{id} - Deve retornar 404 Not Found para ID inexistente")
    @Test
    void testBuscarCategoriaPorId_Api_NaoExistente() throws Exception {
        // Arrange
        Integer categoriaId = 99;
        given(categoriaServiceMock.buscarCategoriaPorId(categoriaId)).willReturn(Optional.empty());

        // Act
        ResultActions response = mockMvc.perform(get("/api/v1/categorias/{id}", categoriaId));

        // Assert
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("POST /api/v1/categorias - Deve criar uma nova categoria")
    @Test
    void testCriarCategoria_Api() throws Exception {
        // Arrange
        Categoria categoriaParaCriar = new Categoria();
        categoriaParaCriar.setNomeCategoria("Design");
        // Quando o service.salvarCategoria for chamado com QUALQUER Categoria...
        given(categoriaServiceMock.salvarCategoria(any(Categoria.class))).willAnswer((invocation) -> {
            Categoria catRecebida = invocation.getArgument(0);
            Categoria catSalva = new Categoria();
            catSalva.setIdCategoria(5); // Simula um ID gerado
            catSalva.setNomeCategoria(catRecebida.getNomeCategoria());
            return catSalva;
        });

        // Act: Simula uma requisição POST
        ResultActions response = mockMvc.perform(post("/api/v1/categorias")
                .contentType(MediaType.APPLICATION_JSON) // Define o tipo de conteúdo que estamos enviando
                .content(objectMapper.writeValueAsString(categoriaParaCriar))); // Converte o objeto para JSON

        // Assert
        response.andDo(print())
                .andExpect(status().isCreated()) // Espera status HTTP 201 Created
                .andExpect(jsonPath("$.idCategoria", is(5)))
                .andExpect(jsonPath("$.nomeCategoria", is("Design")));
    }
}