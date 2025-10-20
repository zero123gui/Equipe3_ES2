package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cidade")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcidade")
    private Integer id;

    @Column(name = "nomecidade", nullable = false, length = 40)
    private String nomeCidade;

    @Column(name = "iduf", nullable = false)
    private Integer idUf;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeCidade() { return nomeCidade; }
    public void setNomeCidade(String nomeCidade) { this.nomeCidade = nomeCidade; }

    public Integer getIdUf() { return idUf; }
    public void setIdUf(Integer idUf) { this.idUf = idUf; }
}
