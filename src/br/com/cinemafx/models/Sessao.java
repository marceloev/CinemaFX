package br.com.cinemafx.models;

import br.com.cinemafx.dbcontrollers.DBObjects;

import java.sql.Timestamp;
import java.time.Instant;

public class Sessao {

    private int codSessao;
    private Sala sala;
    private Filme filme;
    private Exibicao exibicao;
    private Timestamp dataHoraExib;
    private Timestamp dataHoraFim;

    public Sessao() {
        this.setCodSessao(0);
        this.setSala(DBObjects.getSalas().get(0));
        this.setFilme(DBObjects.getFilmes().get(0));
        this.setExibicao(DBObjects.getExibicoes().get(0));
        this.setDataHoraExib(Timestamp.from(Instant.now()));
    }

    public Sessao(int codSessao, Sala sala, Filme filme, Exibicao exibicao, Timestamp dataHoraExib) {
        this.setCodSessao(codSessao);
        this.setSala(sala);
        this.setFilme(filme);
        this.setExibicao(exibicao);
        this.setDataHoraExib(dataHoraExib);
    }

    @Override
    public String toString() {
        return "Sessao{" +
                "codSessao=" + getCodSessao() +
                ", sala=" + getSala() +
                ", filme=" + getFilme() +
                ", exibicao=" + getExibicao() +
                ", dataHoraExib=" + getDataHoraExib() +
                ", dataHoraFim=" + getDataHoraFim() +
                '}';
    }

    public int getCodSessao() {
        return codSessao;
    }

    public void setCodSessao(int codSessao) {
        this.codSessao = codSessao;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public Exibicao getExibicao() {
        return exibicao;
    }

    public void setExibicao(Exibicao exibicao) {
        this.exibicao = exibicao;
    }

    public Timestamp getDataHoraExib() {
        return dataHoraExib;
    }

    public void setDataHoraExib(Timestamp dataHoraExib) {
        this.dataHoraExib = dataHoraExib;
        this.setDataHoraFim(Timestamp.valueOf(this.getDataHoraExib().toLocalDateTime().plusMinutes(getFilme().getMinFilme().longValue())));
    }

    public Timestamp getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(Timestamp dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

}
