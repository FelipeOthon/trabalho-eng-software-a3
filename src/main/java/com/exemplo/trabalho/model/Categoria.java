package com.exemplo.trabalho.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Categoria") // Verifique se o nome da tabela é "Categoria" no seu BD
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assumindo ID autoincrementável
    @Column(name = "ID_categoria")
    private Integer idCategoria;

    @Column(name = "NomeCategoria", nullable = false, length = 100)
    private String nomeCategoria;

    // Construtores
    public Categoria() {
    }

    public Categoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    // Getters e Setters
    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }
}