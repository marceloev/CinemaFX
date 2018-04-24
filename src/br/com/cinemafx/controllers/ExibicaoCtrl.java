package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.DBBoss;
import br.com.cinemafx.dbcontrollers.DBFunctions;
import br.com.cinemafx.dbcontrollers.DBObjects;
import br.com.cinemafx.methods.Functions;
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
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExibicaoCtrl implements Initializable, CadCtrlIntface {

    ObservableList<Exibicao> exibicaoObservableList = FXCollections.observableArrayList();
    private Exibicao cachedExibicao = new Exibicao();
    private ImageView imgView = ImageViewBuilder.create().image(imgGrade).fitHeight(31).fitWidth(35).build();

    @FXML
    private AnchorPane paneGrade, paneForm;
    @FXML
    private TableView<Exibicao> tbvExibicoes;
    @FXML
    private Button btnView, btnAtualizar, btnAdicionar, btnSalvar, btnCancelar,
            btnEditar, btnDuplicar, btnExcluir, btnPrimeiro, btnAnterior, btnProximo, btnUltimo;
    @FXML
    private Label lblMensagem;
    @FXML
    private TextField txfCodigo, txfNome, txfValor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
        appCalls();
        init();
    }

    @Override
    public void estrutura() {
        tbvExibicoes.getColumns().addAll(getTableColumns());
        tbvExibicoes.setItems(exibicaoObservableList);
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
        tbvExibicoes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvExibicoes.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> showInForm(newItem));
        tbvExibicoes.setOnMouseClicked(e -> {
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
        MaskField.MaxCharField(txfNome, 25);
        MaskField.MoneyField(txfValor, 11);
        txfCodigo.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV && !isAtualizando() && getFrameStatus() == FrameStatus.Status.Visualizando) { //FocusLost to Search
                tbvExibicoes.getSelectionModel().clearSelection();
                if (txfCodigo.getText().isEmpty()) {
                    sendMensagem(lblMensagem, false, "Informe algum código válido para pesquisar");
                    tbvExibicoes.getSelectionModel().clearAndSelect(0);
                    return;
                }
                long exists = exibicaoObservableList.stream()
                        .filter(exib -> exib.getCodExibicao() == Integer.valueOf(txfCodigo.getText()))
                        .count();
                if (exists > 0)
                    tbvExibicoes.getSelectionModel().select(
                            exibicaoObservableList.stream()
                                    .filter(exib -> exib.getCodExibicao() == Integer.valueOf(txfCodigo.getText())).findFirst().get());
                else {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            String.format("Exibição não encontrada para o código: %s", txfCodigo.getText())).getAlert().showAndWait();
                    tbvExibicoes.getSelectionModel().clearAndSelect(0);
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
        txfNome.textProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getCachedExibicao().setNomeExibicao(newV));
        });
        txfValor.textProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getCachedExibicao().setVlrExibicao(Functions.getDoubleFrom(newV))); //Mais zero pra evitar Null
        });
    }

    @Override
    public void init() {
        loadTableValues();
        tbvExibicoes.getSelectionModel().clearAndSelect(0);
    }

    @Override
    public void loadTableValues() {
        try {
            exibicaoObservableList.clear();
            exibicaoObservableList.addAll(DBObjects.reloadExibicoes().stream()
                    .filter(exib -> exib.getCodExibicao() != 0).collect(Collectors.toList()));
            sendMensagem(lblMensagem, true, "Tabela de Exibições atualizada com sucesso!");
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar atualizar tabela de exibições\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            exibicaoObservableList.clear();
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        //Constrained sized table
        TableColumn[] tableColumns = new TableColumn[3];
        tableColumns[0] = new ModelTableColumn<Exibicao, Integer>("#", "codExibicao", TableColumnType.Inteiro);
        tableColumns[1] = new ModelTableColumn<Exibicao, String>("Nome Exibição", "nomeExibicao", TableColumnType.Texto_Pequeno);
        tableColumns[2] = new ModelTableColumn<Exibicao, Double>("Valor (R$)", "vlrExibicao", TableColumnType.Dinheiro);
        return tableColumns;
    }

    private void showInForm(Exibicao exibicao) {
        if (getFrameStatus() != FrameStatus.Status.Visualizando) {
            int choice = FormattedDialog.getYesNoDialog(this.getClass(),
                    "Foram detectadas alterações não salvas\nDeseja salvar estas alterações antes de sair do registro?",
                    new String[]{"Salvar", "Cancelar"});
            if (choice == 0)
                btnSalvar.fire();
            if (getFrameStatus() != FrameStatus.Status.Visualizando) //Deu erro na tentativa de salvar
                return;
        }
        if (exibicao == null) return;
        setAtualizando(true);
        setCachedExibicao(exibicao);
        txfCodigo.setText(String.valueOf(getCachedExibicao().getCodExibicao()));
        txfNome.setText(getCachedExibicao().getNomeExibicao());
        txfValor.setText(getCachedExibicao().getVlrExibicao().toString());
        ctrlLinhasTab(tbvExibicoes.getItems().indexOf(exibicao), true);
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
                    tbvExibicoes.getSelectionModel().select(tbvExibicoes.getItems().stream()
                            .filter(exib -> exib.getCodExibicao() == getCachedExibicao().getCodExibicao())
                            .findFirst().get());
                } catch (NoSuchElementException ex) {
                    sendMensagem(lblMensagem, false, "Registro pré-selecionado não existe mais");
                    tbvExibicoes.getSelectionModel().clearAndSelect(0);
                }
                break;
            case Adicionar:
                paneForm.setVisible(true);
                setFrameStatus(FrameStatus.Status.Adicionando);
                txfCodigo.clear(); //Não precisa colocar runEdits pois, o FrameStatus = Adicionando não ativa o EditMode
                txfNome.clear();
                txfValor.clear();
                disableButtons(true);
                break;
            case Salvar:
                if (getFrameStatus() == FrameStatus.Status.Adicionando) {
                    try {
                        int idInserted = DBBoss.inseriExibicao(this.getClass(), getCachedExibicao());
                        getCachedExibicao().setCodExibicao(idInserted);
                        disableButtons(false);
                        setFrameStatus(FrameStatus.Status.Visualizando);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, String.format("Exibição %d - %s cadastrada com sucesso",
                                getCachedExibicao().getCodExibicao(), getCachedExibicao().getNomeExibicao()));
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar cadastrar nova exibição\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                } else if (getFrameStatus() == FrameStatus.Status.Alterando) {
                    try {
                        DBBoss.alteraExibicao(this.getClass(), getCachedExibicao());
                        disableButtons(false);
                        setFrameStatus(FrameStatus.Status.Visualizando);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, String.format("Exibição %d - %s alterada com sucesso",
                                getCachedExibicao().getCodExibicao(), getCachedExibicao().getNomeExibicao()));
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar alterar exibição\n%s", ex.getMessage()), ex).getAlert().showAndWait();
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
                StringBuilder exibicoes = new StringBuilder();
                for (Exibicao exibicao : tbvExibicoes.getSelectionModel().getSelectedItems()) {
                    exibicoes.append(String.format("\n%d - %s", exibicao.getCodExibicao(), exibicao.getNomeExibicao()));
                }
                int resp = FormattedDialog.getYesNoDialog(this.getClass(),
                        "Deseja realmente excluir a(s) exibiçõe(s) selecionada(s)?" + exibicoes.toString(),
                        new String[]{"Confirmar", "Cancelar"});
                if (resp == 0)
                    try {
                        ArrayList<Integer> codExibicoes = new ArrayList<>();
                        tbvExibicoes.getSelectionModel().getSelectedItems().forEach(exibicao -> codExibicoes.add(exibicao.getCodExibicao()));
                        int count = DBFunctions.checkIfExistsWithoutThrows(this.getClass(), "TSESSOES",
                                new Pair<>("CODEXIB", codExibicoes.toArray()));
                        if (count > 0)
                            throw new Exception("Existem sessões cadastradas para estas exibições\n" +
                                    "Caso seja necessário, exclua as sessões destas exibições e tente novamente.");
                        DBBoss.excluiExibicao(this.getClass(), codExibicoes);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, "Exibiçõe(s) excluída(s) com sucesso");
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar excluir exibições(s)\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                break;
            case Primeiro:
                ctrlLinhasTab(0, false);
                break;
            case Anterior:
                int selectedIndexA = tbvExibicoes.getSelectionModel().getSelectedIndex();
                if (selectedIndexA == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexA - 1, false);
                break;
            case Proximo:
                int selectedIndexB = tbvExibicoes.getSelectionModel().getSelectedIndex();
                if (selectedIndexB == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexB + 1, false);
                break;
            case Ultimo:
                ctrlLinhasTab(tbvExibicoes.getItems().size() - 1, false);
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
        if (tbvExibicoes.getItems().size() == 1) return;
        if (!alreadySelected) tbvExibicoes.getSelectionModel().clearAndSelect(newIndex);
        if (newIndex == 0) {
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex < tbvExibicoes.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex == tbvExibicoes.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
        }
    }

    public Exibicao getCachedExibicao() {
        return cachedExibicao;
    }

    public void setCachedExibicao(Exibicao cachedExibicao) {
        this.cachedExibicao = cachedExibicao;
    }

}
