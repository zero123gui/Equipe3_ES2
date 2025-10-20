package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_inscricao")
public class TipoInscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipoinscricao")
    private Integer id;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
