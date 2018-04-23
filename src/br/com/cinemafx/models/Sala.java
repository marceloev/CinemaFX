package br.com.cinemafx.models;

public class Sala {

    private int codSala;
    private String refSala;
    private int capacidade;

    public Sala() {
        this.codSala = 0;
        this.refSala = "";
        this.capacidade = 0;
    }

    public Sala(int codSala, String refSala, int capacidade) {
        this.codSala = codSala;
        this.refSala = refSala;
        this.capacidade = capacidade;
    }

    @Override
    public String toString() {
        return "Sala{" +
                "codSala=" + codSala +
                ", refSala='" + refSala + '\'' +
                ", capacidade=" + capacidade +
                '}';
    }

    public int getCodSala() {
        return codSala;
    }

    public void setCodSala(int codSala) {
        this.codSala = codSala;
    }

    public String getRefSala() {
        return refSala;
    }

    public void setRefSala(String refSala) {
        this.refSala = refSala;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
}
