package com.exemplo.trabalho.controller;

import com.exemplo.trabalho.model.Prestador;
import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.service.PrestadorService;
import com.exemplo.trabalho.service.ServicoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/prestadores")
@CrossOrigin(origins = "*")
public class PrestadorApiController {

    private final PrestadorService prestadorService;
    private final ServicoService servicoService;

    @Autowired
    public PrestadorApiController(PrestadorService prestadorService, ServicoService servicoService) {
        this.prestadorService = prestadorService;
        this.servicoService = servicoService;
    }

    // DTO para o payload de cadastro
    public static class CadastroPrestadorComServicoRequest {
        @NotBlank(message = "CPF é obrigatório.")
        @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "Formato de CPF inválido.")
        private String cpf; // MUDADO PARA PRIVATE

        @NotBlank(message = "Nome é obrigatório.")
        @Size(max = 200, message = "Nome não pode exceder 200 caracteres.")
        private String nome; // MUDADO PARA PRIVATE

        @NotBlank(message = "Email é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        @Size(max = 150, message = "Email não pode exceder 150 caracteres.")
        private String email; // MUDADO PARA PRIVATE

        @NotBlank(message = "Celular é obrigatório.")
        @Pattern(regexp = "^(\\(\\d{2}\\)\\s?)?\\d{4,5}-?\\d{4}$|^\\d{10,11}$", message = "Formato de celular inválido.")
        private String celular; // MUDADO PARA PRIVATE

        @NotBlank(message = "Senha é obrigatória.")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres.")
        private String senha; // MUDADO PARA PRIVATE

        @NotBlank(message = "CEP é obrigatório.")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Formato de CEP inválido (somente números, opcionalmente com traço).")
        private String endereco; // MUDADO PARA PRIVATE

        @NotNull(message = "Categoria é obrigatória.")
        private Integer categoria; // MUDADO PARA PRIVATE

        @NotBlank(message = "Descrição do serviço é obrigatória.")
        private String descricao; // MUDADO PARA PRIVATE

        @NotBlank(message = "Informações complementares são obrigatórias.")
        private String informacoesComplementares; // MUDADO PARA PRIVATE

        private MultipartFile anexo; // MUDADO PARA PRIVATE

        // GETTERS E SETTERS ADICIONADOS ABAIXO
        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getCelular() { return celular; }
        public void setCelular(String celular) { this.celular = celular; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public String getEndereco() { return endereco; }
        public void setEndereco(String endereco) { this.endereco = endereco; }

        public Integer getCategoria() { return categoria; }
        public void setCategoria(Integer categoria) { this.categoria = categoria; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getInformacoesComplementares() { return informacoesComplementares; }
        public void setInformacoesComplementares(String informacoesComplementares) { this.informacoesComplementares = informacoesComplementares; }

        public MultipartFile getAnexo() { return anexo; }
        public void setAnexo(MultipartFile anexo) { this.anexo = anexo; }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarPrestador(
            @Valid @ModelAttribute CadastroPrestadorComServicoRequest request,
            BindingResult bindingResult) {

        // LINHAS DE DEBUG (MANTIDAS PARA VERIFICAR)
        System.out.println("--- DEBUG: DADOS RECEBIDOS NO CONTROLLER (PrestadorApiController) ---");
        if (request != null) {
            System.out.println("CPF Recebido: [" + request.getCpf() + "]"); // Usar getter
            System.out.println("Nome Recebido: [" + request.getNome() + "]"); // Usar getter
            System.out.println("Email Recebido: [" + request.getEmail() + "]"); // Usar getter
            System.out.println("Celular Recebido: [" + request.getCelular() + "]"); // Usar getter
            System.out.println("Senha Recebida (comprimento): [" + (request.getSenha() != null ? request.getSenha().length() : "null") + "]"); // Usar getter
            System.out.println("Endereço (CEP) Recebido: [" + request.getEndereco() + "]"); // Usar getter
            System.out.println("Categoria ID Recebida: [" + request.getCategoria() + "]"); // Usar getter
            System.out.println("Descrição Recebida: [" + request.getDescricao() + "]"); // Usar getter
            System.out.println("Info Comp. Recebidas: [" + request.getInformacoesComplementares() + "]"); // Usar getter
            System.out.println("Anexo Nome: " + (request.getAnexo() != null && !request.getAnexo().isEmpty() ? request.getAnexo().getOriginalFilename() : "Nenhum ou vazio")); // Usar getter
            System.out.println("Anexo Tamanho: " + (request.getAnexo() != null && !request.getAnexo().isEmpty() ? request.getAnexo().getSize() : "N/A")); // Usar getter
        } else {
            System.out.println("O objeto 'request' (CadastroPrestadorComServicoRequest) CHEGOU NULO!");
        }
        System.out.println("-------------------------------------------------------------------");

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            System.out.println("--- ERROS DE VALIDAÇÃO DETECTADOS (PrestadorApiController) ---");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
                System.out.println("Campo: '" + error.getField() + "', Erro: '" + error.getDefaultMessage() + "', Valor Rejeitado: [" + error.getRejectedValue() + "]");
            }
            System.out.println("-------------------------------------------------------------");
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            if (request.getCategoria() == null || request.getCategoria() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("categoria", "Seleção de categoria inválida."));
            }

            byte[] bytesAnexo = null;
            if (request.getAnexo() != null && !request.getAnexo().isEmpty()) {
                if (request.getAnexo().getSize() > (5 * 1024 * 1024)) { // Limite de 5MB
                    return ResponseEntity.badRequest().body(Map.of("anexo", "Arquivo muito grande. O limite é de 5MB."));
                }
                bytesAnexo = request.getAnexo().getBytes();
            }

            Prestador prestadorSalvo = prestadorService.cadastrarNovoPrestadorComServico(
                    request.getCpf(), request.getNome(), request.getEmail(), request.getCelular(),
                    request.getEndereco(), request.getSenha(),
                    request.getDescricao(), request.getInformacoesComplementares(),
                    request.getCategoria(), bytesAnexo
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Prestador cadastrado com sucesso!", "cpfPrestador", prestadorSalvo.getCpf()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocorreu um erro inesperado ao processar o cadastro: " + e.getMessage()));
        }
    }

    // ... (Restante da classe, incluindo ServicoPainelDTO e outros endpoints, permanece igual) ...
    public static class ServicoPainelDTO {
        public String descricao;
        public String emailPrestador;
        public String nomePrestador;
        public String infoComplementares;
        public String nomeCategoria;

        public ServicoPainelDTO(Servico servico) {
            this.descricao = servico.getDescricao();
            if (servico.getPrestador() != null) {
                this.emailPrestador = servico.getPrestador().getEmail();
                this.nomePrestador = servico.getPrestador().getNome();
            }
            if (servico.getCategoria() != null) {
                this.nomeCategoria = servico.getCategoria().getNomeCategoria();
            }
            this.infoComplementares = servico.getInfoComplementares();
        }
    }

    @GetMapping("/servicos-disponiveis")
    public ResponseEntity<List<ServicoPainelDTO>> listarServicosParaPainel() {
        List<Servico> servicos = servicoService.listarTodos();
        List<ServicoPainelDTO> dtos = servicos.stream()
                .map(ServicoPainelDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Prestador>> listarTodosPrestadores() {
        return ResponseEntity.ok(prestadorService.listarTodosPrestadores());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Prestador> buscarPrestadorPorCpf(@PathVariable String cpf) {
        return prestadorService.buscarPrestadorPorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}