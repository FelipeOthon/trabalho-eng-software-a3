// Local: src/test/java/com/exemplo/trabalho/service/RequisicaoServiceTest.java
package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.model.Requerente;
import com.exemplo.trabalho.model.Requisicao;
import com.exemplo.trabalho.repository.RequisicaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequisicaoServiceTest {

    @Mock
    private RequisicaoRepository requisicaoRepositoryMock;

    @InjectMocks
    private RequisicaoService requisicaoService;

    private Requerente requerenteExemplo;
    private Categoria categoriaExemplo;
    private Requisicao requisicaoExemplo;

    @BeforeEach
    void setUp() {
        requerenteExemplo = new Requerente();
        requerenteExemplo.setCpf("111.111.111-11");
        requerenteExemplo.setNome("Requerente dos Testes");

        categoriaExemplo = new Categoria();
        categoriaExemplo.setIdCategoria(1);
        categoriaExemplo.setNomeCategoria("Serviços Domésticos");

        requisicaoExemplo = new Requisicao();
        requisicaoExemplo.setIdRequisicao(1L);
        requisicaoExemplo.setRequerente(requerenteExemplo);
        requisicaoExemplo.setCategoria(categoriaExemplo);
        requisicaoExemplo.setDataCadastro(new Date());
        requisicaoExemplo.setDescricao("Preciso de limpeza geral.");
        requisicaoExemplo.setFrequencia("Única");
    }

    @DisplayName("Deve salvar uma requisição com sucesso")
    @Test
    void testSalvarRequisicao_Sucesso() {
        // Arrange
        given(requisicaoRepositoryMock.save(any(Requisicao.class))).willReturn(requisicaoExemplo);

        Requisicao novaRequisicao = new Requisicao();
        novaRequisicao.setRequerente(requerenteExemplo);
        novaRequisicao.setCategoria(categoriaExemplo);
        novaRequisicao.setDataCadastro(new Date());
        novaRequisicao.setDescricao("Outra descrição");
        // ... preencha outros campos se necessário

        // Act
        Requisicao requisicaoSalva = requisicaoService.salvarRequisicao(novaRequisicao);

        // Assert
        assertThat(requisicaoSalva).isNotNull();
        assertThat(requisicaoSalva.getIdRequisicao()).isEqualTo(1L); // Veio do mock
        verify(requisicaoRepositoryMock).save(novaRequisicao);
    }

    @DisplayName("Deve lançar exceção ao salvar requisição com dados incompletos (requerente nulo)")
    @Test
    void testSalvarRequisicao_RequerenteNulo() {
        // Arrange
        Requisicao requisicaoIncompleta = new Requisicao();
        requisicaoIncompleta.setRequerente(null); // Requerente nulo
        requisicaoIncompleta.setCategoria(categoriaExemplo);
        requisicaoIncompleta.setDataCadastro(new Date());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            requisicaoService.salvarRequisicao(requisicaoIncompleta);
        });
        assertThat(exception.getMessage()).isEqualTo("Dados da requisição incompletos para salvar.");
        verify(requisicaoRepositoryMock, never()).save(any(Requisicao.class));
    }

    @DisplayName("Deve listar todas as requisições")
    @Test
    void testListarTodasRequisicoes() {
        // Arrange
        Requisicao req2 = new Requisicao(); // Outra requisição para a lista
        List<Requisicao> listaSimulada = Arrays.asList(requisicaoExemplo, req2);
        given(requisicaoRepositoryMock.findAll()).willReturn(listaSimulada);

        // Act
        List<Requisicao> resultado = requisicaoService.listarTodasRequisicoes();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado).containsExactlyInAnyOrder(requisicaoExemplo, req2);
    }

    @DisplayName("Deve listar requisições por CPF do requerente")
    @Test
    void testListarRequisicoesPorCpfRequerente() {
        // Arrange
        String cpf = "111.111.111-11";
        List<Requisicao> listaSimulada = Arrays.asList(requisicaoExemplo);
        given(requisicaoRepositoryMock.findByRequerenteCpf(cpf)).willReturn(listaSimulada);

        // Act
        List<Requisicao> resultado = requisicaoService.listarRequisicoesPorCpfRequerente(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(1);
        assertThat(resultado.get(0).getRequerente().getCpf()).isEqualTo(cpf);
    }

    @DisplayName("Deve listar requisições por ID da categoria")
    @Test
    void testListarRequisicoesPorIdCategoria() {
        // Arrange
        Integer idCategoria = 1;
        List<Requisicao> listaSimulada = Arrays.asList(requisicaoExemplo);
        given(requisicaoRepositoryMock.findByCategoriaIdCategoria(idCategoria)).willReturn(listaSimulada);

        // Act
        List<Requisicao> resultado = requisicaoService.listarRequisicoesPorIdCategoria(idCategoria);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(1);
        assertThat(resultado.get(0).getCategoria().getIdCategoria()).isEqualTo(idCategoria);
    }


    @DisplayName("Deve buscar requisição por ID existente")
    @Test
    void testBuscarRequisicaoPorId_Existente() {
        // Arrange
        Long id = 1L;
        given(requisicaoRepositoryMock.findById(id)).willReturn(Optional.of(requisicaoExemplo));

        // Act
        Optional<Requisicao> resultado = requisicaoService.buscarRequisicaoPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDescricao()).isEqualTo("Preciso de limpeza geral.");
    }

    @DisplayName("Deve retornar Optional vazio ao buscar requisição por ID inexistente")
    @Test
    void testBuscarRequisicaoPorId_NaoExistente() {
        // Arrange
        Long idInexistente = 99L;
        given(requisicaoRepositoryMock.findById(idInexistente)).willReturn(Optional.empty());

        // Act
        Optional<Requisicao> resultado = requisicaoService.buscarRequisicaoPorId(idInexistente);

        // Assert
        assertThat(resultado).isNotPresent();
    }
}