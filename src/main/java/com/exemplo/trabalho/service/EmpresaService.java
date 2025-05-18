package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Empresa;
import com.exemplo.trabalho.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Autowired
    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public Empresa cadastrarEmpresa(Empresa empresa) {
        // Adicionar validações (ex: CNPJ único)
        if (empresa.getCnpj() != null && empresaRepository.findByCnpj(empresa.getCnpj()).isPresent()) {
            throw new IllegalArgumentException("CNPJ já cadastrado.");
        }
        // Lógica para lidar com upload de arquivo (empresa.getArquivo()) se necessário
        // Salvar o arquivo em disco e armazenar o caminho, ou salvar o binário se a entidade permitir.
        return empresaRepository.save(empresa);
    }

    public List<Empresa> listarTodasEmpresas() {
        return empresaRepository.findAll();
    }

    public Optional<Empresa> buscarEmpresaPorId(Long id) {
        return empresaRepository.findById(id);
    }

    public List<Empresa> buscarEmpresaPorNome(String nome) {
        return empresaRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Outros métodos...
}