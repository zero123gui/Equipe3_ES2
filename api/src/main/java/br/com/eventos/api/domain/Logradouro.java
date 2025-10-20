package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "logradouro")
public class Logradouro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idlogradouro")
    private Integer id;

    @Column(name = "nomelogradouro", nullable = false, length = 40)
    private String nomeLogradouro;

    @Column(name = "idtipologradouro", nullable = false)
    private Integer idTipoLogradouro;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeLogradouro() { return nomeLogradouro; }
    public void setNomeLogradouro(String nomeLogradouro) { this.nomeLogradouro = nomeLogradouro; }

    public Integer getIdTipoLogradouro() { return idTipoLogradouro; }
    public void setIdTipoLogradouro(Integer idTipoLogradouro) { this.idTipoLogradouro = idTipoLogradouro; }
}
