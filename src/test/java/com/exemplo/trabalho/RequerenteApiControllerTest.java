// Local: src/test/java/com/exemplo/trabalho/controller/RequerenteApiControllerTest.java
package com.exemplo.trabalho.controller;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.model.Requerente;
import com.exemplo.trabalho.model.Requisicao;
import com.exemplo.trabalho.service.CategoriaService;
import com.exemplo.trabalho.service.RequerenteService;
import com.exemplo.trabalho.service.RequisicaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt; // Certifique-se que este está aqui
import static org.mockito.ArgumentMatchers.anyString; // Certifique-se que este está aqui
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never; // <<<<<< IMPORTANTE ADICIONAR ESTE
import static org.mockito.Mockito.verify; // <<<<<< IMPORTANTE ADICIONAR ESTE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequerenteApiController.class)
class RequerenteApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequerenteService requerenteServiceMock;

    @MockBean
    private RequisicaoService requisicaoServiceMock;

    @MockBean
    private CategoriaService categoriaServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    private RequerenteApiController.CadastroRequerenteComRequisicaoRequest dtoValido;
    private Requerente requerenteSalvoExemplo;
    private Categoria categoriaSalvaExemplo;
    private Requisicao requisicaoSalvaExemplo;

    @BeforeEach
    void setUp() {
        dtoValido = new RequerenteApiController.CadastroRequerenteComRequisicaoRequest();
        dtoValido.setCpfRequerente("111.222.333-44");
        dtoValido.setNomeRequerente("Requerente API Teste");
        dtoValido.setEmailRequerente("req.api@teste.com");
        dtoValido.setTelefoneRequerente("(11)98888-7777");
        dtoValido.setEnderecoRequerente("12345-000"); // CEP
        dtoValido.setSenhaRequerente("senhaApi123");
        dtoValido.setIdCategoriaRequisicao(1);
        dtoValido.setDescricaoRequisicao("Preciso de um pintor");
        dtoValido.setFrequenciaRequisicao("Única");
        dtoValido.setInfoCompRequisicao("Pintura de uma parede.");

        requerenteSalvoExemplo = new Requerente();
        requerenteSalvoExemplo.setCpf(dtoValido.getCpfRequerente());
        requerenteSalvoExemplo.setNome(dtoValido.getNomeRequerente());
        requerenteSalvoExemplo.setEmail(dtoValido.getEmailRequerente());

        categoriaSalvaExemplo = new Categoria();
        categoriaSalvaExemplo.setIdCategoria(dtoValido.getIdCategoriaRequisicao());
        categoriaSalvaExemplo.setNomeCategoria("Serviços Gerais");

        requisicaoSalvaExemplo = new Requisicao();
        requisicaoSalvaExemplo.setIdRequisicao(10L); // Use L para Long
        requisicaoSalvaExemplo.setRequerente(requerenteSalvoExemplo);
        requisicaoSalvaExemplo.setCategoria(categoriaSalvaExemplo);
        requisicaoSalvaExemplo.setDescricao(dtoValido.getDescricaoRequisicao());
        requisicaoSalvaExemplo.setDataCadastro(new Date());
    }

    @DisplayName("POST /api/v1/requerentes/cadastrar-com-requisicao - Deve criar requerente e requisição com sucesso")
    @Test
    void testCadastrarRequerenteComRequisicao_Sucesso() throws Exception {
        // Arrange
        given(requerenteServiceMock.buscarRequerentePorCpf(dtoValido.getCpfRequerente())).willReturn(Optional.empty());
        given(requerenteServiceMock.salvarRequerente(any(Requerente.class))).willReturn(requerenteSalvoExemplo);
        given(categoriaServiceMock.buscarCategoriaPorId(dtoValido.getIdCategoriaRequisicao())).willReturn(Optional.of(categoriaSalvaExemplo));
        given(requisicaoServiceMock.salvarRequisicao(any(Requisicao.class))).willReturn(requisicaoSalvaExemplo);

        // Act
        ResultActions response = mockMvc.perform(post("/api/v1/requerentes/cadastrar-com-requisicao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido)));

        // Assert
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Demanda de serviço cadastrada com sucesso!")))
                .andExpect(jsonPath("$.cpfRequerente", is(requerenteSalvoExemplo.getCpf())))
                .andExpect(jsonPath("$.idRequisicao", is(requisicaoSalvaExemplo.getIdRequisicao().intValue())));
    }

    @DisplayName("POST /api/v1/requerentes/cadastrar-com-requisicao - Deve usar requerente existente e criar nova requisição")
    @Test
    void testCadastrarRequerenteComRequisicao_RequerenteExistente() throws Exception {
        // Arrange
        given(requerenteServiceMock.buscarRequerentePorCpf(dtoValido.getCpfRequerente())).willReturn(Optional.of(requerenteSalvoExemplo));
        given(categoriaServiceMock.buscarCategoriaPorId(dtoValido.getIdCategoriaRequisicao())).willReturn(Optional.of(categoriaSalvaExemplo));
        given(requisicaoServiceMock.salvarRequisicao(any(Requisicao.class))).willReturn(requisicaoSalvaExemplo);

        // Act
        ResultActions response = mockMvc.perform(post("/api/v1/requerentes/cadastrar-com-requisicao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido)));

        // Assert
        response.andDo(print())
                .andExpect(status().isCreated());
        verify(requerenteServiceMock, never()).salvarRequerente(any(Requerente.class));
        verify(requisicaoServiceMock).salvarRequisicao(any(Requisicao.class));
    }


    @DisplayName("POST /api/v1/requerentes/cadastrar-com-requisicao - Deve retornar erro para CPF inválido")
    @Test
    void testCadastrarRequerenteComRequisicao_CpfInvalido() throws Exception {
        // Arrange
        dtoValido.setCpfRequerente("123");

        // Act
        ResultActions response = mockMvc.perform(post("/api/v1/requerentes/cadastrar-com-requisicao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido)));

        // Assert
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cpfRequerente", is("Formato de CPF/CNPJ do requerente inválido.")));
    }

    @DisplayName("POST /api/v1/requerentes/cadastrar-com-requisicao - Deve retornar erro para categoria não encontrada")
    @Test
    void testCadastrarRequerenteComRequisicao_CategoriaNaoEncontrada() throws Exception {
        // Arrange
        given(requerenteServiceMock.buscarRequerentePorCpf(dtoValido.getCpfRequerente())).willReturn(Optional.empty());
        given(requerenteServiceMock.salvarRequerente(any(Requerente.class))).willReturn(requerenteSalvoExemplo);
        given(categoriaServiceMock.buscarCategoriaPorId(dtoValido.getIdCategoriaRequisicao())).willReturn(Optional.empty());

        // Act
        ResultActions response = mockMvc.perform(post("/api/v1/requerentes/cadastrar-com-requisicao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido)));

        // Assert
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Categoria não encontrada com o ID: " + dtoValido.getIdCategoriaRequisicao())));
    }
}