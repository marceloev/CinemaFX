package br.com.cinemafx.methods.charts;

public class VendasPorSala {
    private int codSala = 0;
    private String nomeSala = "";
    private int qtdIngresso = 0;
    private Double vlrIngresso = 0.0;

    public VendasPorSala(int codSala, String nomeSala) {
        this.codSala = codSala;
        this.nomeSala = nomeSala;
    }

    public void setQtdVlr(int qtd, Double vlr) {
        setQtdIngresso(qtd);
        setVlrIngresso(vlr);
    }

    public int getCodSala() {
        return codSala;
    }

    public void setCodSala(int codSala) {
        this.codSala = codSala;
    }

    public String getNomeSala() {
        return nomeSala;
    }

    public void setNomeSala(String nomeSala) {
        this.nomeSala = nomeSala;
    }

    public int getQtdIngresso() {
        return qtdIngresso;
    }

    public void setQtdIngresso(int qtdIngresso) {
        this.qtdIngresso = qtdIngresso + this.qtdIngresso;
    }

    public Double getVlrIngresso() {
        return vlrIngresso;
    }

    public void setVlrIngresso(Double vlrIngresso) {
        this.vlrIngresso = vlrIngresso + this.vlrIngresso;
    }
}