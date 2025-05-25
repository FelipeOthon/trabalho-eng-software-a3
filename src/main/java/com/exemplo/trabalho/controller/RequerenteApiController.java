package com.exemplo.trabalho.controller;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.model.Requerente;
import com.exemplo.trabalho.model.Requisicao;
import com.exemplo.trabalho.service.RequerenteService;
import com.exemplo.trabalho.service.RequisicaoService;
import com.exemplo.trabalho.service.CategoriaService;

// IMPORTS PARA VALIDAÇÃO
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

import java.util.Date;
import java.util.HashMap; // Para o mapa de erros
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/requerentes")
@CrossOrigin(origins = "*")
public class RequerenteApiController {

    private final RequerenteService requerenteService;
    private final RequisicaoService requisicaoService;
    private final CategoriaService categoriaService;

    @Autowired
    public RequerenteApiController(RequerenteService requerenteService,
                                   RequisicaoService requisicaoService,
                                   CategoriaService categoriaService) {
        this.requerenteService = requerenteService;
        this.requisicaoService = requisicaoService;
        this.categoriaService = categoriaService;
    }

    // DTO para o endpoint combinado com validações
    public static class CadastroRequerenteComRequisicaoRequest {
        @NotBlank(message = "CPF/CNPJ do requerente é obrigatório.")
        @Pattern(regexp = "^(\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}|\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2})$", message = "Formato de CPF/CNPJ do requerente inválido.")
        private String cpfRequerente;

        @NotBlank(message = "Nome do requerente é obrigatório.")
        @Size(max = 200, message = "Nome do requerente não pode exceder 200 caracteres.")
        private String nomeRequerente;

        @NotBlank(message = "Email do requerente é obrigatório.")
        @Email(message = "Formato de e-mail do requerente inválido.")
        @Size(max = 150, message = "Email do requerente não pode exceder 150 caracteres.")
        private String emailRequerente;

        @NotBlank(message = "Telefone do requerente é obrigatório.")
        @Pattern(regexp = "^(\\(\\d{2}\\)\\s?)?\\d{4,5}-?\\d{4}$|^\\d{10,11}$", message = "Formato de telefone do requerente inválido.")
        private String telefoneRequerente;

        @NotBlank(message = "Endereço (CEP) do requerente é obrigatório.")
        @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Formato de CEP do requerente inválido.")
        private String enderecoRequerente;

        @NotBlank(message = "Senha do requerente é obrigatória.")
        @Size(min = 6, message = "Senha do requerente deve ter no mínimo 6 caracteres.")
        private String senhaRequerente;

        @NotNull(message = "Categoria da requisição é obrigatória.")
        private Integer idCategoriaRequisicao;

        @NotBlank(message = "Descrição da requisição é obrigatória.")
        private String descricaoRequisicao;

        @NotBlank(message = "Frequência da requisição é obrigatória.")
        private String frequenciaRequisicao;

        @NotBlank(message = "Informações complementares da requisição são obrigatórias.")
        private String infoCompRequisicao;

        public CadastroRequerenteComRequisicaoRequest() {} // Construtor padrão

        // Getters e Setters
        public String getCpfRequerente() { return cpfRequerente; }
        public void setCpfRequerente(String cpfRequerente) { this.cpfRequerente = cpfRequerente; }
        public String getNomeRequerente() { return nomeRequerente; }
        public void setNomeRequerente(String nomeRequerente) { this.nomeRequerente = nomeRequerente; }
        public String getEmailRequerente() { return emailRequerente; }
        public void setEmailRequerente(String emailRequerente) { this.emailRequerente = emailRequerente; }
        public String getTelefoneRequerente() { return telefoneRequerente; }
        public void setTelefoneRequerente(String telefoneRequerente) { this.telefoneRequerente = telefoneRequerente; }
        public String getEnderecoRequerente() { return enderecoRequerente; }
        public void setEnderecoRequerente(String enderecoRequerente) { this.enderecoRequerente = enderecoRequerente; }
        public String getSenhaRequerente() { return senhaRequerente; }
        public void setSenhaRequerente(String senhaRequerente) { this.senhaRequerente = senhaRequerente; }
        public Integer getIdCategoriaRequisicao() { return idCategoriaRequisicao; }
        public void setIdCategoriaRequisicao(Integer idCategoriaRequisicao) { this.idCategoriaRequisicao = idCategoriaRequisicao; }
        public String getDescricaoRequisicao() { return descricaoRequisicao; }
        public void setDescricaoRequisicao(String descricaoRequisicao) { this.descricaoRequisicao = descricaoRequisicao; }
        public String getFrequenciaRequisicao() { return frequenciaRequisicao; }
        public void setFrequenciaRequisicao(String frequenciaRequisicao) { this.frequenciaRequisicao = frequenciaRequisicao; }
        public String getInfoCompRequisicao() { return infoCompRequisicao; }
        public void setInfoCompRequisicao(String infoCompRequisicao) { this.infoCompRequisicao = infoCompRequisicao; }
    }

