package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.DBFunctions;
import br.com.cinemafx.methods.AppInfo;
import br.com.cinemafx.methods.CreateLogFile;
import br.com.cinemafx.models.ParametroType;
import br.com.cinemafx.views.dialogs.FormattedDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ResourceBundle;

public class PrincipalCtrl implements Initializable {

    String versaoExec = "1.0.0.1";

    @FXML
    private TabPane mainTabPane;
    @FXML
    private MenuButton menuButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneEstrutura();
        menuEstrutura();
        feedAppProperties();
    }

    private void paneEstrutura() {
        String telaLoad = null;
        try {
            telaLoad = "Ingressos";
            Parent rootIngressos = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Ingresso.fxml"));
            mainTabPane.getTabs().get(0).setContent(rootIngressos);
            telaLoad = "Sessões";
            Parent rootSessoes = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Sessao.fxml"));
            mainTabPane.getTabs().get(1).setContent(rootSessoes);
            telaLoad = "Filmes";
            Parent rootFilmes = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Filme.fxml"));
            mainTabPane.getTabs().get(2).setContent(rootFilmes);
            telaLoad = "Exibições";
            Parent rootExibicoes = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Exibicao.fxml"));
            mainTabPane.getTabs().get(3).setContent(rootExibicoes);
            telaLoad = "Salas";
            Parent rootSalas = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Sala.fxml"));
            mainTabPane.getTabs().get(4).setContent(rootSalas);
            telaLoad = "Usuários";
            Parent rootUsuarios = FXMLLoader.load(getClass().getResource("/br/com/cinemafx/views/fxml/Usuario.fxml"));
            mainTabPane.getTabs().get(5).setContent(rootUsuarios);
        } catch (IOException ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar abrir tela de menu: %s\n%s\nA aplicação será finalizada", telaLoad, ex.getMessage()), ex).
                    getAlert().showAndWait();
            System.exit(0);
        }
    }

    private void menuEstrutura() {
        /*Autoria > Download Log > sep > Att. Senha > Deslogar > Sair*/
        menuButton.getItems().get(0).setOnAction(e -> AutoriaDlg.show());
        menuButton.getItems().get(1).setOnAction(e -> new CreateLogFile(menuButton));
        menuButton.getItems().get(3).setOnAction(e -> {
            AltSenhaDlg altSenhaDlg = new AltSenhaDlg();
            altSenhaDlg.show();
        });
        menuButton.getItems().get(4).setOnAction(e -> {
            int resp = FormattedDialog.getYesNoDialog(this.getClass(),
                    "Deseja realmente deslogar do sistema?", new String[]{"Deslogar", "Cancelar"});
            if (resp == 0) {
                Main main = new Main();
                main.start(new Stage());
                Stage stage = (Stage) this.menuButton.getScene().getWindow();
                stage.close();
            }
        });
        menuButton.getItems().get(5).setOnAction(e ->
                menuButton.getScene().getWindow().fireEvent(
                        new WindowEvent(menuButton.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

    private void feedAppProperties() {
        Platform.runLater(() -> {
            try {
                AppInfo.getInfo().setVersaoBD(String.valueOf(DBFunctions.getUserParametro(this.getClass(),
                        "VERSAOATUALDB", ParametroType.Texto, 0)));
                AppInfo.getInfo().setVersaoExec(versaoExec);
                AppInfo.getInfo().setNomeMaquina(InetAddress.getLocalHost().getHostName());
                AppInfo.getInfo().setIPMaquina(InetAddress.getLocalHost().getHostAddress());
                AppInfo.getInfo().setDhLogin(Timestamp.from(Instant.now()));
            } catch (UnknownHostException ex) {
                new ModelException(this.getClass(),
                        "Erro ao tentar capturar informações da máquina", ex).getAlert().showAndWait();
            }
        });
    }
}
