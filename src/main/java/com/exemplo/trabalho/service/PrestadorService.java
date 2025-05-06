package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Categoria;
import com.exemplo.trabalho.model.Prestador;
import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.repository.CategoriaRepository;
import com.exemplo.trabalho.repository.PrestadorRepository;
import com.exemplo.trabalho.repository.ServicoRepository;
// Importe PasswordEncoder se for lidar com senhas aqui (melhor com Spring Security)
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para operações que envolvem múltiplos saves

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PrestadorService {

    private final PrestadorRepository prestadorRepository;
    private final ServicoRepository servicoRepository;
    private final CategoriaRepository categoriaRepository; // Para buscar a categoria
    // private final PasswordEncoder passwordEncoder; // Descomente se for usar Spring Security

    @Autowired
    public PrestadorService(PrestadorRepository prestadorRepository,
                            ServicoRepository servicoRepository,
                            CategoriaRepository categoriaRepository
            /*, PasswordEncoder passwordEncoder */) {
        this.prestadorRepository = prestadorRepository;
        this.servicoRepository = servicoRepository;
        this.categoriaRepository = categoriaRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Garante que ou tudo é salvo, ou nada é (atomicidade)
    public Prestador cadastrarNovoPrestadorComServico(
            String cpf, String nome, String email, String telefone, String endereco, String senha,
            String descricaoServico, String infoComplementaresServico, Integer idCategoria, byte[] anexoServico) {

        // 1. Validar se o prestador já existe (pelo CPF ou email)
        if (prestadorRepository.existsById(cpf)) {
            throw new IllegalArgumentException("Já existe um prestador cadastrado com este CPF.");
        }
        if (prestadorRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Este email já está em uso por outro prestador.");
        }

        // 2. Buscar a Categoria
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com o ID: " + idCategoria));

        // 3. Criar e salvar o Prestador
        Prestador novoPrestador = new Prestador();
        novoPrestador.setCpf(cpf);
        novoPrestador.setNome(nome);
        novoPrestador.setEmail(email);
        novoPrestador.setTelefone(telefone);
        novoPrestador.setEndereco(endereco);
        // novoPrestador.setSenha(passwordEncoder.encode(senha)); // Hashear a senha antes de salvar
        novoPrestador.setSenha(senha); // Por enquanto, sem hash. Vamos adicionar segurança depois.

        Prestador prestadorSalvo = prestadorRepository.save(novoPrestador);

        // 4. Criar e salvar o Servico associado
        Servico novoServico = new Servico();
        novoServico.setPrestador(prestadorSalvo); // Associa o serviço ao prestador
        novoServico.setCategoria(categoria);     // Associa o serviço à categoria
        novoServico.setDescricao(descricaoServico);
        novoServico.setInfoComplementares(infoComplementaresServico);
        novoServico.setDataCadastro(new Date()); // Data atual
        novoServico.setAnexo(anexoServico); // Se houver anexo

        servicoRepository.save(novoServico);

        return prestadorSalvo; // Retorna o prestador salvo (que agora tem o serviço na sua lista, se o relacionamento estiver correto)
    }

    public List<Prestador> listarTodosPrestadores() {
        return prestadorRepository.findAll();
    }

    public Optional<Prestador> buscarPrestadorPorCpf(String cpf) {
        return prestadorRepository.findById(cpf);
    }

    public Optional<Prestador> buscarPrestadorPorEmail(String email) {
        return prestadorRepository.findByEmail(email);
    }

    // Se precisarmos listar serviços com informações do prestador (como no seu PHP original)
    // O `ServicoRepository.findAll()` já pode trazer os dados do prestador se o fetch for EAGER,
    // ou podemos carregar sob demanda se for LAZY.
    // Para replicar exatamente a consulta PHP (`SELECT * FROM SERVICO S, PRESTADOR P WHERE P.CPF = S.CPF;`),
    // podemos simplesmente buscar todos os serviços e acessar os dados do prestador.
    public List<Servico> listarTodosOsServicosComDetalhesDoPrestador() {
        return servicoRepository.findAll(); // Cada objeto Servico terá seu Prestador associado
    }

    // Outros métodos: atualizarPrestador, deletarPrestador, etc.
}