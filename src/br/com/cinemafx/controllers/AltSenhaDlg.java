package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.Conexao;
import br.com.cinemafx.models.Password;
import br.com.cinemafx.models.User;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class AltSenhaDlg {

    private static Boolean estruturado = false;
    private final PasswordField pwfOldSenha = new PasswordField();
    private static Dialog<Pair<Pair<String, String>, String>> dialog;

    public AltSenhaDlg() {
        if (!estruturado) estrutura();
    }

    public void show() {
        Platform.runLater(() -> pwfOldSenha.requestFocus());
        Optional<Pair<Pair<String, String>, String>> result = dialog.showAndWait();
        result.ifPresent(valores -> validFields(valores.getValue(), valores.getKey().getKey(), valores.getKey().getValue()));
    }

    private void validFields(String oldPass, String newPass, String newPass2) {
        if (User.getCurrent().getPassUsu().getUncryptedPassword().equals(oldPass)) {
            if (newPass.equals(newPass2)) {
                try {
                    Password password = changePassword(this.getClass(), User.getCurrent().getCodUsu(), newPass, false);
                    User.getCurrent().setPassUsu(password);
                    pwfOldSenha.clear();
                    new ModelDialog(this.getClass(), Alert.AlertType.INFORMATION, "Senha atualizada com sucesso").getAlert().showAndWait();
                } catch (Exception ex) {
                    new ModelException(this.getClass(), "Erro ao tentar atualizar senha do usuário\n" + ex.getMessage(), ex).getAlert().showAndWait();
                }
            } else {
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING, "Senha e confirmação de senha divergem").getAlert().showAndWait();
            }
        } else {
            new ModelDialog(this.getClass(), Alert.AlertType.WARNING, "Senha digitada incorretamente").getAlert().showAndWait();
        }
    }

    private void estrutura() {
        estruturado = true;
        dialog = new Dialog<Pair<Pair<String, String>, String>>();
        dialog.setTitle("Alterar senha do usuário");
        dialog.setHeaderText("Digite aqui a nova senha e confirmação");
        // Set the icon (must be included in the project).
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/br/com/cinemafx/views/images/VideoPlay.png"));
        dialog.setGraphic(new ImageView(new Image("/br/com/cinemafx/views/images/Icone_Senha.png")));
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Alterar Senha", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        pwfOldSenha.setPromptText("Senha Atual");
        PasswordField pwfSenha = new PasswordField();
        pwfSenha.setPromptText("Nova Senha");
        PasswordField pwfConfirma = new PasswordField();
        pwfConfirma.setPromptText("Confirmação");
        grid.add(new Label("Senha Atual"), 0, 0);
        grid.add(pwfOldSenha, 1, 0);
        grid.add(new Label("Nova Senha:"), 0, 1);
        grid.add(pwfSenha, 1, 1);
        grid.add(new Label("Confirmação:"), 0, 2);
        grid.add(pwfConfirma, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(new Pair<>(pwfSenha.getText(), pwfConfirma.getText()), pwfOldSenha.getText());
            }
            return null;
        });
    }

    public static Password changePassword(Class invocador, int codUsu, String novaSenha, Boolean crypted) throws Exception {
        Password password = new Password(crypted, novaSenha);
        Conexao conex = new Conexao(invocador);
        conex.createStatement("UPDATE TUSU SET SENHA = ? WHERE CODUSU = ?");
        conex.addParametro(password.getPassword(), codUsu);
        conex.execute();
        return password;
    }
}