    @PostMapping("/cadastrar-com-requisicao")
    public ResponseEntity<?> cadastrarRequerenteComRequisicao(
            @Valid @RequestBody CadastroRequerenteComRequisicaoRequest request,
            BindingResult bindingResult) {

        System.out.println("--- DEBUG: DADOS RECEBIDOS (cadastrarRequerenteComRequisicao) ---");
        if (request != null) {
            System.out.println("CPF Req: [" + request.getCpfRequerente() + "]");
            System.out.println("Nome Req: [" + request.getNomeRequerente() + "]");
            System.out.println("Email Req: [" + request.getEmailRequerente() + "]");
            System.out.println("Telefone Req: [" + request.getTelefoneRequerente() + "]");
            System.out.println("Endereço Req: [" + request.getEnderecoRequerente() + "]");
            System.out.println("Senha Req (comprimento): [" + (request.getSenhaRequerente() != null ? request.getSenhaRequerente().length() : "null") + "]");
            System.out.println("ID Categoria Req: [" + request.getIdCategoriaRequisicao() + "]");
            System.out.println("Descrição Req: [" + request.getDescricaoRequisicao() + "]");
            System.out.println("Frequência Req: [" + request.getFrequenciaRequisicao() + "]");
            System.out.println("Info Comp Req: [" + request.getInfoCompRequisicao() + "]");
        } else {
            System.out.println("Objeto 'request' (CadastroRequerenteComRequisicaoRequest) chegou NULO!");
        }
        System.out.println("----------------------------------------------------------------");

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            System.out.println("--- ERROS DE VALIDAÇÃO DETECTADOS (cadastrarRequerenteComRequisicao) ---");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
                System.out.println("Campo: '" + error.getField() + "', Erro: '" + error.getDefaultMessage() + "', Valor Rejeitado: [" + error.getRejectedValue() + "]");
            }
            System.out.println("--------------------------------------------------------------------");
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Requerente novoRequerente = new Requerente();
            novoRequerente.setCpf(request.getCpfRequerente());
            novoRequerente.setNome(request.getNomeRequerente());
            novoRequerente.setEmail(request.getEmailRequerente());
            novoRequerente.setTelefone(request.getTelefoneRequerente());
            novoRequerente.setEndereco(request.getEnderecoRequerente());
            novoRequerente.setSenha(request.getSenhaRequerente()); // Lembre-se do hashing

            Requerente requerenteParaRequisicao;
            Optional<Requerente> requerenteExistente = requerenteService.buscarRequerentePorCpf(request.getCpfRequerente());
            if (requerenteExistente.isPresent()) {
                requerenteParaRequisicao = requerenteExistente.get();
            } else {
                requerenteParaRequisicao = requerenteService.salvarRequerente(novoRequerente);
            }

