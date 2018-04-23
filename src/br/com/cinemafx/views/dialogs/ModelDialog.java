package br.com.cinemafx.views.dialogs;

import br.com.cinemafx.methods.log.GravaLog;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

public class ModelDialog extends Alert {

    private Class invocador;
    private String tips;

    public ModelDialog(Class invocador, AlertType alertType, String contentText) {
        super(alertType, contentText);
        setInvocador(invocador);
    }

    public ModelDialog(Class invocador, AlertType alertType, String contentText, String tips) {
        super(alertType, contentText);
        setInvocador(invocador);
        setTips(tips);
    }

    public Alert getAlert() {
        this.setTitle("CinemaFX:");
        this.setHeaderText(null);
        this.setGraphic(null);
        if (getTips() != null && !getTips().isEmpty()) {
            GridPane gridPane = GridPaneBuilder.create()
                    .maxWidth(Double.MAX_VALUE)
                    .build();
            Label label = LabelBuilder.create()
                    .text("Veja aqui dicas de como solucionar o ocorrido:")
                    .font(Font.font("System", FontPosture.ITALIC, 14))
                    .build();
            TextArea textArea = TextAreaBuilder.create()
                    .text(getTips())
                    .maxWidth(Double.MAX_VALUE)
                    .maxHeight(Double.MAX_VALUE)
                    .build();
            gridPane.add(label, 0, 0);
            gridPane.add(textArea, 0, 1);
            this.getDialogPane().setExpandableContent(gridPane);
        }
        createInfo();
        return this;
    }

    private void createInfo() {
        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        switch (this.getAlertType()) {
            case NONE:
            case INFORMATION:
            case CONFIRMATION:
                GravaLog.gravaInfo(this.getInvocador(), this.getContentText());
                stage.getIcons().add(new Image("/br/com/cinemafx/views/images/Icone_Informação.png"));
                break;
            case WARNING:
                GravaLog.gravaAlerta(this.getInvocador(), this.getContentText());
                stage.getIcons().add(new Image("/br/com/cinemafx/views/images/Icone_Alerta.png"));
                break;
            case ERROR:
                GravaLog.gravaAlerta(this.getInvocador(), this.getContentText());
                stage.getIcons().add(new Image("/br/com/cinemafx/views/images/Icone_Error.png"));
                break;
        }
    }

    public Class getInvocador() {
        return invocador;
    }

    public void setInvocador(Class invocador) {
        this.invocador = invocador;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
