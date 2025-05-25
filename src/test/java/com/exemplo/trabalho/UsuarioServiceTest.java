// Local: src/test/java/com/exemplo/trabalho/service/UsuarioServiceTest.java
package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Usuario;
import com.exemplo.trabalho.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.crypto.password.PasswordEncoder; // Se você estivesse usando

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepositoryMock;

    // @Mock // Se você fosse injetar o PasswordEncoder
    // private PasswordEncoder passwordEncoderMock;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioAdminExemplo;

    @BeforeEach
    void setUp() {
        usuarioAdminExemplo = new Usuario();
        usuarioAdminExemplo.setId(1L);
        usuarioAdminExemplo.setUsername("admin");
        usuarioAdminExemplo.setPassword("senhaSegura123"); // Em um cenário real, isso seria o hash
        usuarioAdminExemplo.setRoles("ROLE_ADMIN");
    }

    @DisplayName("Deve salvar um usuário com sucesso")
    @Test
    void testSalvarUsuario() {
        // Arrange
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername("novoUser");
        novoUsuario.setPassword("outraSenha");
        novoUsuario.setRoles("ROLE_USER");

        // Se você estivesse usando PasswordEncoder:
        // given(passwordEncoderMock.encode("outraSenha")).willReturn("senhaHasheada123");
        // novoUsuario.setPassword("senhaHasheada123"); // Service usaria o encoder

        // Configura o mock para retornar o usuário que foi passado para save
        // (simulando o comportamento do JpaRepository que retorna a entidade salva)
        given(usuarioRepositoryMock.save(any(Usuario.class))).willAnswer(invocation -> {
            Usuario userArg = invocation.getArgument(0);
            // Simula a atribuição de um ID pelo banco de dados, se ainda não tiver
            if (userArg.getId() == null) {
                userArg.setId(2L); // ID de exemplo para um novo usuário
            }
            return userArg;
        });


        // Act
        Usuario usuarioSalvo = usuarioService.salvarUsuario(novoUsuario);

        // Assert
        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getUsername()).isEqualTo("novoUser");
        // Se estivesse usando PasswordEncoder, verificaria se a senha foi "hasheada"
        // assertThat(usuarioSalvo.getPassword()).isEqualTo("senhaHasheada123");
        verify(usuarioRepositoryMock).save(novoUsuario); // Verifica se o método save foi chamado no mock
    }

    @DisplayName("Deve buscar um usuário por username existente")
    @Test
    void testBuscarPorUsername_Existente() {
        // Arrange
        String username = "admin";
        given(usuarioRepositoryMock.findByUsername(username)).willReturn(Optional.of(usuarioAdminExemplo));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorUsername(username);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getUsername()).isEqualTo(username);
        assertThat(resultado.get().getRoles()).isEqualTo("ROLE_ADMIN");
        verify(usuarioRepositoryMock).findByUsername(username);
    }

    @DisplayName("Deve retornar Optional vazio ao buscar por username inexistente")
    @Test
    void testBuscarPorUsername_NaoExistente() {
        // Arrange
        String usernameInexistente = "usuariofantasma";
        given(usuarioRepositoryMock.findByUsername(usernameInexistente)).willReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorUsername(usernameInexistente);

        // Assert
        assertThat(resultado).isNotPresent();
        verify(usuarioRepositoryMock).findByUsername(usernameInexistente);
    }
}