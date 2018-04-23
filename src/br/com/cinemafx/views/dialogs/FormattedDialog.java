package br.com.cinemafx.views.dialogs;

import br.com.cinemafx.methods.log.GravaLog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FormattedDialog {

    public static int getYesNoDialog(Class invocador, String mensagem, String[] buttons) {
        List<String> arrayButtons = Arrays.asList(buttons);
        if (arrayButtons.size() != 2) {
            new ModelException(invocador, "Erro ao tentar criar diálogo formatado\n" +
                    "Valores passados para botões lógico diferente de 2").getAlert().showAndWait();
            return -1;
        }
        Alert alerta = new ModelDialog(invocador, Alert.AlertType.CONFIRMATION, mensagem).getAlert();
        ButtonType[] btnTypes = new ButtonType[2];
        btnTypes[0] = new ButtonType(arrayButtons.get(0));
        btnTypes[1] = new ButtonType(arrayButtons.get(1));
        alerta.getButtonTypes().clear();
        alerta.getButtonTypes().addAll(btnTypes);
        Optional<ButtonType> getChoosed = alerta.showAndWait();
        if (getChoosed.get() == btnTypes[0]) {
            GravaLog.gravaInfo(invocador, "Opção selecionada: " + arrayButtons.get(0));
            return 0;
        } else {
            GravaLog.gravaInfo(invocador, "Opção selecionada: " + arrayButtons.get(1));
            return 1;
        }
    }

    public static Pair<Boolean, Integer> getYesNoDialog(Class invocador, String mensagem, String mensagemCheck, String[] buttons) {
        List<String> arrayButtons = Arrays.asList(buttons);
        if (arrayButtons.size() != 2) {
            new ModelException(invocador, "Erro ao tentar criar diálogo formatado\n" +
                    "Valores passados para botões lógico diferente de 2").getAlert().showAndWait();
            return null;
        }
        BooleanProperty selected = new SimpleBooleanProperty(false);
        Alert alerta = new ModelDialog(invocador, Alert.AlertType.CONFIRMATION, mensagem).getAlert();
        Node graphic = alerta.getDialogPane().getGraphic();
        alerta.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                CheckBox optOut = new CheckBox(mensagemCheck);
                optOut.setOnAction(e -> selected.setValue(!selected.getValue()));
                return optOut;
            }
        });
        ButtonType[] btnTypes = new ButtonType[2];
        btnTypes[0] = new ButtonType(arrayButtons.get(0));
        btnTypes[1] = new ButtonType(arrayButtons.get(1));
        alerta.getButtonTypes().clear();
        alerta.getButtonTypes().addAll(btnTypes);
        alerta.getDialogPane().setExpandableContent(new Group());
        alerta.getDialogPane().setExpanded(true);
        alerta.setGraphic(graphic);
        alerta.setContentText(mensagem);
        Optional<ButtonType> getChoosed = alerta.showAndWait();
        if (getChoosed.get() == btnTypes[0]) {
            GravaLog.gravaInfo(invocador, "Opção selecionada: " + arrayButtons.get(0));
            return new Pair<>(selected.getValue(), 0);
        } else {
            GravaLog.gravaInfo(invocador, "Opção selecionada: " + arrayButtons.get(1));
            return new Pair<>(selected.getValue(), 1);
        }
    }
}
