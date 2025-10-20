package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "bairro")
public class Bairro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idbairro")
    private Integer id;

    @Column(name = "nomebairro", nullable = false, length = 40)
    private String nomeBairro;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeBairro() { return nomeBairro; }
    public void setNomeBairro(String nomeBairro) { this.nomeBairro = nomeBairro; }
}
