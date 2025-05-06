package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Requerente;
import com.exemplo.trabalho.repository.RequerenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RequerenteService {

    private final RequerenteRepository requerenteRepository;

    @Autowired
    public RequerenteService(RequerenteRepository requerenteRepository) {
        this.requerenteRepository = requerenteRepository;
    }

    @Transactional
    public Requerente salvarRequerente(Requerente requerente) {
        if (requerente == null || requerente.getCpf() == null || requerente.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("Dados do requerente inválidos (CPF é obrigatório).");
        }
        if (requerenteRepository.existsById(requerente.getCpf())) {
            throw new IllegalArgumentException("Já existe um requerente cadastrado com este CPF.");
        }
        if (requerente.getEmail() != null && !requerente.getEmail().trim().isEmpty() &&
                requerenteRepository.findByEmail(requerente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Este email já está em uso por outro requerente.");
        }
        // Futuramente: requerente.setSenha(passwordEncoder.encode(requerente.getSenha()));
        return requerenteRepository.save(requerente);
    }

    public Optional<Requerente> buscarRequerentePorCpf(String cpf) {
        return requerenteRepository.findById(cpf);
    }

    public List<Requerente> listarTodosRequerentes() {
        return requerenteRepository.findAll();
    }
}