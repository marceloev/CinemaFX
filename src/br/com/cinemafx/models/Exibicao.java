package br.com.cinemafx.models;

public class Exibicao {

    private int codExibicao;
    private String nomeExibicao;
    private Double vlrExibicao;

    public Exibicao() {
        this.codExibicao = 0;
        this.nomeExibicao = "";
        this.vlrExibicao = 0.0;
    }

    public Exibicao(int codExibicao, String nomeExibicao, Double vlrExibicao) {
        this.codExibicao = codExibicao;
        this.nomeExibicao = nomeExibicao;
        this.vlrExibicao = vlrExibicao;
    }

    @Override
    public String toString() {
        return "Exibicao{" +
                "codExibicao=" + codExibicao +
                ", nomeExibicao='" + nomeExibicao + '\'' +
                ", vlrExibicao=" + vlrExibicao +
                '}';
    }

    public int getCodExibicao() {
        return codExibicao;
    }

    public void setCodExibicao(int codExibicao) {
        this.codExibicao = codExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public void setNomeExibicao(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public Double getVlrExibicao() {
        return vlrExibicao;
    }

    public void setVlrExibicao(Double vlrExibicao) {
        this.vlrExibicao = vlrExibicao;
    }
}
