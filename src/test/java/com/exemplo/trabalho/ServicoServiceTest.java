// Local: src/test/java/com/exemplo/trabalho/service/ServicoServiceTest.java
package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.model.Prestador;
import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.repository.ServicoRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*; // Importa never, times, verify

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepositoryMock;

    @InjectMocks
    private ServicoService servicoService;

    private Servico servicoExemplo1;
    private Servico servicoExemplo2;
    private Prestador prestadorExemplo;
    private Categoria categoriaExemplo;

    @BeforeEach
    void setUp() {
        prestadorExemplo = new Prestador();
        prestadorExemplo.setCpf("123.456.789-00");
        prestadorExemplo.setNome("Prestador Padrão");

        categoriaExemplo = new Categoria();
        categoriaExemplo.setIdCategoria(1);
        categoriaExemplo.setNomeCategoria("Consultoria TI");

        servicoExemplo1 = new Servico();
        servicoExemplo1.setIdServico(1L);
        servicoExemplo1.setDescricao("Consultoria em Redes");
        servicoExemplo1.setPrestador(prestadorExemplo);
        servicoExemplo1.setCategoria(categoriaExemplo);
        servicoExemplo1.setDataCadastro(new Date());

        servicoExemplo2 = new Servico();
        servicoExemplo2.setIdServico(2L);
        servicoExemplo2.setDescricao("Desenvolvimento Web");
        servicoExemplo2.setPrestador(prestadorExemplo); // Mesmo prestador para simplificar
        servicoExemplo2.setCategoria(categoriaExemplo); // Mesma categoria
        servicoExemplo2.setDataCadastro(new Date());
    }

    @DisplayName("Deve listar todos os serviços com prestador e categoria")
    @Test
    void testListarTodos() {
        // Arrange
        List<Servico> listaSimulada = Arrays.asList(servicoExemplo1, servicoExemplo2);
        given(servicoRepositoryMock.findAllWithPrestadorAndCategoria()).willReturn(listaSimulada);

        // Act
        List<Servico> resultado = servicoService.listarTodos();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado).containsExactlyInAnyOrder(servicoExemplo1, servicoExemplo2);
        verify(servicoRepositoryMock).findAllWithPrestadorAndCategoria();
    }

    @DisplayName("Deve buscar serviço por ID existente")
    @Test
    void testBuscarPorId_Existente() {
        // Arrange
        Long id = 1L;
        given(servicoRepositoryMock.findById(id)).willReturn(Optional.of(servicoExemplo1));

        // Act
        Optional<Servico> resultado = servicoService.buscarPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDescricao()).isEqualTo("Consultoria em Redes");
        verify(servicoRepositoryMock).findById(id);
    }

    @DisplayName("Deve retornar Optional vazio ao buscar serviço por ID inexistente")
    @Test
    void testBuscarPorId_NaoExistente() {
        // Arrange
        Long idInexistente = 99L;
        given(servicoRepositoryMock.findById(idInexistente)).willReturn(Optional.empty());

        // Act
        Optional<Servico> resultado = servicoService.buscarPorId(idInexistente);

        // Assert
        assertThat(resultado).isNotPresent();
        verify(servicoRepositoryMock).findById(idInexistente);
    }

    @DisplayName("Deve deletar um serviço existente")
    @Test
    void testDeletarServico_Existente() {
        // Arrange
        Long idParaDeletar = 1L;
        given(servicoRepositoryMock.existsById(idParaDeletar)).willReturn(true);
        // O método deleteById é void, então não precisamos mockar o retorno com willReturn()
        // doNothing() é o comportamento padrão para métodos void em mocks, mas podemos ser explícitos se quisermos:
        // doNothing().when(servicoRepositoryMock).deleteById(idParaDeletar);

        // Act
        servicoService.deletarServico(idParaDeletar);

        // Assert
        // Verifica se o método deleteById do mock foi chamado exatamente uma vez com o ID correto.
        verify(servicoRepositoryMock, times(1)).existsById(idParaDeletar);
        verify(servicoRepositoryMock, times(1)).deleteById(idParaDeletar);
    }

    @DisplayName("Não deve chamar deleteById se o serviço não existir")
    @Test
    void testDeletarServico_NaoExistente() {
        // Arrange
        Long idParaDeletar = 99L;
        given(servicoRepositoryMock.existsById(idParaDeletar)).willReturn(false);

        // Act
        servicoService.deletarServico(idParaDeletar);

        // Assert
        // Verifica que o existsById foi chamado
        verify(servicoRepositoryMock, times(1)).existsById(idParaDeletar);
        // Verifica que o deleteById NUNCA foi chamado
        verify(servicoRepositoryMock, never()).deleteById(idParaDeletar);
    }

    @DisplayName("Deve listar serviços por CPF do prestador")
    @Test
    void testListarServicosPorCpfPrestador() {
        // Arrange
        String cpf = "123.456.789-00";
        List<Servico> listaSimulada = Arrays.asList(servicoExemplo1, servicoExemplo2);
        given(servicoRepositoryMock.findByPrestadorCpf(cpf)).willReturn(listaSimulada);

        // Act
        List<Servico> resultado = servicoService.listarServicosPorCpfPrestador(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado.get(0).getPrestador().getCpf()).isEqualTo(cpf);
        verify(servicoRepositoryMock).findByPrestadorCpf(cpf);
    }

    @DisplayName("Deve listar serviços por ID da categoria")
    @Test
    void testListarServicosPorCategoria() {
        // Arrange
        Integer idCategoria = 1;
        List<Servico> listaSimulada = Arrays.asList(servicoExemplo1, servicoExemplo2);
        given(servicoRepositoryMock.findByCategoriaIdCategoria(idCategoria)).willReturn(listaSimulada);

        // Act
        List<Servico> resultado = servicoService.listarServicosPorCategoria(idCategoria);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado.get(0).getCategoria().getIdCategoria()).isEqualTo(idCategoria);
        verify(servicoRepositoryMock).findByCategoriaIdCategoria(idCategoria);
    }
}