package br.com.cinemafx.models;

import java.sql.Timestamp;

public class Vendas {

    private int codSessao;
    private int codFilme;
    private int codExib;
    private int codSala;
    private boolean integral;
    private Integer qtdIngresso;
    private Timestamp dataHoraSes;
    private Double vlrTotal;

    public Vendas(int codSessao, int codFilme, int codExib, int codSala, boolean integral, Timestamp datahora, Integer qtdIngresso, Double vlrTotal) {
        this.codSessao = codSessao;
        this.codFilme = codFilme;
        this.codExib = codExib;
        this.codSala = codSala;
        this.integral = integral;
        this.qtdIngresso = qtdIngresso;
        this.dataHoraSes = datahora;
        this.vlrTotal = vlrTotal;
    }

    public int getCodSessao() {
        return codSessao;
    }

    public void setCodSessao(int codSessao) {
        this.codSessao = codSessao;
    }

    public int getCodFilme() {
        return codFilme;
    }

    public void setCodFilme(int codFilme) {
        this.codFilme = codFilme;
    }

    public int getCodExib() {
        return codExib;
    }

    public void setCodExib(int codExib) {
        this.codExib = codExib;
    }

    public int getCodSala() {
        return codSala;
    }

    public void setCodSala(int codSala) {
        this.codSala = codSala;
    }

    public boolean isIntegral() {
        return integral;
    }

    public void setIntegral(boolean integral) {
        this.integral = integral;
    }

    public Double getVlrTotal() {
        return vlrTotal;
    }

    public void setVlrTotal(Double vlrTotal) {
        this.vlrTotal = vlrTotal;
    }

    public Integer getQtdIngresso() {
        return qtdIngresso;
    }

    public void setQtdIngresso(Integer qtdIngresso) {
        this.qtdIngresso = qtdIngresso;
    }

    public Timestamp getDataHoraSes() {
        return dataHoraSes;
    }

    public void setDataHoraSes(Timestamp dataHoraSes) {
        this.dataHoraSes = dataHoraSes;
    }
}
