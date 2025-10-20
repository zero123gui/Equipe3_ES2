package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "participante_evento",
        uniqueConstraints = @UniqueConstraint(name = "uq_participante_evento", columnNames = {"idevento","idparticipante"}))
public class ParticipanteEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idparticipanteevento")
    private Integer id;

    @Column(name = "idevento", nullable = false)
    private Integer idEvento;

    @Column(name = "idtipoinscricao", nullable = false)
    private Integer idTipoInscricao;

    @Column(name = "idparticipante", nullable = false)
    private Integer idParticipante;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }

    public Integer getIdTipoInscricao() { return idTipoInscricao; }
    public void setIdTipoInscricao(Integer idTipoInscricao) { this.idTipoInscricao = idTipoInscricao; }

    public Integer getIdParticipante() { return idParticipante; }
    public void setIdParticipante(Integer idParticipante) { this.idParticipante = idParticipante; }
}
