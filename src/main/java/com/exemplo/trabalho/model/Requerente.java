package com.exemplo.trabalho.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Requerente") // Verifique o nome exato da tabela no seu BD
public class Requerente {

    @Id
    @Column(name = "CPF", length = 14, unique = true, nullable = false)
    private String cpf;

    @Column(name = "Senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "Nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "Email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "Endereco", length = 255)
    private String endereco;

    @Column(name = "Telefone", length = 20)
    private String telefone;

    @OneToMany(mappedBy = "requerente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Requisicao> requisicoes;

    // Construtores
    public Requerente() {
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Requisicao> getRequisicoes() {
        return requisicoes;
    }

    public void setRequisicoes(List<Requisicao> requisicoes) {
        this.requisicoes = requisicoes;
    }
}