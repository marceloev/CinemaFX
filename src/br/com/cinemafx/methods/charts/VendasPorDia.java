package br.com.cinemafx.methods.charts;

import java.sql.Date;

public class VendasPorDia {
    private Date dataVenda;
    private int qtdVendaInt = 0;
    private Double vlrVendaInt = 0.0;
    private int qtdVendaMeia = 0;
    private Double vlrVendaMeia = 0.0;

    public VendasPorDia(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public void setVlrQtd(Boolean integral, int qtdVenda, Double vlrVenda) {
        if(integral) {
            setQtdVendaInt(qtdVenda);
            setVlrVendaInt(vlrVenda);
        } else {
            setQtdVendaMeia(qtdVenda);
            setVlrVendaMeia(vlrVenda);
        }
    }

    public Date getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public int getQtdVendaInt() {
        return qtdVendaInt;
    }

    public void setQtdVendaInt(int qtdVendaInt) {
        this.qtdVendaInt = qtdVendaInt + this.qtdVendaInt;
    }

    public Double getVlrVendaInt() {
        return vlrVendaInt;
    }

    public void setVlrVendaInt(Double vlrVendaInt) {
        this.vlrVendaInt = vlrVendaInt + this.vlrVendaInt;
    }

    public int getQtdVendaMeia() {
        return qtdVendaMeia;
    }

    public void setQtdVendaMeia(int qtdVendaMeia) {
        this.qtdVendaMeia = qtdVendaMeia + this.qtdVendaMeia;
    }

    public Double getVlrVendaMeia() {
        return vlrVendaMeia;
    }

    public void setVlrVendaMeia(Double vlrVendaMeia) {
        this.vlrVendaMeia = vlrVendaMeia + this.vlrVendaMeia;
    }
}
