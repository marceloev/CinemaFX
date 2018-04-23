package br.com.cinemafx.models;

import javafx.util.Pair;

public class SessaoFilme {

    private Pair<Integer, String> sala;
    private Pair<Integer, String> exib;
    private Filme filme;

    public SessaoFilme() {
        this.setSala(new Pair<>(1, ""));
        this.setExib(new Pair<>(8, ""));
        this.setFilme(new Filme());
        this.getFilme().setCodFilme(17);
    }

    public SessaoFilme(Pair<Integer, String> sala, Pair<Integer, String> exib, Filme filme) {
        this.sala = sala;
        this.exib = exib;
        this.filme = filme;
    }

    public Pair<Integer, String> getSala() {
        return sala;
    }

    public void setSala(Pair<Integer, String> sala) {
        this.sala = sala;
    }

    public Pair<Integer, String> getExib() {
        return exib;
    }

    public void setExib(Pair<Integer, String> exib) {
        this.exib = exib;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }
}
