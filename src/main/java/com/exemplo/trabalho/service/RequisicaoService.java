package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Requisicao;
import com.exemplo.trabalho.repository.RequisicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RequisicaoService {

    private final RequisicaoRepository requisicaoRepository;

    @Autowired
    public RequisicaoService(RequisicaoRepository requisicaoRepository) {
        this.requisicaoRepository = requisicaoRepository;
    }

    @Transactional
    public Requisicao salvarRequisicao(Requisicao requisicao) {
        if (requisicao == null || requisicao.getRequerente() == null || requisicao.getCategoria() == null || requisicao.getDataCadastro() == null) {
            throw new IllegalArgumentException("Dados da requisição incompletos para salvar.");
        }
        return requisicaoRepository.save(requisicao);
    }

    public List<Requisicao> listarTodasRequisicoes() {
        return requisicaoRepository.findAll();
    }

    public List<Requisicao> listarRequisicoesPorCpfRequerente(String cpf) {
        return requisicaoRepository.findByRequerenteCpf(cpf);
    }

    public List<Requisicao> listarRequisicoesPorIdCategoria(Integer idCategoria) {
        return requisicaoRepository.findByCategoriaIdCategoria(idCategoria);
    }

    public Optional<Requisicao> buscarRequisicaoPorId(Long id) {
        return requisicaoRepository.findById(id);
    }
}