            if (request.getIdCategoriaRequisicao() == null || request.getIdCategoriaRequisicao() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("idCategoriaRequisicao", "Seleção de categoria da requisição inválida."));
            }
            Categoria categoria = categoriaService.buscarCategoriaPorId(request.getIdCategoriaRequisicao())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com o ID: " + request.getIdCategoriaRequisicao()));

            Requisicao novaRequisicao = new Requisicao();
            novaRequisicao.setRequerente(requerenteParaRequisicao);
            novaRequisicao.setCategoria(categoria);
            novaRequisicao.setDescricao(request.getDescricaoRequisicao());
            novaRequisicao.setInfoComplementares(request.getInfoCompRequisicao());
            novaRequisicao.setFrequencia(request.getFrequenciaRequisicao());
            novaRequisicao.setDataCadastro(new Date());

            Requisicao requisicaoSalva = requisicaoService.salvarRequisicao(novaRequisicao);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Demanda de serviço cadastrada com sucesso!",
                            "cpfRequerente", requerenteParaRequisicao.getCpf(),
                            "idRequisicao", requisicaoSalva.getIdRequisicao()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao processar cadastro de demanda: " + e.getMessage()));
        }
    }

    // --- DTOs e outros endpoints não relacionados diretamente ao formulário de cadastro de demanda permanecem os mesmos ---
    // (Removi os DTOs CadastroRequerenteRequest e CadastroRequisicaoRequest não utilizados para limpeza,
    //  mas o restante dos endpoints GET foram mantidos pois já existiam)
    public static class RequisicaoDetalhesDTO {
        public Long idRequisicao;
        public Date dataCadastro;
        public String descricao;
        public String infoComplementares;
        public String frequencia;
        public String nomeRequerente;
        public String emailRequerente;
        public String nomeCategoria;

        public RequisicaoDetalhesDTO(Requisicao requisicao) {
            this.idRequisicao = requisicao.getIdRequisicao();
            this.dataCadastro = requisicao.getDataCadastro();
            this.descricao = requisicao.getDescricao();
            this.infoComplementares = requisicao.getInfoComplementares();
            this.frequencia = requisicao.getFrequencia();
            if (requisicao.getRequerente() != null) {
                this.nomeRequerente = requisicao.getRequerente().getNome();
                this.emailRequerente = requisicao.getRequerente().getEmail();
            }
            if (requisicao.getCategoria() != null) {
                this.nomeCategoria = requisicao.getCategoria().getNomeCategoria();
            }
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarRequerente(@Valid @RequestBody CadastroRequerenteRequest request, BindingResult bindingResult) {
        // Se este endpoint for realmente necessário, precisará de seu próprio DTO validado e lógica.
        // Por agora, vamos focar no "/cadastrar-com-requisicao".
        // ... (a lógica que você tinha aqui, mas precisaria de @Valid e BindingResult também) ...
        // Exemplo simplificado:
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Requerente novoRequerente = new Requerente();
            // Popular novoRequerente a partir do request.get...()
            Requerente requerenteSalvo = requerenteService.salvarRequerente(novoRequerente);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Requerente cadastrado com sucesso!", "cpfRequerente", requerenteSalvo.getCpf()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao cadastrar requerente: " + e.getMessage()));
        }
    }

    // --- DTO para CadastroRequerenteRequest ---
    // Se for usar o endpoint /cadastrar, este DTO precisaria de validações também
    public static class CadastroRequerenteRequest {
        @NotBlank(message = "CPF é obrigatório.") // Exemplo
        public String cpf;
        @NotBlank(message = "Nome é obrigatório.") // Exemplo
        public String nome;
        // Adicionar outras anotações e campos
        public String email;
        public String telefone;
        public String endereco;
        public String senha;

        // Getters e setters
        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        // Adicionar outros getters e setters
    }


    @GetMapping("/{cpf}")
    public ResponseEntity<Requerente> buscarRequerentePorCpf(@PathVariable String cpf) {
        return requerenteService.buscarRequerentePorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Requerente>> listarTodosRequerentes() {
        List<Requerente> requerentes = requerenteService.listarTodosRequerentes();
        return ResponseEntity.ok(requerentes);
    }

    @GetMapping("/requisicoes")
    public ResponseEntity<List<RequisicaoDetalhesDTO>> listarTodasRequisicoes() {
        List<Requisicao> requisicoes = requisicaoService.listarTodasRequisicoes();
        List<RequisicaoDetalhesDTO> dtos = requisicoes.stream()
                .map(RequisicaoDetalhesDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/requisicoes/requerente/{cpfRequerente}")
    public ResponseEntity<List<RequisicaoDetalhesDTO>> listarRequisicoesPorRequerente(@PathVariable String cpfRequerente) {
        List<Requisicao> requisicoes = requisicaoService.listarRequisicoesPorCpfRequerente(cpfRequerente);
        List<RequisicaoDetalhesDTO> dtos = requisicoes.stream()
                .map(RequisicaoDetalhesDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/requisicoes/categoria/{idCategoria}")
    public ResponseEntity<List<RequisicaoDetalhesDTO>> listarRequisicoesPorCategoria(@PathVariable Integer idCategoria) {
        List<Requisicao> requisicoes = requisicaoService.listarRequisicoesPorIdCategoria(idCategoria);
        List<RequisicaoDetalhesDTO> dtos = requisicoes.stream()
                .map(RequisicaoDetalhesDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}