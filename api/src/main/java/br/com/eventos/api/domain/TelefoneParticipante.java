package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "telefone_participante")
public class TelefoneParticipante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtelefone")
    private Integer id;

    @Column(name = "nrotelefone", nullable = false, length = 15)
    private String nroTelefone;

    // Mantendo como FK "crua" (Integer) para evitar dependências circulares.
    // Se quiser relacionamento, dá pra trocar por @ManyToOne.
    @Column(name = "idparticipante", nullable = false)
    private Integer idParticipante;

    @Column(name = "idddd", nullable = false)
    private Integer idDdd;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNroTelefone() { return nroTelefone; }
    public void setNroTelefone(String nroTelefone) { this.nroTelefone = nroTelefone; }

    public Integer getIdParticipante() { return idParticipante; }
    public void setIdParticipante(Integer idParticipante) { this.idParticipante = idParticipante; }

    public Integer getIdDdd() { return idDdd; }
    public void setIdDdd(Integer idDdd) { this.idDdd = idDdd; }
}
