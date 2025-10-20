package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idendereco")
    private Integer id;

    @Column(name = "cep", nullable = false, length = 10)
    private String cep;

    @Column(name = "idlogradouro", nullable = false)
    private Integer idLogradouro;

    @Column(name = "idbairro", nullable = false)
    private Integer idBairro;

    @Column(name = "idcidade", nullable = false)
    private Integer idCidade;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public Integer getIdLogradouro() { return idLogradouro; }
    public void setIdLogradouro(Integer idLogradouro) { this.idLogradouro = idLogradouro; }

    public Integer getIdBairro() { return idBairro; }
    public void setIdBairro(Integer idBairro) { this.idBairro = idBairro; }

    public Integer getIdCidade() { return idCidade; }
    public void setIdCidade(Integer idCidade) { this.idCidade = idCidade; }
}
