package br.com.cinemafx.methods;

import javafx.application.Platform;
import javafx.scene.control.*;

public class MaskField {

    private static Boolean isAtualizando = false;

    public static void MaxCharField(TextField txt, int max) {
        txt.lengthProperty().addListener((observableValue, oldValue, newValue) -> {
            if (txt.getText().isEmpty() || isAtualizando) return;
            if (txt.getLength() > max) txt.deleteText(max, txt.getLength());
        });
    }

    public static void MaxUpperCharField(TextField txt, int max) {
        txt.caretPositionProperty().addListener((observableValue, oldValue, newValue) -> {
            if (txt.getText().isEmpty() || isAtualizando) return;
            Platform.runLater(() -> {
                isAtualizando = true;
                if (txt.getLength() > max) txt.deleteText(max, txt.getLength());
                txt.setText(txt.getText().toUpperCase());
                txt.positionCaret(txt.getLength());
                isAtualizando = false;
            });
        });
    }

    public static void CharField(TextArea Txt, int Max) {
        Txt.lengthProperty().addListener((obs, oldV, newV) -> {
            if (Txt.getLength() > Max && Max > 0) {
                Txt.deleteText(Max, Txt.getLength());
            }
        });
    }

    public static void CharField(ComboBox Cbb, int Max) {
        Cbb.cellFactoryProperty().addListener((obs, oldV, newV) -> {
            if (Cbb.getValue().toString().length() > Max && Max > 0) {
                Cbb.setValue(Cbb.getValue().toString().substring(0, Max));
            }
        });
    }

    public static void NumberField(TextField Txt, int Max) {
        Txt.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (Txt.getLength() > 0 && !Txt.getText().matches("^[0-9]{" + Txt.getLength() + "}")) {
                Txt.setText(getOnlyDigits(Txt.getText()));
            }
            if (Txt.getLength() > Max && Max > 0) {
                Txt.deleteText(Max, Txt.getLength());
            }
        });
    }

    public static void MoneyField(TextField txf, int max) { //Corrigir
        txf.setPromptText("R$0,00");
        txf.lengthProperty().addListener((obs, oldV, newV) -> {
            if (txf.getText().isEmpty() || isAtualizando) return;
            if (txf.getLength() > max
                    || (!txf.getText().matches("[0-9]{1,}[.||,]{1}[0-9]{0,2}")
                    && !txf.getText().matches("[0-9]{1,}")))
                try {
                    txf.deleteText(oldV.intValue(), newV.intValue());
                } catch (Exception ex) {
                    txf.clear();
                }
        });
    }

    private static String MoneyFormat(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.matches("[0-9]{1,}[.]{1}[0-9]{1,}"))
            value = value.replace(",", "");
        return String.format("%,.2f", value);
    }

    public static void NCMField(TextField Txt) {
        Txt.lengthProperty().addListener((observableValue, oldV, newV) -> {
            if (isAtualizando) { //Não pode ser Platform.RunLater, se não cai no textproperty
                isAtualizando = false;
                String text = getOnlyDigits(Txt.getText());
                if (text.length() > 8) text = text.substring(0, 8);
                text = text.replaceFirst("(\\d{6})(\\d)", "$1.$2");
                text = text.replaceFirst("(\\d{4})(\\d)", "$1.$2");
                Txt.setText(text);
                Platform.runLater(() -> Txt.positionCaret(Txt.getLength()));
                isAtualizando = true;
            }
        });
    }

    public static void SpnFieldCtrl(Spinner<Integer> spn, Integer min, Integer max) {
        spn.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, min));
        NumberField(spn.getEditor(), max);
        spn.focusedProperty().addListener((obs, wasF, isF) -> {
            if (wasF)
                if (spn.getEditor().getLength() == 0)
                    spn.getEditor().setText(min.toString());
            spn.getValueFactory().setValue(Integer.valueOf(spn.getEditor().getText()));
        });
    }

    public static void SpnFieldCtrl(Spinner<Double> Spn, Double Min, Double Max) {
        Spn.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Min, Max, Min));
        Spn.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches(oldV)) {
                if (newV.length() != 0 &&
                        !newV.matches("[0-9]{1,3}") &&
                        !newV.matches("[0-9]{1,3}[,]{1}") &&
                        !newV.matches("[0-9]{1,3}[.]{1}") &&
                        !newV.matches("[0-9]{1,3}[,]{1}[0-9]{1,2}") &&
                        !newV.matches("[0-9]{1,3}[.]{1}[0-9]{1,2}")) {
                    Platform.runLater(() -> {
                        int Pos = Spn.getEditor().getCaretPosition();
                        Spn.getEditor().setText(oldV);
                        Spn.getEditor().positionCaret(Pos - 1);
                    });
                }
            }
        });
        Spn.focusedProperty().addListener((obs, wasF, isF) -> {
            if (wasF) {
                if (Spn.getEditor().getLength() == 0) {
                    Spn.getEditor().setText("" + Min.toString().replace(".", ","));
                }
                Spn.getValueFactory().setValue(Double.parseDouble(Spn.getEditor().getText().replace(",", ".")));
            }
        });
    }

    public static String getOnlyDigits(String strTarget) {
        String strResult = strTarget.replaceAll("[^0-9]", "");
        return strResult;
    }
}
