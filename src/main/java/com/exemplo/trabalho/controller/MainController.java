package com.exemplo.trabalho.controller;

import com.exemplo.trabalho.model.Usuario; // Adicionado para o modelo Usuario
import com.exemplo.trabalho.service.UsuarioService; // Adicionado para o serviço de Usuario
import jakarta.servlet.http.HttpSession; // Adicionado para gerenciamento de sessão HTTP
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Mantido para passar dados para a view
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Adicionado para o processamento do formulário de login
import org.springframework.web.bind.annotation.RequestParam; // Adicionado para capturar parâmetros do formulário
// import org.springframework.ui.Model; // Se precisar passar dados para o Thymeleaf

import java.util.Optional; // Adicionado para lidar com o resultado de busca do usuário

@Controller
public class MainController {

    // Injeção de dependência para o UsuarioService
    // Garanta que a classe UsuarioService esteja anotada com @Service
    // e que UsuarioRepository esteja anotado com @Repository.
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/prestador/cadastrar")
    public String formCadastroPrestador() {
        return "formCadastro";
    }

    @GetMapping("/demanda/cadastrar")
    public String formCadastroDemanda() {
        return "formCadastroDemanda";
    }

    @GetMapping("/servicos/painel")
    public String painelServicos() {
        return "indexEmpresas";
    }

    // Rota para exibir a página de login do administrador
    @GetMapping("/login-admin")
    public String loginAdminPage() {
        return "loginAdm";
    }

    // --- INÍCIO DAS NOVAS ROTAS E LÓGICA PARA ADMIN ---

    // Rota para processar a submissão do formulário de login do administrador
    @PostMapping("/login-admin-process")
    public String processLoginAdmin(@RequestParam String username,
                                    @RequestParam String password,
                                    HttpSession session, // Para gerenciar a sessão do usuário
                                    Model model) {      // Para enviar mensagens de erro de volta para a view

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorUsername(username);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            // ATENÇÃO: Comparação de senha em texto plano.
            // Esta é uma GRANDE FALHA DE SEGURANÇA e só deve ser usada para fins de teste MUITO iniciais.
            // Em um sistema real, você DEVE usar Spring Security para hashear senhas no cadastro
            // e comparar o hash da senha fornecida com o hash armazenado.
            if (usuario.getPassword().equals(password)) { // Senha correta (para este exemplo simplificado)

                // Verifica se o usuário tem a role de admin
                // Certifique-se de que o valor "ROLE_ADMIN" é o que você usará no banco de dados.
                if ("ROLE_ADMIN".equalsIgnoreCase(usuario.getRoles())) {
                    session.setAttribute("adminLogado", usuario); // Armazena o objeto Usuario na sessão
                    session.setAttribute("username", usuario.getUsername()); // Facilita o acesso ao nome do usuário no Thymeleaf
                    return "redirect:/admin/dashboard"; // Redireciona para o dashboard do admin
                } else {
                    // Usuário autenticado, mas não tem permissão de administrador
                    model.addAttribute("error", "Acesso negado. Você não possui permissões de administrador.");
                    return "loginAdm"; // Volta para a página de login com mensagem de erro
                }
            }
        }

        // Usuário não encontrado ou senha incorreta
        model.addAttribute("error", "Usuário ou senha inválidos.");
        return "loginAdm"; // Volta para a página de login com mensagem de erro
    }

    // Rota para exibir o dashboard do administrador
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Usuario adminLogado = (Usuario) session.getAttribute("adminLogado");

        // Proteção básica da rota: verifica se o usuário está na sessão e tem a role correta
        if (adminLogado == null || !"ROLE_ADMIN".equalsIgnoreCase(adminLogado.getRoles())) {
            // Se não estiver logado como admin, ou se a sessão expirou, redireciona para o login
            return "redirect:/login-admin";
        }

        // Passa o nome do usuário (ou o objeto usuário inteiro) para ser exibido na página do dashboard
        model.addAttribute("username", adminLogado.getUsername());
        // model.addAttribute("adminUser", adminLogado); // Alternativa para passar o objeto inteiro

        // TODO (Próximos Passos):
        // Aqui você adicionaria a lógica para buscar os dados que o admin precisa ver:
        // Ex: List<Servico> todosServicos = servicoService.listarTodos();
        // Ex: List<Requisicao> todasRequisicoes = requisicaoService.listarTodasRequisicoes();
        // model.addAttribute("todosServicos", todosServicos);
        // model.addAttribute("todasRequisicoes", todasRequisicoes);

        return "adminDashboard"; // Nome do arquivo HTML do painel do administrador (src/main/resources/templates/adminDashboard.html)
    }

    // Rota para realizar o logout do administrador
    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("adminLogado"); // Remove o atributo específico da sessão
        session.removeAttribute("username");    // Remove o username também, se você o adicionou
        session.invalidate();                   // Invalida a sessão HTTP inteira, removendo todos os atributos
        return "redirect:/login-admin?logout"; // Redireciona para a página de login, o "?logout" é opcional (pode ser usado para exibir uma msg de logout bem-sucedido)
    }

    // --- FIM DAS NOVAS ROTAS E LÓGICA PARA ADMIN ---
}