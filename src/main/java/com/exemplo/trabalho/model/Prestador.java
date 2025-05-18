package com.exemplo.trabalho.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "prestador") // Minúsculo
public class Prestador {

    @Id
    @Column(name = "cpf", length = 14, unique = true, nullable = false) // Minúsculo
    private String cpf;

    @Column(name = "senha", nullable = false, length = 255) // Minúsculo
    private String senha;

    @Column(name = "nome", nullable = false, length = 200) // Minúsculo
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 150) // Minúsculo
    private String email;

    @Column(name = "endereco", length = 255) // Minúsculo
    private String endereco;

    @Column(name = "telefone", length = 20) // Minúsculo
    private String telefone;

    @OneToMany(mappedBy = "prestador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Servico> servicos;

    // Construtores
    public Prestador() {
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

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }
}