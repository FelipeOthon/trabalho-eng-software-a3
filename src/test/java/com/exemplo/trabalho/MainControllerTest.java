// Local: src/test/java/com/exemplo/trabalho/controller/MainControllerTest.java
package com.exemplo.trabalho.controller; // VERIFIQUE SE ESTA LINHA ESTÁ CORRETA

import com.exemplo.trabalho.model.Requisicao;
import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.model.Usuario;
import com.exemplo.trabalho.service.RequisicaoService;
import com.exemplo.trabalho.service.ServicoService;
import com.exemplo.trabalho.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList; // Corrigido: Import faltante
import java.util.Optional;

// IMPORTS ESTÁTICOS IMPORTANTES:
import static org.assertj.core.api.Assertions.assertThat; // Para o assertThat
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*; // Para never(), times(), verify()
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioServiceMock;

    @MockBean
    private ServicoService servicoServiceMock;

    @MockBean
    private RequisicaoService requisicaoServiceMock;

    private Usuario adminUsuario;

    @BeforeEach
    void setUp() {
        adminUsuario = new Usuario();
        adminUsuario.setId(1L);
        adminUsuario.setUsername("adminTest");
        adminUsuario.setPassword("password");
        adminUsuario.setRoles("ROLE_ADMIN");
    }

    @DisplayName("GET / - Deve retornar a view index")
    @Test
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andDo(print());
    }

    @DisplayName("GET /sobre - Deve retornar a view sobre")
    @Test
    void testSobrePage() throws Exception {
        mockMvc.perform(get("/sobre"))
                .andExpect(status().isOk())
                .andExpect(view().name("sobre"))
                .andDo(print());
    }

    @DisplayName("GET /prestador/cadastrar - Deve retornar a view formCadastro")
    @Test
    void testFormCadastroPrestadorPage() throws Exception {
        mockMvc.perform(get("/prestador/cadastrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("formCadastro"))
                .andDo(print());
    }

    @DisplayName("GET /demanda/cadastrar - Deve retornar a view formCadastroDemanda")
    @Test
    void testFormCadastroDemandaPage() throws Exception {
        mockMvc.perform(get("/demanda/cadastrar"))
                .andExpect(status().isOk())
                .andExpect(view().name("formCadastroDemanda"))
                .andDo(print());
    }

    @DisplayName("GET /servicos/painel - Deve retornar a view indexEmpresas")
    @Test
    void testPainelServicosPage() throws Exception {
        mockMvc.perform(get("/servicos/painel"))
                .andExpect(status().isOk())
                .andExpect(view().name("indexEmpresas"))
                .andDo(print());
    }

    @DisplayName("GET /login-admin - Deve retornar a view loginAdm")
    @Test
    void testLoginAdminPage() throws Exception {
        mockMvc.perform(get("/login-admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginAdm"))
                .andDo(print());
    }

    @DisplayName("POST /login-admin-process - Deve logar admin com sucesso e redirecionar para dashboard")
    @Test
    void testProcessLoginAdmin_Sucesso() throws Exception {
        given(usuarioServiceMock.buscarPorUsername("adminTest")).willReturn(Optional.of(adminUsuario));

        mockMvc.perform(post("/login-admin-process")
                        .param("username", "adminTest")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(request().sessionAttribute("adminLogado", adminUsuario))
                .andExpect(request().sessionAttribute("username", "adminTest"))
                .andDo(print());
    }

    @DisplayName("POST /login-admin-process - Deve falhar login com usuário inválido")
    @Test
    void testProcessLoginAdmin_UsuarioInvalido() throws Exception {
        given(usuarioServiceMock.buscarPorUsername("userErrado")).willReturn(Optional.empty());

        mockMvc.perform(post("/login-admin-process")
                        .param("username", "userErrado")
                        .param("password", "senhaQualquer"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginAdm"))
                .andExpect(model().attributeExists("error"))
                .andDo(print());
    }

    @DisplayName("POST /login-admin-process - Deve falhar login com senha inválida")
    @Test
    void testProcessLoginAdmin_SenhaInvalida() throws Exception {
        given(usuarioServiceMock.buscarPorUsername("adminTest")).willReturn(Optional.of(adminUsuario));

        mockMvc.perform(post("/login-admin-process")
                        .param("username", "adminTest")
                        .param("password", "senhaErrada"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginAdm"))
                .andExpect(model().attributeExists("error"))
                .andDo(print());
    }

    @DisplayName("POST /login-admin-process - Deve falhar login se usuário não for ROLE_ADMIN")
    @Test
    void testProcessLoginAdmin_NaoAdmin() throws Exception {
        Usuario usuarioNaoAdmin = new Usuario();
        usuarioNaoAdmin.setUsername("userComum");
        usuarioNaoAdmin.setPassword("password");
        usuarioNaoAdmin.setRoles("ROLE_USER");

        given(usuarioServiceMock.buscarPorUsername("userComum")).willReturn(Optional.of(usuarioNaoAdmin));

        mockMvc.perform(post("/login-admin-process")
                        .param("username", "userComum")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginAdm"))
                .andExpect(model().attribute("error", "Acesso negado. Você não possui permissões de administrador."))
                .andDo(print());
    }

    @DisplayName("GET /admin/dashboard - Deve redirecionar para login se não estiver logado")
    @Test
    void testAdminDashboard_NaoLogado() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login-admin"))
                .andDo(print());
    }

    @DisplayName("GET /admin/dashboard - Deve mostrar dashboard se admin estiver logado")
    @Test
    void testAdminDashboard_Logado() throws Exception {
        given(servicoServiceMock.listarTodos()).willReturn(new ArrayList<Servico>());
        given(requisicaoServiceMock.listarTodasRequisicoes()).willReturn(new ArrayList<Requisicao>());

        mockMvc.perform(get("/admin/dashboard")
                        .sessionAttr("adminLogado", adminUsuario))
                .andExpect(status().isOk())
                .andExpect(view().name("adminDashboard"))
                .andExpect(model().attribute("username", adminUsuario.getUsername()))
                .andExpect(model().attributeExists("listaServicos"))
                .andExpect(model().attributeExists("listaDemandas"))
                .andDo(print());
    }

    @DisplayName("GET /admin/servicos/deletar/{id} - Deve deletar serviço e redirecionar para dashboard")
    @Test
    void testDeletarServico_AdminLogado() throws Exception {
        Long idServicoParaDeletar = 1L;
        doNothing().when(servicoServiceMock).deletarServico(anyLong());

        mockMvc.perform(get("/admin/servicos/deletar/{idServico}", idServicoParaDeletar)
                        .sessionAttr("adminLogado", adminUsuario))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attribute("successMessage", "Serviço ID " + idServicoParaDeletar + " deletado com sucesso!"))
                .andDo(print());

        verify(servicoServiceMock, times(1)).deletarServico(idServicoParaDeletar);
    }

    @DisplayName("GET /admin/servicos/deletar/{id} - Deve redirecionar para login se não for admin")
    @Test
    void testDeletarServico_NaoAdminOuNaoLogado() throws Exception {
        Long idServicoParaDeletar = 1L;

        mockMvc.perform(get("/admin/servicos/deletar/{idServico}", idServicoParaDeletar))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login-admin"))
                .andExpect(flash().attribute("errorMessage", "Acesso não autorizado."))
                .andDo(print());

        verify(servicoServiceMock, never()).deletarServico(anyLong());
    }

    @DisplayName("GET /admin/logout - Deve invalidar sessão e redirecionar para login")
    @Test
    void testAdminLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("adminLogado", adminUsuario);

        MvcResult mvcResult = mockMvc.perform(get("/admin/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login-admin?logout"))
                .andDo(print())
                .andReturn();

        assertThat(mvcResult.getRequest().getSession(false).getAttribute("adminLogado")).isNull();
    }
}