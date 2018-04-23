package br.com.cinemafx.methods.charts;

public class VendasPorExib {
    private int codExib = 0;
    private String nomeExib = "";
    private int qtdIngresso = 0;
    private Double vlrIngresso = 0.0;

    public VendasPorExib(int codExib, String nomeExib) {
        this.setCodExib(codExib);
        this.setNomeExib(nomeExib);
    }

    public void setQtdVlr(int qtd, Double vlr) {
        setQtdIngresso(qtd);
        setVlrIngresso(vlr);
    }

    public int getCodExib() {
        return codExib;
    }

    public void setCodExib(int codExib) {
        this.codExib = codExib;
    }

    public String getNomeExib() {
        return nomeExib;
    }

    public void setNomeExib(String nomeExib) {
        this.nomeExib = nomeExib;
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
