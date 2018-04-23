package br.com.cinemafx.models;

public class Genero {

    private int codGenero;
    private String nomeGenero;

    public Genero(int codGenero, String nomeGenero) {
        this.codGenero = codGenero;
        this.nomeGenero = nomeGenero;
    }

    @Override
    public String toString() {
        return getNomeGenero();
    }

    public int getCodGenero() {
        return codGenero;
    }

    public void setCodGenero(int codGenero) {
        this.codGenero = codGenero;
    }

    public String getNomeGenero() {
        return nomeGenero;
    }

    public void setNomeGenero(String nomeGenero) {
        this.nomeGenero = nomeGenero;
    }
}
