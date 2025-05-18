package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional; // <<< ADICIONE ESTA LINHA EXATAMENTE AQUI

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    @Autowired
    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public List<Servico> listarServicosPorCpfPrestador(String cpf) {
        return servicoRepository.findByPrestadorCpf(cpf);
    }

    public List<Servico> listarServicosPorCategoria(Integer idCategoria) {
        return servicoRepository.findByCategoriaIdCategoria(idCategoria);
    }

    public List<Servico> listarTodos() {
        return servicoRepository.findAllWithPrestadorAndCategoria();
    }

    public Optional<Servico> buscarPorId(Long id) {
        return servicoRepository.findById(id);
    }

    // Outros métodos específicos para serviços...
}