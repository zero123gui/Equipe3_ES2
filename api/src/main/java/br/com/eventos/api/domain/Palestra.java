package br.com.eventos.api.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "palestra")
public class Palestra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpalestra")
    private Integer id;

    @Column(name = "nomepalestra", nullable = false, length = 40)
    private String nomePalestra;

    @Column(name = "local", length = 40)
    private String local;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "qntdvagas", nullable = false)
    private Integer qtdVagas;

    @Column(name = "dtinicio", nullable = false)
    private LocalDate dtInicio;

    @Column(name = "dttermino", nullable = false)
    private LocalDate dtTermino;

    @Column(name = "horainicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "horatermino", nullable = false)
    private LocalTime horaTermino;

    @Column(name = "idevento", nullable = false)
    private Integer idEvento;

    // getters/setters
    public Integer getId() { return id; }
    public String getNomePalestra() { return nomePalestra; }
    public void setNomePalestra(String nomePalestra) { this.nomePalestra = nomePalestra; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getQtdVagas() { return qtdVagas; }
    public void setQtdVagas(Integer qtdVagas) { this.qtdVagas = qtdVagas; }
    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }
    public LocalDate getDtTermino() { return dtTermino; }
    public void setDtTermino(LocalDate dtTermino) { this.dtTermino = dtTermino; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraTermino() { return horaTermino; }
    public void setHoraTermino(LocalTime horaTermino) { this.horaTermino = horaTermino; }
    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }
}
