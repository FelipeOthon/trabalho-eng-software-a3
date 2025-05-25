// Local: src/test/java/com/exemplo/trabalho/service/EmpresaServiceTest.java
package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Empresa;
import com.exemplo.trabalho.repository.EmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepositoryMock;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresaExemplo;

    @BeforeEach
    void setUp() {
        empresaExemplo = new Empresa();
        empresaExemplo.setIdEmpresa(1L);
        empresaExemplo.setNome("Empresa Teste S.A.");
        empresaExemplo.setCnpj("12.345.678/0001-99");
        empresaExemplo.setEmail("contato@empresateste.com");
        // Adicione outros campos se necessário para os testes
    }

    @DisplayName("Deve cadastrar uma empresa com sucesso")
    @Test
    void testCadastrarEmpresa_Sucesso() {
        // Arrange
        Empresa novaEmpresa = new Empresa();
        novaEmpresa.setNome("Nova Empresa Ltda");
        novaEmpresa.setCnpj("98.765.432/0001-00");
        novaEmpresa.setEmail("contato@novaempresa.com");

        given(empresaRepositoryMock.findByCnpj(novaEmpresa.getCnpj())).willReturn(Optional.empty()); // CNPJ não existe
        given(empresaRepositoryMock.save(any(Empresa.class))).willAnswer(invocation -> {
            Empresa empSalva = invocation.getArgument(0);
            empSalva.setIdEmpresa(2L); // Simula ID gerado
            return empSalva;
        });

        // Act
        Empresa empresaSalva = empresaService.cadastrarEmpresa(novaEmpresa);

        // Assert
        assertThat(empresaSalva).isNotNull();
        assertThat(empresaSalva.getIdEmpresa()).isEqualTo(2L);
        assertThat(empresaSalva.getNome()).isEqualTo("Nova Empresa Ltda");
        verify(empresaRepositoryMock).findByCnpj(novaEmpresa.getCnpj());
        verify(empresaRepositoryMock).save(novaEmpresa);
    }

    @DisplayName("Deve lançar exceção ao cadastrar empresa com CNPJ já existente")
    @Test
    void testCadastrarEmpresa_CnpjExistente() {
        // Arrange
        Empresa empresaComCnpjDuplicado = new Empresa();
        empresaComCnpjDuplicado.setNome("Outra Empresa");
        empresaComCnpjDuplicado.setCnpj(empresaExemplo.getCnpj()); // CNPJ igual ao do 'empresaExemplo'

        given(empresaRepositoryMock.findByCnpj(empresaExemplo.getCnpj())).willReturn(Optional.of(empresaExemplo)); // Simula que o CNPJ já existe

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            empresaService.cadastrarEmpresa(empresaComCnpjDuplicado);
        });

        assertThat(exception.getMessage()).isEqualTo("CNPJ já cadastrado.");
        verify(empresaRepositoryMock).findByCnpj(empresaExemplo.getCnpj());
        verify(empresaRepositoryMock, never()).save(any(Empresa.class));
    }

    @DisplayName("Deve listar todas as empresas")
    @Test
    void testListarTodasEmpresas() {
        // Arrange
        Empresa empresa2 = new Empresa();
        empresa2.setIdEmpresa(2L);
        empresa2.setNome("Segunda Empresa");
        List<Empresa> listaSimulada = Arrays.asList(empresaExemplo, empresa2);
        given(empresaRepositoryMock.findAll()).willReturn(listaSimulada);

        // Act
        List<Empresa> resultado = empresaService.listarTodasEmpresas();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado).containsExactlyInAnyOrder(empresaExemplo, empresa2);
        verify(empresaRepositoryMock).findAll();
    }

    @DisplayName("Deve buscar empresa por ID existente")
    @Test
    void testBuscarEmpresaPorId_Existente() {
        // Arrange
        Long id = 1L;
        given(empresaRepositoryMock.findById(id)).willReturn(Optional.of(empresaExemplo));

        // Act
        Optional<Empresa> resultado = empresaService.buscarEmpresaPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Empresa Teste S.A.");
        verify(empresaRepositoryMock).findById(id);
    }

    @DisplayName("Deve retornar Optional vazio ao buscar empresa por ID inexistente")
    @Test
    void testBuscarEmpresaPorId_NaoExistente() {
        // Arrange
        Long idInexistente = 99L;
        given(empresaRepositoryMock.findById(idInexistente)).willReturn(Optional.empty());

        // Act
        Optional<Empresa> resultado = empresaService.buscarEmpresaPorId(idInexistente);

        // Assert
        assertThat(resultado).isNotPresent();
        verify(empresaRepositoryMock).findById(idInexistente);
    }

    @DisplayName("Deve buscar empresas por nome (contendo, ignorando case)")
    @Test
    void testBuscarEmpresaPorNome() {
        // Arrange
        String trechoNome = "teste";
        List<Empresa> listaSimulada = Arrays.asList(empresaExemplo);
        given(empresaRepositoryMock.findByNomeContainingIgnoreCase(trechoNome)).willReturn(listaSimulada);

        // Act
        List<Empresa> resultado = empresaService.buscarEmpresaPorNome(trechoNome);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(1);
        assertThat(resultado.get(0).getNome()).containsIgnoringCase(trechoNome);
        verify(empresaRepositoryMock).findByNomeContainingIgnoreCase(trechoNome);
    }
}