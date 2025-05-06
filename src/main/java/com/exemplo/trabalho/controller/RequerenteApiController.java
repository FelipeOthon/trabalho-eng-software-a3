package com.exemplo.trabalho.controller;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.model.Requerente;
import com.exemplo.trabalho.model.Requisicao;
import com.exemplo.trabalho.service.RequerenteService;
import com.exemplo.trabalho.service.RequisicaoService;
import com.exemplo.trabalho.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional; // <<< ESTA LINHA FOI ADICIONADA
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

    // --- DTOs (Data Transfer Objects) ---
    public static class CadastroRequerenteRequest {
        public String cpf;
        public String nome;
        public String email;
        public String telefone;
        public String endereco;
        public String senha;
    }

    public static class CadastroRequisicaoRequest {
        public String descricao;
        public String infoComplementares;
        public String frequencia;
        public Integer idCategoria;
    }

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

    // DTO para o novo endpoint combinado
    public static class CadastroRequerenteComRequisicaoRequest {
        // Campos do Requerente
        public String cpfRequerente;
        public String nomeRequerente;
        public String emailRequerente;
        public String telefoneRequerente;
        public String enderecoRequerente;
        public String senhaRequerente;
        // Campos da Requisição
        public Integer idCategoriaRequisicao;
        public String descricaoRequisicao;
        public String frequenciaRequisicao;
        public String infoCompRequisicao;
    }

    // --- Endpoints para Requerentes ---
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarRequerente(@RequestBody CadastroRequerenteRequest request) {
        try {
            Requerente novoRequerente = new Requerente();
            novoRequerente.setCpf(request.cpf);
            novoRequerente.setNome(request.nome);
            novoRequerente.setEmail(request.email);
            novoRequerente.setTelefone(request.telefone);
            novoRequerente.setEndereco(request.endereco);
            novoRequerente.setSenha(request.senha);
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

    // NOVO ENDPOINT ABAIXO
    @PostMapping("/cadastrar-com-requisicao")
    public ResponseEntity<?> cadastrarRequerenteComRequisicao(@RequestBody CadastroRequerenteComRequisicaoRequest request) {
        try {
            // 1. Criar e salvar o Requerente
            Requerente novoRequerente = new Requerente();
            novoRequerente.setCpf(request.cpfRequerente);
            novoRequerente.setNome(request.nomeRequerente);
            novoRequerente.setEmail(request.emailRequerente);
            novoRequerente.setTelefone(request.telefoneRequerente);
            novoRequerente.setEndereco(request.enderecoRequerente);
            novoRequerente.setSenha(request.senhaRequerente); // Lembre-se do hashing de senha no futuro

            Requerente requerenteParaRequisicao;
            Optional<Requerente> requerenteExistente = requerenteService.buscarRequerentePorCpf(request.cpfRequerente);
            if (requerenteExistente.isPresent()) {
                requerenteParaRequisicao = requerenteExistente.get();
            } else {
                requerenteParaRequisicao = requerenteService.salvarRequerente(novoRequerente);
            }

            Categoria categoria = categoriaService.buscarCategoriaPorId(request.idCategoriaRequisicao)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com o ID: " + request.idCategoriaRequisicao));

            Requisicao novaRequisicao = new Requisicao();
            novaRequisicao.setRequerente(requerenteParaRequisicao);
            novaRequisicao.setCategoria(categoria);
            novaRequisicao.setDescricao(request.descricaoRequisicao);
            novaRequisicao.setInfoComplementares(request.infoCompRequisicao);
            novaRequisicao.setFrequencia(request.frequenciaRequisicao);
            novaRequisicao.setDataCadastro(new Date());

            Requisicao requisicaoSalva = requisicaoService.salvarRequisicao(novaRequisicao);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Requerente e requisição processados com sucesso!",
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