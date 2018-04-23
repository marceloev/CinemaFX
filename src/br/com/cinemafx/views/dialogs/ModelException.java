package br.com.cinemafx.views.dialogs;

import br.com.cinemafx.methods.log.GravaLog;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

public class ModelException extends Alert {

    private Class invocador;
    private Throwable execption;

    public ModelException(Class invocador, String contentText) {
        super(AlertType.ERROR, contentText);
        setInvocador(invocador);
    }

    public ModelException(Class invocador, String contentText, Throwable ex) {
        super(AlertType.ERROR, contentText);
        setInvocador(invocador);
        setExecption(ex);
    }

    public Alert getAlert() {
        this.setTitle("CinemaFX:");
        this.setHeaderText(null);
        this.setGraphic(null);
        if (getExecption() != null) {
            GridPane gridPane = GridPaneBuilder.create()
                    .maxWidth(Double.MAX_VALUE)
                    .maxHeight(Double.MAX_VALUE)
                    .build();
            Label label = LabelBuilder.create()
                    .text("Veja aqui o caminho completo do erro:")
                    .textFill(Color.BLACK)
                    .font(Font.font("System", FontPosture.ITALIC, 14))
                    .build();
            CharArrayWriter charArrayWriter = new CharArrayWriter();
            PrintWriter printWriter = new PrintWriter(charArrayWriter);
            getExecption().printStackTrace(printWriter);
            printWriter.close();
            TextArea textArea = TextAreaBuilder.create()
                    .text(charArrayWriter.toString())
                    .maxWidth(Double.MAX_VALUE)
                    .maxHeight(Double.MAX_VALUE)
                    .build();
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            gridPane.add(label, 0, 0);
            gridPane.add(textArea, 0, 1);
            this.getDialogPane().setExpandableContent(gridPane);
            GravaLog.gravaErro(getInvocador(), getContentText(), getExecption());
            this.getDialogPane().maxWidth(Double.MAX_VALUE);
        } else {
            GravaLog.gravaErro(getInvocador(), getContentText());
        }

        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/br/com/cinemafx/views/images/Icone_Error.png"));
        return this;
    }

    public Class getInvocador() {
        return invocador;
    }

    public void setInvocador(Class invocador) {
        this.invocador = invocador;
    }

    public Throwable getExecption() {
        return execption;
    }

    public void setExecption(Throwable execption) {
        this.execption = execption;
    }
}
