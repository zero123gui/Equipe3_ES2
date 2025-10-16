package br.com.eventos.api.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "login")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idlogin")
    private Integer id;

    @Column(name = "emailparticipante", unique = true)
    private String email;

    @Column(name = "senhausuario")
    private String senhaHash;

    @Column(name = "idparticipante", unique = true, nullable = false)
    private Integer participanteId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public Integer getParticipanteId() { return participanteId; }
    public void setParticipanteId(Integer participanteId) { this.participanteId = participanteId; }
}
