package com.exemplo.trabalho.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Servico") // Verifique se este é o nome da PK na sua tabela Servico
    private Long idServico;

    @Temporal(TemporalType.DATE)
    @Column(name = "Data_cadastro", nullable = false)
    private Date dataCadastro;

    @Column(name = "Descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "InfoComplementares", columnDefinition = "TEXT")
    private String infoComplementares;

    @Lob
    @Column(name = "Anexo", columnDefinition="LONGBLOB") // Para conteúdo binário
    // private String caminhoAnexo; // Alternativa se armazenar o caminho do arquivo
    private byte[] anexo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CPF", nullable = false) // FK para Prestador.CPF
    private Prestador prestador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_categoria", nullable = false) // FK para Categoria.ID_categoria
    private Categoria categoria;

    // Construtores
    public Servico() {
    }

    // Getters e Setters
    public Long getIdServico() {
        return idServico;
    }

    public void setIdServico(Long idServico) {
        this.idServico = idServico;
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

    public byte[] getAnexo() {
        return anexo;
    }

    public void setAnexo(byte[] anexo) {
        this.anexo = anexo;
    }

    public Prestador getPrestador() {
        return prestador;
    }

    public void setPrestador(Prestador prestador) {
        this.prestador = prestador;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}