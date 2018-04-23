package br.com.cinemafx.models;

public class User {

    private static User current;

    private int codUsu;
    private String nomeUsu;
    private String loginUsu;
    private Password passUsu;
    private Boolean ativoUsu;

    public User() {
        this.setCodUsu(0);
        this.setNomeUsu("");
        this.setLoginUsu("");
        this.setPassUsu(new Password("", ""));
        this.setAtivoUsu(true);
    }

    public User(int codUsu, String nomeUsu, String loginUsu, Password passUsu, Boolean ativoUsu) {
        this.setCodUsu(codUsu);
        this.setNomeUsu(nomeUsu);
        this.setLoginUsu(loginUsu);
        this.setPassUsu(passUsu);
        this.setAtivoUsu(ativoUsu);
    }

    public static User getCurrent() {
        return current;
    }

    public static void setCurrent(User current) {
        User.current = current;
    }

    @Override
    public String toString() {
        return "User{" +
                "codUsu=" + codUsu +
                ", nomeUsu='" + nomeUsu + '\'' +
                ", loginUsu='" + loginUsu + '\'' +
                ", passUsu=" + passUsu +
                ", ativoUsu=" + ativoUsu +
                '}';
    }

    public int getCodUsu() {
        return codUsu;
    }

    public void setCodUsu(int codUsu) {
        this.codUsu = codUsu;
    }

    public String getNomeUsu() {
        return nomeUsu;
    }

    public void setNomeUsu(String nomeUsu) {
        this.nomeUsu = nomeUsu;
    }

    public String getLoginUsu() {
        return loginUsu;
    }

    public void setLoginUsu(String loginUsu) {
        this.loginUsu = loginUsu;
    }

    public Password getPassUsu() {
        return passUsu;
    }

    public void setPassUsu(Password passUsu) {
        this.passUsu = passUsu;
    }

    public Boolean getAtivoUsu() {
        return ativoUsu;
    }

    public void setAtivoUsu(Boolean ativoUsu) {
        this.ativoUsu = ativoUsu;
    }
}
