package br.com.cinemafx.controllers;

import br.com.cinemafx.methods.AppInfo;
import br.com.cinemafx.methods.Functions;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AutoriaDlg {

    private static Boolean estruturado = false;
    private static Dialog dialog = new Dialog();

    public static void show() {
        if (!estruturado) estrutura();
        dialog.show();
    }

    private static void estrutura() {
        estruturado = true;
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialog.setTitle("Autoria do Sistema");
        stage.getIcons().add(new Image("/br/com/cinemafx/views/images/VideoPlay.png"));
        dialog.setGraphic(new ImageView(new Image("/br/com/cinemafx/views/images/Icone_Ferramenta.png")));
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Versão do Executável: "), 0, 0);
        grid.add(new Text(AppInfo.getInfo().getVersaoExec()), 1, 0);
        grid.add(new Label("Versão do Banco de Dados: "), 0, 1);
        grid.add(new Text(AppInfo.getInfo().getVersaoBD()), 1, 1);
        grid.add(new Label("IP da Máquina: "), 0, 2);
        grid.add(new Text(AppInfo.getInfo().getIPMaquina()), 1, 2);
        grid.add(new Label("Nome da Máquina: "), 0, 3);
        grid.add(new Text(AppInfo.getInfo().getNomeMaquina()), 1, 3);
        grid.add(new Label("Data/Hora Login: "), 0, 4);
        grid.add(new Text(Functions.getDataFormatted(Functions.dataHoraFormater, AppInfo.getInfo().getDhLogin())), 1, 4);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
    }
}
