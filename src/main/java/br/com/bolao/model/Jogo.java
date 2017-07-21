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
@Table(name = "jogo")
public class Jogo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_time1", referencedColumnName = "id")
    @ManyToOne
    private Time time1;
    @JoinColumn(name = "id_time2", referencedColumnName = "id")
    @ManyToOne
    private Time time2;
    @Column(name = "placar_time1")
    private Integer placarTime1;
    @Column(name = "placar_time2")
    private Integer placarTime2;
    @Temporal(TemporalType.DATE)
    @Column(name = "data_jogo")
    private Date dataJogo;
    @Column(name = "hora_jogo", length = 10)
    private String horaJogo;
    @Temporal(TemporalType.DATE)
    @Column(name = "data_jogo_final")
    private Date dataJogoFinal;
    @Column(name = "hora_jogo_final", length = 10)
    private String horaJogoFinal;
    @Column(name = "local")
    private String local;
    @JoinColumn(name = "id_bolao", referencedColumnName = "id")
    @ManyToOne
    private Bolao bolao;
    @Column(name = "importancia")
    private Integer importancia;
    
    public Jogo() {
        this.id = -1;
        this.time1 = new Time();
        this.time2 = new Time();
        this.placarTime1 = null;
        this.placarTime2 = null;
        this.dataJogo = null;
        this.horaJogo = "";
        this.dataJogoFinal = null;
        this.horaJogoFinal = "";
        this.local = "";
        this.bolao = new Bolao();
        this.importancia = 1;
    }

    public Jogo(int id, Time time1, Time time2, Integer placarTime1, Integer placarTime2, Date dataJogo, String horaJogo, Date dataJogoFinal, String horaJogoFinal, String local, Bolao bolao, Integer importancia) {
        this.id = id;
        this.time1 = time1;
        this.time2 = time2;
        this.placarTime1 = placarTime1;
        this.placarTime2 = placarTime2;
        this.dataJogo = dataJogo;
        this.horaJogo = horaJogo;
        this.dataJogoFinal = dataJogoFinal;
        this.horaJogoFinal = horaJogoFinal;
        this.local = local;
        this.bolao = bolao;
        this.importancia = importancia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Time getTime1() {
        return time1;
    }

    public void setTime1(Time time1) {
        this.time1 = time1;
    }

    public Time getTime2() {
        return time2;
    }

    public void setTime2(Time time2) {
        this.time2 = time2;
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

    public Date getDataJogo() {
        return dataJogo;
    }

    public void setDataJogo(Date dataJogo) {
        this.dataJogo = dataJogo;
    }
    
    public String getDataJogoString() {
        return Datas.converteData(dataJogo);
    }

    public void setDataJogoString(String dataJogoString) {
        this.dataJogo = Datas.converte(dataJogoString);
    }
    
    public String getDataJogoStringDia() {
        return Datas.diaSemanaString(getDataJogoString());
    }

    public String getHoraJogo() {
        return horaJogo;
    }

    public void setHoraJogo(String horaJogo) {
        this.horaJogo = horaJogo;
    }

    public Date getDataJogoFinal() {
        return dataJogoFinal;
    }

    public void setDataJogoFinal(Date dataJogoFinal) {
        this.dataJogoFinal = dataJogoFinal;
    }
    
    public String getDataJogoFinalString() {
        return Datas.converteData(dataJogoFinal);
    }

    public void setDataJogoFinalString(String dataJogoFinalString) {
        this.dataJogoFinal = Datas.converte(dataJogoFinalString);
    }

    public String getHoraJogoFinal() {
        return horaJogoFinal;
    }

    public void setHoraJogoFinal(String horaJogoFinal) {
        this.horaJogoFinal = horaJogoFinal;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Bolao getBolao() {
        return bolao;
    }

    public void setBolao(Bolao bolao) {
        this.bolao = bolao;
    }

    public Integer getImportancia() {
        return importancia;
    }

    public void setImportancia(Integer importancia) {
        this.importancia = importancia;
    }

}
