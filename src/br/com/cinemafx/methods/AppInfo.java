package br.com.cinemafx.methods;

import java.sql.Timestamp;

public class AppInfo {

    private static Info info = new Info();

    public static Info getInfo() {
        return info;
    }

    public static class Info {

        private String VersaoExec;
        private String VersaoBD;
        private String IPMaquina;
        private String NomeMaquina;
        private Timestamp dhLogin;

        public String getIPMaquina() {
            return IPMaquina;
        }

        public void setIPMaquina(String IPMaquina) {
            this.IPMaquina = IPMaquina;
        }

        public String getNomeMaquina() {
            return NomeMaquina;
        }

        public void setNomeMaquina(String nomeMaquina) {
            NomeMaquina = nomeMaquina;
        }

        public String getVersaoExec() {
            return VersaoExec;
        }

        public void setVersaoExec(String versaoExec) {
            VersaoExec = versaoExec;
        }

        public String getVersaoBD() {
            return VersaoBD;
        }

        public void setVersaoBD(String versaoBD) {
            VersaoBD = versaoBD;
        }

        public Timestamp getDhLogin() {
            return dhLogin;
        }

        public void setDhLogin(Timestamp dhLogin) {
            this.dhLogin = dhLogin;
        }
    }
}
