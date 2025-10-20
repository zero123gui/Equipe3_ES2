package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ddd")
public class Ddd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idddd")
    private Integer id;

    @Column(name = "nroddd", nullable = false)
    private Integer nroDdd;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getNroDdd() { return nroDdd; }
    public void setNroDdd(Integer nroDdd) { this.nroDdd = nroDdd; }
}
