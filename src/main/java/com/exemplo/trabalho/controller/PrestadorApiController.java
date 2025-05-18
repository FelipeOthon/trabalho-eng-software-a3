package com.exemplo.trabalho.controller; // Ou com.exemplo.trabalho.controller.api

import com.exemplo.trabalho.model.Prestador;
import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.service.PrestadorService;
import com.exemplo.trabalho.service.ServicoService; // Para listar todos os serviços
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Para upload de arquivos

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Para DTOs, se necessário

@RestController
@RequestMapping("/api/v1/prestadores") // Define um prefixo para todas as rotas da API de prestadores
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem (ajuste para produção)
public class PrestadorApiController {

    private final PrestadorService prestadorService;
    private final ServicoService servicoService; // Adicionado para listagem

    @Autowired
    public PrestadorApiController(PrestadorService prestadorService, ServicoService servicoService) {
        this.prestadorService = prestadorService;
        this.servicoService = servicoService;
    }

    // DTO para o payload de cadastro (simplificado, combine com o do serviço)
    // Idealmente, crie classes DTO separadas em um pacote dto
    public static class CadastroPrestadorRequest {
        public String cpf;
        public String nome;
        public String email;
        public String telefone;
        public String endereco;
        public String senha;
        public String descricaoServico;
        public String infoComplementaresServico;
        public Integer idCategoria;
        // O anexo será tratado como MultipartFile
    }

    // Endpoint para cadastrar um novo prestador e seu primeiro serviço
    // Corresponde ao seu antigo POST para /controller/servico
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarPrestador(
            @RequestParam("cpf") String cpf,
            @RequestParam("nome") String nome,
            @RequestParam("email") String email,
            @RequestParam("celular") String telefone, // "celular" no form JS
            @RequestParam("endereco") String endereco, // "cep" no form JS
            @RequestParam("senha") String senha,
            @RequestParam("categoria") Integer idCategoria,
            @RequestParam("descricao") String descricaoServico,
            @RequestParam("informacoesComplementares") String infoComplementaresServico,
            @RequestParam(value = "anexo", required = false) MultipartFile anexo // "Anexo" no form HTML original
    ) {
        try {
            // ---> Validação da IdCategoria <---
            if (idCategoria == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "A seleção de uma categoria é obrigatória."));
            }
            byte[] bytesAnexo = null;
            if (anexo != null && !anexo.isEmpty()) {
                bytesAnexo = anexo.getBytes();
            }

            Prestador prestadorSalvo = prestadorService.cadastrarNovoPrestadorComServico(
                    cpf, nome, email, telefone, endereco, senha,
                    descricaoServico, infoComplementaresServico, idCategoria, bytesAnexo
            );
            // Retornar uma resposta mais significativa
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Prestador cadastrado com sucesso!", "cpfPrestador", prestadorSalvo.getCpf()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Logar o erro e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocorreu um erro inesperado ao processar o cadastro."));
        }
    }

    // Endpoint para listar serviços (similar ao seu /controller/prestadores)
    // O frontend JS em painelServicos.js espera: { Descricao, Email (do prestador), InfoComplementares }
    // Vamos criar um DTO simples para isso.
    public static class ServicoPainelDTO {
        public String descricao;
        public String emailPrestador;
        public String nomePrestador;
        public String infoComplementares;
        public String nomeCategoria; // Adicional útil

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
        List<Servico> servicos = servicoService.listarTodos(); // Ou o método mais apropriado do PrestadorService
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