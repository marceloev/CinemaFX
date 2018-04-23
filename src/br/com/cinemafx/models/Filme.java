package br.com.cinemafx.models;

import br.com.cinemafx.dbcontrollers.DBObjects;
import br.com.cinemafx.methods.Functions;
import javafx.scene.image.Image;

public class Filme {

    private int codFilme;
    private String nomeFilme;
    private String sinopse;
    private Double custoFilme;
    private Genero genero;
    private int minFilme;
    private Image cartazFilme;

    public Filme() {
        this.codFilme = 0;
        this.nomeFilme = "";
        this.sinopse = "";
        this.custoFilme = 0.0;
        this.genero = DBObjects.reloadGeneros().get(0); //Sem Gênero
        this.minFilme = 0;
        this.cartazFilme = Functions.noImageFilme;
    }

    public Filme(int codFilme, String nomeFilme, String sinopse, Double custoFilme, Genero genero, int minFilme, Image cartazFilme) {
        this.codFilme = codFilme;
        this.nomeFilme = nomeFilme;
        this.sinopse = sinopse;
        this.custoFilme = custoFilme;
        this.genero = genero;
        this.minFilme = minFilme;
        this.cartazFilme = cartazFilme;
    }

    @Override
    public String toString() {
        return String.format("%d - %s", this.codFilme, this.nomeFilme);
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

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public Double getCustoFilme() {
        return custoFilme;
    }

    public void setCustoFilme(Double custoFilme) {
        this.custoFilme = custoFilme;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Integer getMinFilme() {
        return minFilme;
    }

    public void setMinFilme(int minFilme) {
        this.minFilme = minFilme;
    }

    public Image getCartazFilme() {
        return cartazFilme;
    }

    public void setCartazFilme(Image cartazFilme) {
        this.cartazFilme = cartazFilme;
    }
}
