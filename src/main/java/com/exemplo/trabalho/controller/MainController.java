package com.exemplo.trabalho.controller;

import com.exemplo.trabalho.model.Requisicao;
import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.model.Usuario;
import com.exemplo.trabalho.service.RequisicaoService;
import com.exemplo.trabalho.service.ServicoService;
import com.exemplo.trabalho.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // ADICIONADO para capturar o ID da URL
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // ADICIONADO para mensagens flash

import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private RequisicaoService requisicaoService;

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

    @GetMapping("/login-admin")
    public String loginAdminPage() {
        return "loginAdm";
    }

    @PostMapping("/login-admin-process")
    public String processLoginAdmin(@RequestParam String username,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model) {
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorUsername(username);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            // ATENÇÃO: Comparação de senha em texto plano (temporário, se você pulou o passo de segurança).
            if (usuario.getPassword().equals(password)) {
                if ("ROLE_ADMIN".equalsIgnoreCase(usuario.getRoles())) {
                    session.setAttribute("adminLogado", usuario);
                    session.setAttribute("username", usuario.getUsername());
                    return "redirect:/admin/dashboard";
                } else {
                    model.addAttribute("error", "Acesso negado. Você não possui permissões de administrador.");
                    return "loginAdm";
                }
            }
        }
        model.addAttribute("error", "Usuário ou senha inválidos.");
        return "loginAdm";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Usuario adminLogado = (Usuario) session.getAttribute("adminLogado");

        if (adminLogado == null || !"ROLE_ADMIN".equalsIgnoreCase(adminLogado.getRoles())) {
            return "redirect:/login-admin";
        }

        model.addAttribute("username", adminLogado.getUsername());

        List<Servico> todosServicos = servicoService.listarTodos();
        List<Requisicao> todasRequisicoes = requisicaoService.listarTodasRequisicoes();

        System.out.println("--- DEBUG MainController: adminDashboard ---");
        System.out.println("Número de SERVIÇOS encontrados: " + (todosServicos != null ? todosServicos.size() : "null"));
        System.out.println("Número de DEMANDAS encontradas: " + (todasRequisicoes != null ? todasRequisicoes.size() : "null"));
        if (todasRequisicoes != null && !todasRequisicoes.isEmpty()) {
            System.out.println("Primeira demanda (ID): " + todasRequisicoes.get(0).getIdRequisicao() + ", Descrição: " + todasRequisicoes.get(0).getDescricao());
        } else if (todasRequisicoes != null) {
            System.out.println("Lista de demandas está vazia.");
        } else {
            System.out.println("Lista de demandas é nula.");
        }
        System.out.println("------------------------------------------");

        model.addAttribute("listaServicos", todosServicos);
        model.addAttribute("listaDemandas", todasRequisicoes);

        return "adminDashboard";
    }

    // NOVO MÉTODO PARA DELETAR SERVIÇO
    @GetMapping("/admin/servicos/deletar/{idServico}")
    public String deletarServico(@PathVariable Long idServico, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario adminLogado = (Usuario) session.getAttribute("adminLogado");
        if (adminLogado == null || !"ROLE_ADMIN".equalsIgnoreCase(adminLogado.getRoles())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso não autorizado.");
            return "redirect:/login-admin";
        }

        try {
            servicoService.deletarServico(idServico);
            redirectAttributes.addFlashAttribute("successMessage", "Serviço ID " + idServico + " deletado com sucesso!");
        } catch (Exception e) {
            // Você pode querer logar a exceção completa no console do servidor
            // e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar serviço ID " + idServico + ".");
            System.err.println("Erro ao deletar serviço ID " + idServico + ": " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
    // FIM DO NOVO MÉTODO

    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("adminLogado");
        session.removeAttribute("username");
        session.invalidate();
        return "redirect:/login-admin?logout";
    }
}