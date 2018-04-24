package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.Conexao;
import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.methods.MaskField;
import br.com.cinemafx.models.Password;
import br.com.cinemafx.models.User;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginCtrl implements Initializable {

    private SimpleBooleanProperty logginIn = new SimpleBooleanProperty(false);
    //private Pair<String, String> passHidden = new Pair<>("", "");

    @FXML
    private AnchorPane pane;
    @FXML
    private Label lblShowSenha;
    @FXML
    private TextField txfLogin;
    @FXML
    private PasswordField pwfSenha;
    @FXML
    private ProgressBar pgbLoad;
    @FXML
    private Button btnLogin;
    @FXML
    private Hyperlink hplSite, hplDesenv;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
    }

    private void estrutura() {
        MaskField.MaxUpperCharField(txfLogin, 25);
        txfLogin.setOnAction(e -> startLogin());
        MaskField.MaxCharField(pwfSenha, 25);
        pwfSenha.setOnAction(e -> startLogin());
        btnLogin.setOnAction(e -> startLogin());
        hplSite.setOnAction(e -> openNavHyperLink("https://cinemafx.com.br"));
        hplDesenv.setOnAction(e -> openNavHyperLink("https://github.com/marceloev"));
        logginIn.addListener((observable, oldV, newV) -> {
            pgbLoad.setVisible(newV);
            pane.setDisable(newV);
        });
        /*pwfSenha.textProperty().addListener((obs, oldV, newV) -> {
            String pass = "";
            for (int i = 0; newV.length() > i; i++)
                pass = pass.concat("*");
            passHidden = new Pair<>(newV, pass);
        });*/
        //lblShowSenha.getTooltip().textProperty().bind(pwfSenha.textProperty());
    }

    private void startLogin() {
        //Não precisa validar se já logou, pois, o logginIn property desabilita o mainPane;
        logginIn.setValue(true);
        Conexao conex = new Conexao(this.getClass());
        try {
            conex.createStatement("SELECT COUNT(1) FROM TUSU WHERE LOGIN = ?");
            conex.addParametro(txfLogin.getText());
            conex.createSet();
            conex.rs.next();
            int count = conex.rs.getInt(1);
            if (count == 0)
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("Usuário não encontrado para login digitado")).getAlert().showAndWait();
            else
                getUserInfo();
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar buscar usuário\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            logginIn.setValue(false);
            conex.desconecta();
        }
    }

    private void getUserInfo() {
        Conexao conex = new Conexao(this.getClass());
        try {
            conex.createStatement("SELECT CODUSU, NOMEUSU, LOGIN, SENHA, ATIVO\n" +
                    "FROM TUSU\n" +
                    "WHERE LOGIN = ?");
            conex.addParametro(txfLogin.getText());
            conex.createSet();
            conex.rs.next();
            User.setCurrent(new User(
                    conex.rs.getInt(1),
                    conex.rs.getString(2),
                    conex.rs.getString(3),
                    new Password(true, conex.rs.getString(4)),
                    Functions.ToBoo(conex.rs.getString(5))
            ));
            if (pwfSenha.getText().equals(User.getCurrent().getPassUsu().getUncryptedPassword()))
                if (!User.getCurrent().getAtivoUsu())
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            String.format("O usuário %d - %s foi inativado.\nContate o seu administrador para mais informações",
                                    User.getCurrent().getCodUsu(), User.getCurrent().getLoginUsu())).getAlert().showAndWait();
                else
                    showPrincipalFrame();
            else
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("Senha informada está incorreta")).getAlert().showAndWait();
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar buscar informações do usuário\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
        }
    }

    private void showPrincipalFrame() {
        Platform.runLater(() -> {
            try {
                Stage primaryStage = (Stage) this.btnLogin.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Principal.fxml"));
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setResizable(true);
                primaryStage.setMaximized(true);
                primaryStage.setTitle(String.format("CinemaFX ( %d - %s )", User.getCurrent().getCodUsu(), User.getCurrent().getLoginUsu()));
            } catch (IOException ex) {
                new ModelException(this.getClass(),
                        String.format("Erro ao tentar abrir tela principal\n%s\nA aplicação será finalizada", ex.getMessage()), ex).
                        getAlert().showAndWait();
                System.exit(0);
            }
        });
    }

    private void openNavHyperLink(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
