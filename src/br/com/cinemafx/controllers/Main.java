package br.com.cinemafx.controllers;

import br.com.cinemafx.views.dialogs.FormattedDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(new Image("/br/com/cinemafx/views/images/VideoPlay.png"));
            primaryStage.setTitle("CinemaFX");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e -> {
                int resp = FormattedDialog.getYesNoDialog(this.getClass(),
                        "Deseja realmente finalizar o sistema?",
                        new String[]{"Finalizar", "Cancelar"});
                if (resp == 1) e.consume();
            });
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar carregar tela de Login\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        }
    }
}
