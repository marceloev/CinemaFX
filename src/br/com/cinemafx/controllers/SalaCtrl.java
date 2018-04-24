package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.DBBoss;
import br.com.cinemafx.dbcontrollers.DBFunctions;
import br.com.cinemafx.dbcontrollers.DBObjects;
import br.com.cinemafx.dbcontrollers.OpRelacional;
import br.com.cinemafx.methods.MaskField;
import br.com.cinemafx.models.*;
import br.com.cinemafx.views.dialogs.FormattedDialog;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SalaCtrl implements Initializable, CadCtrlIntface {

    ObservableList<Sala> salaObservableList = FXCollections.observableArrayList();
    private Sala cachedSala = new Sala();
    private ImageView imgView = ImageViewBuilder.create().image(imgGrade).fitHeight(31).fitWidth(35).build();

    @FXML
    private AnchorPane paneGrade, paneForm;
    @FXML
    private TableView<Sala> tbvSalas;
    @FXML
    private Button btnView, btnAtualizar, btnAdicionar, btnSalvar, btnCancelar,
            btnEditar, btnDuplicar, btnExcluir, btnPrimeiro, btnAnterior, btnProximo, btnUltimo;
    @FXML
    private Label lblMensagem;
    @FXML
    private TextField txfCodigo, txfReferencia;
    @FXML
    private Spinner<Integer> spnCapacidade;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
        appCalls();
        init();
    }


    @Override
    public void estrutura() {
        tbvSalas.getColumns().addAll(getTableColumns());
        tbvSalas.setItems(salaObservableList);
    }

    @Override
    public void appCalls() {
        paneForm.setVisible(false);
        paneGrade.setVisible(true);
        paneForm.visibleProperty().addListener((obs, oldV, newV) -> {
            paneGrade.setVisible(oldV);
            if (newV) {
                imgView.setImage(imgForm);
                btnView.getTooltip().setText("Modo Formulário");
            } else {
                imgView.setImage(imgGrade);
                btnView.getTooltip().setText("Modo Grade");
            }
        });
        tbvSalas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvSalas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> showInForm(newV));
        tbvSalas.setOnMouseClicked(e -> {
            if (e.getClickCount() > 1) paneForm.setVisible(true);
        });
        btnView.setGraphic(imgView);
        btnView.setOnAction(e -> ctrlAction(FrameAction.ChangeView));
        btnAtualizar.setOnAction(e -> ctrlAction(FrameAction.Atualizar));
        btnAdicionar.setOnAction(e -> ctrlAction(FrameAction.Adicionar));
        btnSalvar.setOnAction(e -> ctrlAction(FrameAction.Salvar));
        btnCancelar.setOnAction(e -> ctrlAction(FrameAction.Cancelar));
        btnEditar.setOnAction(e -> ctrlAction(FrameAction.Editar));
        btnDuplicar.setOnAction(e -> ctrlAction(FrameAction.Duplicar));
        btnExcluir.setOnAction(e -> ctrlAction(FrameAction.Excluir));
        btnPrimeiro.setOnAction(e -> ctrlAction(FrameAction.Primeiro));
        btnAnterior.setOnAction(e -> ctrlAction(FrameAction.Anterior));
        btnProximo.setOnAction(e -> ctrlAction(FrameAction.Proximo));
        btnUltimo.setOnAction(e -> ctrlAction(FrameAction.Ultimo));
        MaskField.NumberField(txfCodigo, 11);
        MaskField.MaxCharField(txfReferencia, 25);
        MaskField.SpnFieldCtrl(spnCapacidade, 1, 999999);
        txfCodigo.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV && !isAtualizando() && getFrameStatus() == FrameStatus.Status.Visualizando) { //FocusLost to Search
                tbvSalas.getSelectionModel().clearSelection();
                if (txfCodigo.getText().isEmpty()) {
                    sendMensagem(lblMensagem, false, "Informe algum código válido para pesquisar");
                    tbvSalas.getSelectionModel().clearAndSelect(0);
                    return;
                }
                long exists = salaObservableList.stream()
                        .filter(sala -> sala.getCodSala() == Integer.valueOf(txfCodigo.getText()))
                        .count();
                if (exists > 0)
                    tbvSalas.getSelectionModel().select(
                            salaObservableList.stream()
                                    .filter(sala -> sala.getCodSala() == Integer.valueOf(txfCodigo.getText())).findFirst().get());
                else {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            String.format("Sala não encontrada para o código: %s", txfCodigo.getText())).getAlert().showAndWait();
                    tbvSalas.getSelectionModel().clearAndSelect(0);
                }
            }
        });
        txfCodigo.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.isEmpty() || isAtualizando() || getFrameStatus() != FrameStatus.Status.Adicionando) return;
            new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                    "Na criação de registros, é bloqueado a digitação dos códigos\n" +
                            "Essa trava tem a funcionalidade de evitar duplicidade.").getAlert().showAndWait();
            Platform.runLater(() -> txfCodigo.clear());
        });
        txfReferencia.textProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getCachedSala().setRefSala(newV));
        });
        spnCapacidade.getValueFactory().valueProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getCachedSala().setCapacidade(newV));
        });
    }

    @Override
    public void init() {
        loadTableValues();
        tbvSalas.getSelectionModel().select(0);
    }

    @Override
    public void loadTableValues() {
        try {
            salaObservableList.clear();
            salaObservableList.addAll(DBObjects.reloadSala().stream().filter(sala -> sala.getCodSala() != 0).collect(Collectors.toList()));
            sendMensagem(lblMensagem, true, "Tabela de Salas atualizada com sucesso!");
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar atualizar tabela de salas\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            salaObservableList.clear();
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        //Constrained sized table
        TableColumn[] tableColumns = new TableColumn[3];
        tableColumns[0] = new ModelTableColumn<Sala, Integer>("#", "codSala", TableColumnType.Inteiro);
        tableColumns[1] = new ModelTableColumn<Sala, String>("Referência", "refSala", TableColumnType.Texto_Pequeno);
        tableColumns[2] = new ModelTableColumn<Sala, Integer>("Capacidade", "capacidade", TableColumnType.Inteiro);
        return tableColumns;
    }

    private void showInForm(Sala sala) {
        if (getFrameStatus() != FrameStatus.Status.Visualizando) {
            int choice = FormattedDialog.getYesNoDialog(this.getClass(),
                    "Foram detectadas alterações não salvas\nDeseja salvar estas alterações antes de sair do registro?",
                    new String[]{"Salvar", "Cancelar"});
            if (choice == 0)
                btnSalvar.fire();
            if (getFrameStatus() != FrameStatus.Status.Visualizando) //Deu erro na tentativa de salvar
                return;
        }
        if (sala == null) return;
        setAtualizando(true);
        setCachedSala(sala);
        txfCodigo.setText(String.valueOf(getCachedSala().getCodSala()));
        txfReferencia.setText(getCachedSala().getRefSala());
        spnCapacidade.getValueFactory().setValue(getCachedSala().getCapacidade());
        ctrlLinhasTab(tbvSalas.getItems().indexOf(sala), true);
        setAtualizando(false);
    }

    @Override
    public void ctrlAction(FrameAction frameAction) {
        switch (frameAction) {
            case ChangeView:
                paneForm.setVisible(paneGrade.isVisible());
                break;
            case Atualizar:
                setFrameStatus(FrameStatus.Status.Visualizando);
                disableButtons(false);
                loadTableValues();
                try {
                    tbvSalas.getSelectionModel().select(tbvSalas.getItems().stream()
                            .filter(sala -> sala.getCodSala() == getCachedSala().getCodSala())
                            .findFirst().get());
                } catch (NoSuchElementException ex) {
                    sendMensagem(lblMensagem, false, "Registro pré-selecionado não existe mais");
                    tbvSalas.getSelectionModel().clearAndSelect(0);
                }
                break;
            case Adicionar:
                paneForm.setVisible(true);
                setFrameStatus(FrameStatus.Status.Adicionando);
                txfCodigo.clear(); //Não precisa colocar runEdits pois, o FrameStatus = Adicionando não ativa o EditMode
                txfReferencia.clear();
                spnCapacidade.getValueFactory().setValue(1);
                disableButtons(true);
                break;
            case Salvar:
                if (getFrameStatus() == FrameStatus.Status.Adicionando) {
                    try {
                        int idInserted = DBBoss.inseriSala(this.getClass(), getCachedSala());
                        getCachedSala().setCodSala(idInserted);
                        disableButtons(false);
                        setFrameStatus(FrameStatus.Status.Visualizando);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, String.format("Sala %d - %s cadastrada com sucesso",
                                getCachedSala().getCodSala(), getCachedSala().getRefSala()));
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar cadastrar nova sala\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                } else if (getFrameStatus() == FrameStatus.Status.Alterando) {
                    try {
                        DBBoss.alteraSala(this.getClass(), getCachedSala());
                        disableButtons(false);
                        setFrameStatus(FrameStatus.Status.Visualizando);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, String.format("Sala %d - %s alterada com sucesso",
                                getCachedSala().getCodSala(), getCachedSala().getRefSala()));
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar alterar sala\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                }
                break;
            case Cancelar:
                //Até agora, não descobri uma forma de "cancelar" sem ter que re-buscar no banco, terei que estudar
                ctrlAction(FrameAction.Atualizar);
                sendMensagem(lblMensagem, false, "Operação cancelada pelo usuário");
                break;
            case Editar:
                setFrameStatus(FrameStatus.Status.Alterando);
                disableButtons(true);
                break;
            case Duplicar:
                paneForm.setVisible(true);
                setFrameStatus(FrameStatus.Status.Adicionando);
                txfCodigo.clear(); //Não precisa colocar runEdits pois, o FrameStatus = Adicionando não ativa o EditMode
                disableButtons(true);
                break;
            case Excluir:
                StringBuilder salas = new StringBuilder();
                for (Sala sala : tbvSalas.getSelectionModel().getSelectedItems()) {
                    salas.append(String.format("\n%d - %s", sala.getCodSala(), sala.getRefSala()));
                }
                int resp = FormattedDialog.getYesNoDialog(this.getClass(),
                        "Deseja realmente excluir a(s) sala(s) selecionada(s)?" + salas.toString(),
                        new String[]{"Confirmar", "Cancelar"});
                if (resp == 0)
                    try {
                        ArrayList<Integer> codSalas = new ArrayList<>();
                        tbvSalas.getSelectionModel().getSelectedItems().forEach(sala -> codSalas.add(sala.getCodSala()));
                        if (DBFunctions.checkIfExists(this.getClass(), "TSESSOES",
                                new Pair<>(OpRelacional.EQUALS, new Pair("CODSALA", Arrays.asList(codSalas)))) > 0)
                            throw new Exception("Existem sessões cadastradas para estas salas\n" +
                                    "Caso seja necessário, exclua as sessões destas salas e tente novamente.");
                        DBBoss.excluiSala(this.getClass(), codSalas);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, "Sala(s) excluída(s) com sucesso");
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar excluir sala(s)\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                break;
            case Primeiro:
                ctrlLinhasTab(0, false);
                break;
            case Anterior:
                int selectedIndexA = tbvSalas.getSelectionModel().getSelectedIndex();
                if (selectedIndexA == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexA - 1, false);
                break;
            case Proximo:
                int selectedIndexB = tbvSalas.getSelectionModel().getSelectedIndex();
                if (selectedIndexB == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexB + 1, false);
                break;
            case Ultimo:
                ctrlLinhasTab(tbvSalas.getItems().size() - 1, false);
                break;
        }
    }

    private void disableButtons(Boolean disable) {
        if (disable) {
            btnAtualizar.setDisable(true);
            btnAdicionar.setDisable(true);
            btnSalvar.setDisable(false);
            btnCancelar.setDisable(false);
            btnEditar.setDisable(true);
            btnDuplicar.setDisable(true);
            btnExcluir.setDisable(true);
        } else {
            btnAtualizar.setDisable(false);
            btnAdicionar.setDisable(false);
            btnSalvar.setDisable(true);
            btnCancelar.setDisable(true);
            btnEditar.setDisable(false);
            btnDuplicar.setDisable(false);
            btnExcluir.setDisable(false);
        }
    }

    private void ctrlLinhasTab(int newIndex, boolean alreadySelected) {
        btnPrimeiro.setDisable(true);
        btnAnterior.setDisable(true);
        btnProximo.setDisable(true);
        btnUltimo.setDisable(true);
        if (tbvSalas.getItems().size() == 1) return;
        if (!alreadySelected) tbvSalas.getSelectionModel().clearAndSelect(newIndex);
        if (newIndex == 0) {
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex < tbvSalas.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex == tbvSalas.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
        }
    }

    public Sala getCachedSala() {
        return cachedSala;
    }

    public void setCachedSala(Sala cachedSala) {
        this.cachedSala = cachedSala;
    }
}
