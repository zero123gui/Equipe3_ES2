package br.com.eventos.api.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevento")
    private Integer id;

    @Column(name = "nomeevento")
    private String nomeEvento;

    @Column(name = "dtinicio")
    private LocalDate dtInicio;

    @Column(name = "dttermino")
    private LocalDate dtTermino;

    @Column(name = "local")
    private String local;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "urlsite")
    private String urlSite;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeEvento() { return nomeEvento; }
    public void setNomeEvento(String nomeEvento) { this.nomeEvento = nomeEvento; }

    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }

    public LocalDate getDtTermino() { return dtTermino; }
    public void setDtTermino(LocalDate dtTermino) { this.dtTermino = dtTermino; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUrlSite() { return urlSite; }
    public void setUrlSite(String urlSite) { this.urlSite = urlSite; }
}
