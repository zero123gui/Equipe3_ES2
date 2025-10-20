package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "unidade_federacao")
public class UnidadeFederacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iduf")
    private Integer id;

    @Column(name = "siglauf", nullable = false, length = 5)
    private String siglaUf;

    @Column(name = "nomeuf", nullable = false, length = 40)
    private String nomeUf;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSiglaUf() { return siglaUf; }
    public void setSiglaUf(String siglaUf) { this.siglaUf = siglaUf; }

    public String getNomeUf() { return nomeUf; }
    public void setNomeUf(String nomeUf) { this.nomeUf = nomeUf; }
}
