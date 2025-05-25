package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Servico;
import com.exemplo.trabalho.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para operações de deleção

import java.util.List;
import java.util.Optional;

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

    // NOVO MÉTODO PARA DELETAR SERVIÇO
    @Transactional // Adicionar @Transactional para garantir a consistência da operação de deleção
    public void deletarServico(Long id) {
        if (servicoRepository.existsById(id)) {
            servicoRepository.deleteById(id);
            System.out.println("Serviço com ID " + id + " deletado com sucesso."); // Log de debug
        } else {
            System.out.println("Tentativa de deletar serviço com ID " + id + ", mas não foi encontrado."); // Log de debug
            // Você pode optar por lançar uma exceção aqui se o serviço não for encontrado,
            // por exemplo: throw new jakarta.persistence.EntityNotFoundException("Serviço com ID " + id + " não encontrado.");
            // Para usar EntityNotFoundException, adicione: import jakarta.persistence.EntityNotFoundException;
        }
    }
}