package br.com.cinemafx.methods.charts;

public class VendasPorFilme {
    private int codFilme = 0;
    private String nomeFilme = "";
    private int qtdIngressosInt = 0;
    private Double vlrIntegral = 0.0;
    private int qtdIngressosMeia = 0;
    private Double vlrMeia = 0.0;

    public VendasPorFilme(int codFilme, String nomeFilme) {
        this.codFilme = codFilme;
        this.nomeFilme = nomeFilme;
    }

    @Override
    public String toString() {
        return "VendasPorFilme{" +
                "codFilme=" + codFilme +
                ", nomeFilme='" + nomeFilme + '\'' +
                ", qtdIngressosInt=" + qtdIngressosInt +
                ", vlrIntegral=" + vlrIntegral +
                ", qtdIngressosMeia=" + qtdIngressosMeia +
                ", vlrMeia=" + vlrMeia +
                '}';
    }

    public void setQtdVlr(Boolean integral, int qtd, Double value) {
        if (integral) {
            setQtdIngressosInt(qtd + getQtdIngressosInt());
            setVlrIntegral(value + getVlrIntegral());
        } else {
            setQtdIngressosMeia(qtd + getQtdIngressosMeia());
            setVlrMeia(value + getVlrMeia());
        }
    }

    public int getCodFilme() {
        return codFilme;
    }

    public void setCodFilme(int codFilme) {
        this.codFilme = codFilme;
    }

    public String getNomeFilme() {
        return nomeFilme;
    }

    public void setNomeFilme(String nomeFilme) {
        this.nomeFilme = nomeFilme;
    }

    public Double getVlrIntegral() {
        return vlrIntegral;
    }

    public void setVlrIntegral(Double vlrIntegral) {
        this.vlrIntegral = vlrIntegral;
    }

    public Double getVlrMeia() {
        return vlrMeia;
    }

    public void setVlrMeia(Double vlrMeia) {
        this.vlrMeia = vlrMeia;
    }

    public int getQtdIngressosInt() {
        return qtdIngressosInt;
    }

    public void setQtdIngressosInt(int qtdIngressosInt) {
        this.qtdIngressosInt = qtdIngressosInt;
    }

    public int getQtdIngressosMeia() {
        return qtdIngressosMeia;
    }

    public void setQtdIngressosMeia(int qtdIngressosMeia) {
        this.qtdIngressosMeia = qtdIngressosMeia;
    }
}