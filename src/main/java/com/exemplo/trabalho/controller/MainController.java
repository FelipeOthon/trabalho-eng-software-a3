package com.exemplo.trabalho.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.ui.Model; // Se precisar passar dados para o Thymeleaf

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index"; // src/main/resources/templates/index.html (o seu `index.html` principal do PHP)
    }

    // Se você tiver uma página "Sobre" separada no projeto Spring Boot.
    // Se não, pode remover ou adaptar.
    @GetMapping("/sobre")
    public String sobre() {
        return "sobre"; // src/main/resources/templates/sobre.html
    }

    // Rota para o formulário de cadastro de prestador
    @GetMapping("/prestador/cadastrar")
    public String formCadastroPrestador() {
        // Este nome "formCadastro" deve corresponder ao nome do seu arquivo HTML
        // que está/estará em src/main/resources/templates/formCadastro.html
        return "formCadastro";
    }

    // Rota para o formulário de cadastro de demanda de serviço
    @GetMapping("/demanda/cadastrar")
    public String formCadastroDemanda() {
        return "formCadastroDemanda"; // src/main/resources/templates/formCadastroDemanda.html
    }

    // Rota para a página de listagem de prestadores/serviços (painel)
    @GetMapping("/servicos/painel")
    public String painelServicos() {
        return "indexEmpresas"; // src/main/resources/templates/indexEmpresas.html
    }

    // Rota para a página de login do administrador
    @GetMapping("/login-admin") // Ou apenas "/login" se for o único login
    public String loginAdminPage() {
        return "loginAdm"; // src/main/resources/templates/loginAdm.html
    }

    // Você precisará mover os arquivos HTML correspondentes do seu projeto PHP
    // (de app/view/) para a pasta src/main/resources/templates/ do seu projeto Spring Boot
    // e ajustar os caminhos para CSS, JS e imagens para usar th:href="@{/caminho/...}"
}