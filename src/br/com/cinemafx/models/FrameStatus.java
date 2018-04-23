package br.com.cinemafx.models;

public class FrameStatus {

    Status status = Status.Visualizando;

    public FrameStatus() {
        setStatus(Status.Visualizando);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        Carregando(-1), Visualizando(0), Alterando(1), Adicionando(2);

        private final int valor;

        Status(int valorOpcao) {
            valor = valorOpcao;
        }

        public int getValor() {
            return valor;
        }
    }
}