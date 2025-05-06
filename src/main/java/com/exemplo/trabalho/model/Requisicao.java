package com.exemplo.trabalho.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Requisicao") // Verifique o nome exato da tabela no seu BD
public class Requisicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Requisicao") // Verifique o nome da PK
    private Long idRequisicao;

    @Temporal(TemporalType.DATE)
    @Column(name = "Data_cadastro", nullable = false)
    private Date dataCadastro;

    @Column(name = "Descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "InfoComplementares", columnDefinition = "TEXT")
    private String infoComplementares;

    @Column(name = "Frequencia", length = 50)
    private String frequencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CPF", nullable = false) // FK para Requerente.CPF
    private Requerente requerente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_categoria", nullable = false) // FK para Categoria.ID_categoria
    private Categoria categoria;

    // Construtores
    public Requisicao() {
    }

    // Getters e Setters
    public Long getIdRequisicao() {
        return idRequisicao;
    }

    public void setIdRequisicao(Long idRequisicao) {
        this.idRequisicao = idRequisicao;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInfoComplementares() {
        return infoComplementares;
    }

    public void setInfoComplementares(String infoComplementares) {
        this.infoComplementares = infoComplementares;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public Requerente getRequerente() {
        return requerente;
    }

    public void setRequerente(Requerente requerente) {
        this.requerente = requerente;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}