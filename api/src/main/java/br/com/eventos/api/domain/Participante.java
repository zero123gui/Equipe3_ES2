package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "participante")
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idparticipante")
    private Integer id;  // integer no banco

    @Column(name = "nomeparticipante", nullable = false, length = 255)
    private String nome;

    @Column(name = "idtipoparticipante", nullable = false)
    private Integer idTipoParticipante;  // integer

    @Column(name = "idendereco", nullable = false)
    private Integer idEndereco;          // integer

    @Column(name = "complementoendereco")
    private String complementoEndereco;

    @Column(name = "nroendereco")
    private String nroEndereco;

    public Participante() {}

    public Participante(Integer id, String nome, Integer idTipoParticipante, Integer idEndereco,
                        String complementoEndereco, String nroEndereco) {
        this.id = id;
        this.nome = nome;
        this.idTipoParticipante = idTipoParticipante;
        this.idEndereco = idEndereco;
        this.complementoEndereco = complementoEndereco;
        this.nroEndereco = nroEndereco;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdTipoParticipante() { return idTipoParticipante; }
    public void setIdTipoParticipante(Integer idTipoParticipante) { this.idTipoParticipante = idTipoParticipante; }

    public Integer getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Integer idEndereco) { this.idEndereco = idEndereco; }

    public String getComplementoEndereco() { return complementoEndereco; }
    public void setComplementoEndereco(String complementoEndereco) { this.complementoEndereco = complementoEndereco; }

    public String getNroEndereco() { return nroEndereco; }
    public void setNroEndereco(String nroEndereco) { this.nroEndereco = nroEndereco; }
}
