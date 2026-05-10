package br.edu.pucminas.pm.hospedagem.domain;

import br.edu.pucminas.pm.hospedagem.domain.quarto.Quarto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "residencia")
public class Residencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String nome;

    @Column(length = 255)
    private String endereco;

    @OneToMany(mappedBy = "residencia", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Quarto> quartos = new ArrayList<>();

    @JsonIgnore
    public List<Quarto> getQuartos() {
        return quartos;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
