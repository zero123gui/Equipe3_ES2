package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_logradouro")
public class TipoLogradouro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipologradouro")
    private Integer id;

    @Column(name = "siglalogradouro", nullable = false, length = 10, unique = true)
    private String siglaLogradouro;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSiglaLogradouro() { return siglaLogradouro; }
    public void setSiglaLogradouro(String siglaLogradouro) { this.siglaLogradouro = siglaLogradouro; }
}
