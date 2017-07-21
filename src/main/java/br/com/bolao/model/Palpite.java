/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bolao.model;

import br.com.bolao.utils.Datas;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "palpite")
public class Palpite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_jogo", referencedColumnName = "id")
    @ManyToOne
    private Jogo jogo;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne
    private Usuario usuario;
    @Temporal(TemporalType.DATE)
    @Column(name = "data_palpite")
    private Date dataPalpite;
    @Column(name = "hora_palpite", length = 10)
    private String horaPalpite;
    @Temporal(TemporalType.DATE)
    @Column(name = "data_atualizacao")
    private Date dataAtualizacao;
    @Column(name = "hora_atualizacao", length = 10)
    private String horaAtualizacao;
    @Column(name = "placar_time1")
    private Integer placarTime1;
    @Column(name = "placar_time2")
    private Integer placarTime2;
    @JoinColumn(name = "id_tipo_palpite", referencedColumnName = "id")
    @ManyToOne
    private TipoPalpite tipoPalpite;

    public Palpite() {
        this.id = -1;
        this.jogo = new Jogo();
        this.usuario = new Usuario();
        this.dataPalpite = null;
        this.horaPalpite = "";
        this.dataAtualizacao = null;
        this.horaAtualizacao = "";
        this.placarTime1 = null;
        this.placarTime2 = null;
        this.tipoPalpite = new TipoPalpite();
    }

    public Palpite(int id, Jogo jogo, Usuario usuario, Date dataPalpite, String horaPalpite, Date dataAtualizacao, String horaAtualizacao, Integer placarTime1, Integer placarTime2, TipoPalpite tipoPalpite) {
        this.id = id;
        this.jogo = jogo;
        this.usuario = usuario;
        this.dataPalpite = dataPalpite;
        this.horaPalpite = horaPalpite;
        this.dataAtualizacao = dataAtualizacao;
        this.horaAtualizacao = horaAtualizacao;
        this.placarTime1 = placarTime1;
        this.placarTime2 = placarTime2;
        this.tipoPalpite = tipoPalpite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDataPalpite() {
        return dataPalpite;
    }

    public void setDataPalpite(Date dataPalpite) {
        this.dataPalpite = dataPalpite;
    }

    public String getDataPalpiteString() {
        return Datas.converteData(dataPalpite);
    }

    public void setDataPalpiteString(String dataPalpiteString) {
        this.dataPalpite = Datas.converte(dataPalpiteString);
    }

    public String getHoraPalpite() {
        return horaPalpite;
    }

    public void setHoraPalpite(String horaPalpite) {
        this.horaPalpite = horaPalpite;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getDataAtualizacaoString() {
        return Datas.converteData(dataAtualizacao);
    }

    public void setDataAtualizacaoString(String dataAtualizacaoString) {
        this.dataAtualizacao = Datas.converte(dataAtualizacaoString);
    }

    public String getHoraAtualizacao() {
        return horaAtualizacao;
    }

    public void setHoraAtualizacao(String horaAtualizacao) {
        this.horaAtualizacao = horaAtualizacao;
    }

    public Integer getPlacarTime1() {
        return placarTime1;
    }

    public void setPlacarTime1(Integer placarTime1) {
        this.placarTime1 = placarTime1;
    }

    public Integer getPlacarTime2() {
        return placarTime2;
    }

    public void setPlacarTime2(Integer placarTime2) {
        this.placarTime2 = placarTime2;
    }

    public TipoPalpite getTipoPalpite() {
        return tipoPalpite;
    }

    public void setTipoPalpite(TipoPalpite tipoPalpite) {
        this.tipoPalpite = tipoPalpite;
    }

}
