// src/main/java/br/com/eventos/api/domain/Evento.java
package br.com.eventos.api.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evento", schema = "eventos_r1s1")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevento")
    private Integer id;

    @Column(name = "nomeevento", nullable = false, length = 40)
    private String nomeEvento;

    @Column(name = "dtinicio", nullable = false)
    private LocalDate dtInicio;

    @Column(name = "dttermino", nullable = false)
    private LocalDate dtTermino;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "urlsite", length = 50)
    private String urlSite;

    // NOVOS CAMPOS (no lugar do antigo "local")
    @Column(name = "idendereco", nullable = false)
    private Integer idEndereco;

    @Column(name = "nroendereco", length = 10)
    private String nroEndereco;

    @Column(name = "complementoendereco", length = 50)
    private String complementoEndereco;

    // getters/setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeEvento() { return nomeEvento; }
    public void setNomeEvento(String nomeEvento) { this.nomeEvento = nomeEvento; }

    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }

    public LocalDate getDtTermino() { return dtTermino; }
    public void setDtTermino(LocalDate dtTermino) { this.dtTermino = dtTermino; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUrlSite() { return urlSite; }
    public void setUrlSite(String urlSite) { this.urlSite = urlSite; }

    public Integer getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Integer idEndereco) { this.idEndereco = idEndereco; }

    public String getNroEndereco() { return nroEndereco; }
    public void setNroEndereco(String nroEndereco) { this.nroEndereco = nroEndereco; }

    public String getComplementoEndereco() { return complementoEndereco; }
    public void setComplementoEndereco(String complementoEndereco) { this.complementoEndereco = complementoEndereco; }
}
