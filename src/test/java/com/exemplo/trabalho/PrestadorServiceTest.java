// Local: src/test/java/com/exemplo/trabalho/service/PrestadorServiceTest.java
package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.model.Prestador;
import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.repository.CategoriaRepository;
import com.exemplo.trabalho.repository.PrestadorRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows; // Para testar exceções
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestadorServiceTest {

    @Mock
    private PrestadorRepository prestadorRepositoryMock;

    @Mock
    private ServicoRepository servicoRepositoryMock;

    @Mock
    private CategoriaRepository categoriaRepositoryMock;

    @InjectMocks
    private PrestadorService prestadorService;

    private Prestador prestadorExemplo;
    private Categoria categoriaExemplo;
    private Servico servicoExemplo;

    @BeforeEach
    void setUp() {
        categoriaExemplo = new Categoria();
        categoriaExemplo.setIdCategoria(1);
        categoriaExemplo.setNomeCategoria("TI");

        prestadorExemplo = new Prestador();
        prestadorExemplo.setCpf("123.456.789-00");
        prestadorExemplo.setNome("Fulano de Tal");
        prestadorExemplo.setEmail("fulano@teste.com");
        prestadorExemplo.setSenha("senha123");
        prestadorExemplo.setTelefone("99999-9999");
        prestadorExemplo.setEndereco("Rua Teste, 123");

        servicoExemplo = new Servico();
        servicoExemplo.setIdServico(1L);
        servicoExemplo.setPrestador(prestadorExemplo);
        servicoExemplo.setCategoria(categoriaExemplo);
        servicoExemplo.setDescricao("Desenvolvimento de Site");
        servicoExemplo.setDataCadastro(new Date());
    }

    @DisplayName("Deve cadastrar novo prestador e serviço com sucesso")
    @Test
    void testCadastrarNovoPrestadorComServico_Sucesso() {
        // Arrange
        given(prestadorRepositoryMock.existsById(anyString())).willReturn(false);
        given(prestadorRepositoryMock.findByEmail(anyString())).willReturn(Optional.empty());
        given(categoriaRepositoryMock.findById(anyInt())).willReturn(Optional.of(categoriaExemplo));
        given(prestadorRepositoryMock.save(any(Prestador.class))).willReturn(prestadorExemplo); // Retorna o prestador como se tivesse sido salvo
        given(servicoRepositoryMock.save(any(Servico.class))).willAnswer(invocation -> invocation.getArgument(0)); // Retorna o serviço que foi passado

        // Act
        Prestador prestadorSalvo = prestadorService.cadastrarNovoPrestadorComServico(
                "111.222.333-44", "Novo Prestador", "novo@email.com", "11223344",
                "Rua Nova", "novasenha", "Descrição Serviço Novo",
                "Info Comp Novo", categoriaExemplo.getIdCategoria(), null
        );

        // Assert
        assertThat(prestadorSalvo).isNotNull();
        assertThat(prestadorSalvo.getEmail()).isEqualTo("fulano@teste.com"); // Mock está retornando prestadorExemplo

        verify(prestadorRepositoryMock).save(any(Prestador.class));
        verify(servicoRepositoryMock).save(any(Servico.class));
    }

    @DisplayName("Deve lançar exceção ao tentar cadastrar prestador com CPF existente")
    @Test
    void testCadastrarNovoPrestadorComServico_CpfExistente() {
        // Arrange
        given(prestadorRepositoryMock.existsById("123.456.789-00")).willReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            prestadorService.cadastrarNovoPrestadorComServico(
                    "123.456.789-00", "Outro Fulano", "outro@email.com", "11223344",
                    "Rua Outra", "outrasenha", "Outro Serviço",
                    "Outra Info", categoriaExemplo.getIdCategoria(), null
            );
        });
        assertThat(exception.getMessage()).isEqualTo("Já existe um prestador cadastrado com este CPF.");
        verify(prestadorRepositoryMock, never()).save(any(Prestador.class)); // Garante que save não foi chamado
        verify(servicoRepositoryMock, never()).save(any(Servico.class));
    }

    @DisplayName("Deve lançar exceção ao tentar cadastrar prestador com email existente")
    @Test
    void testCadastrarNovoPrestadorComServico_EmailExistente() {
        // Arrange
        given(prestadorRepositoryMock.existsById(anyString())).willReturn(false); // CPF não existe
        given(prestadorRepositoryMock.findByEmail("fulano@teste.com")).willReturn(Optional.of(prestadorExemplo)); // Email já existe

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            prestadorService.cadastrarNovoPrestadorComServico(
                    "999.888.777-66", "Novo Fulano", "fulano@teste.com", // Email existente
                    "12345678", "Rua Existente", "senha123", "Serviço X",
                    "Info X", categoriaExemplo.getIdCategoria(), null
            );
        });
        assertThat(exception.getMessage()).isEqualTo("Este email já está em uso por outro prestador.");
    }


    @DisplayName("Deve lançar exceção se a categoria não for encontrada")
    @Test
    void testCadastrarNovoPrestadorComServico_CategoriaNaoEncontrada() {
        // Arrange
        given(prestadorRepositoryMock.existsById(anyString())).willReturn(false);
        given(prestadorRepositoryMock.findByEmail(anyString())).willReturn(Optional.empty());
        given(categoriaRepositoryMock.findById(999)).willReturn(Optional.empty()); // Categoria não existe

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            prestadorService.cadastrarNovoPrestadorComServico(
                    "111.222.333-44", "Novo Prestador", "novo@email.com", "11223344",
                    "Rua Nova", "novasenha", "Descrição Serviço Novo",
                    "Info Comp Novo", 999, null // ID de categoria inexistente
            );
        });
        assertThat(exception.getMessage()).isEqualTo("Categoria não encontrada com o ID: 999");
    }

    @DisplayName("Deve listar todos os prestadores")
    @Test
    void testListarTodosPrestadores() {
        // Arrange
        List<Prestador> listaEsperada = Arrays.asList(prestadorExemplo, new Prestador());
        given(prestadorRepositoryMock.findAll()).willReturn(listaEsperada);

        // Act
        List<Prestador> resultado = prestadorService.listarTodosPrestadores();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
    }

    @DisplayName("Deve buscar prestador por CPF existente")
    @Test
    void testBuscarPrestadorPorCpf_Existente() {
        // Arrange
        given(prestadorRepositoryMock.findById("123.456.789-00")).willReturn(Optional.of(prestadorExemplo));

        // Act
        Optional<Prestador> resultado = prestadorService.buscarPrestadorPorCpf("123.456.789-00");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Fulano de Tal");
    }

    @DisplayName("Deve retornar Optional vazio ao buscar prestador por CPF inexistente")
    @Test
    void testBuscarPrestadorPorCpf_NaoExistente() {
        // Arrange
        given(prestadorRepositoryMock.findById("000.000.000-00")).willReturn(Optional.empty());

        // Act
        Optional<Prestador> resultado = prestadorService.buscarPrestadorPorCpf("000.000.000-00");

        // Assert
        assertThat(resultado).isNotPresent();
    }
}