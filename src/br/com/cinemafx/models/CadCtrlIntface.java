package br.com.cinemafx.models;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public interface CadCtrlIntface {

    Timeline timeline = new Timeline();
    BooleanProperty atualizando = new SimpleBooleanProperty(false);
    FrameStatus frameStatus = new FrameStatus();
    Image imgForm = new Image("/br/com/cinemafx/views/images/Icone_Modo_Formulario.png");
    Image imgGrade = new Image("/br/com/cinemafx/views/images/Icone_Modo_Grade.png");
    Image imgSucesso = new Image("/br/com/cinemafx/views/images/Sucesso.png");
    Image imgAlerta = new Image("/br/com/cinemafx/views/images/Alerta.png");

    void estrutura();

    void appCalls();

    void init();

    void loadTableValues();

    TableColumn[] getTableColumns();

    void ctrlAction(FrameAction frameAction);

    default void sendMensagem(Label lbl, Boolean sucesso, String mensagem) {
        if (sucesso) {
            timeline.stop();
            lbl.setGraphic(new ImageView(imgSucesso));
            lbl.setVisible(true);
            lbl.setText(mensagem);
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(2000),
                            ae -> lbl.setVisible(false)));
            timeline.play();
        } else {
            timeline.stop();
            lbl.setGraphic(new ImageView(imgAlerta));
            lbl.setVisible(true);
            lbl.setText(mensagem);
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(4000),
                            ae -> lbl.setVisible(false)));
            timeline.play();
        }
    }

    default boolean isAtualizando() {
        return atualizando.getValue();
    }

    default void setAtualizando(Boolean valor) {
        atualizando.setValue(valor);
    }

    default FrameStatus.Status getFrameStatus() {
        return frameStatus.status;
    }

    default void setFrameStatus(FrameStatus.Status status) {
        frameStatus.setStatus(status);
    }

    default void notifyEdit(Button btnEdit, Runnable changes) {
        if (getFrameStatus() == FrameStatus.Status.Visualizando) {
            setFrameStatus(FrameStatus.Status.Alterando);
            btnEdit.fire();
        }
        changes.run();
    }

    default void runEdits(Runnable runnable) {
        setAtualizando(true);
        runnable.run();
        setAtualizando(false);
    }
}
