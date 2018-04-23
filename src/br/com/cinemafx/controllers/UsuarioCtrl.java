package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.DBBoss;
import br.com.cinemafx.dbcontrollers.DBObjects;
import br.com.cinemafx.methods.MaskField;
import br.com.cinemafx.models.*;
import br.com.cinemafx.views.dialogs.FormattedDialog;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UsuarioCtrl implements Initializable, CadCtrlIntface {

    private ObservableList<User> userObservableList = FXCollections.observableArrayList();
    private User cachedUser = new User();
    private ImageView imgView = ImageViewBuilder.create().image(imgGrade).fitHeight(31).fitWidth(35).build();

    @FXML
    private AnchorPane paneGrade, paneForm;
    @FXML
    private TableView<User> tbvUsers;
    @FXML
    private Button btnView, btnAtualizar, btnAdicionar, btnSalvar, btnCancelar, btnEditar, btnDuplicar, btnExcluir,
            btnPrimeiro, btnAnterior, btnProximo, btnUltimo;
    @FXML
    private Label lblMensagem;
    @FXML
    private TextField txfCodigo, txfNome, txfLogin;
    @FXML
    private PasswordField pwfSenha;
    @FXML
    private CheckBox ckbAtivo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
        appCalls();
        init();
    }

    @Override
    public void estrutura() {
        tbvUsers.getColumns().addAll(getTableColumns());
        tbvUsers.setItems(userObservableList);
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
        tbvUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> showInForm(newV));
        tbvUsers.setOnMouseClicked(e -> {
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
        MaskField.MaxCharField(txfNome, 80);
        MaskField.MaxUpperCharField(txfLogin, 25);
        MaskField.MaxCharField(pwfSenha, 25);
        txfCodigo.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV && !isAtualizando() && getFrameStatus() == FrameStatus.Status.Visualizando) { //FocusLost to Search
                tbvUsers.getSelectionModel().clearSelection();
                if (txfCodigo.getText().isEmpty()) {
                    sendMensagem(lblMensagem, false, "Informe algum código válido para pesquisar");
                    tbvUsers.getSelectionModel().clearAndSelect(0);
                    return;
                }
                long exists = userObservableList.stream()
                        .filter(user -> user.getCodUsu() == Integer.valueOf(txfCodigo.getText()))
                        .count();
                if (exists > 0)
                    tbvUsers.getSelectionModel().select(
                            userObservableList.stream()
                                    .filter(user -> user.getCodUsu() == Integer.valueOf(txfCodigo.getText())).findFirst().get());
                else {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            String.format("Usuário não encontrada para o código: %s", txfCodigo.getText())).getAlert().showAndWait();
                    tbvUsers.getSelectionModel().clearAndSelect(0);
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
        ckbAtivo.selectedProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getCachedUser().setAtivoUsu(newV));
        });
        txfNome.textProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getCachedUser().setNomeUsu(newV));
        });
        txfLogin.textProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getCachedUser().setLoginUsu(newV));
        });
        pwfSenha.textProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getCachedUser().setPassUsu(new Password(false, pwfSenha.getText())));
        });
    }

    @Override
    public void init() {
        loadTableValues();
        tbvUsers.getSelectionModel().clearAndSelect(0);
    }

    @Override
    public void loadTableValues() {
        try {
            userObservableList.clear();
            userObservableList.addAll(DBObjects.reloadUsers().stream().collect(Collectors.toList()));
            sendMensagem(lblMensagem, true, "Tabela de Usuários atualizada com sucesso!");
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar atualizar tabela de usuários\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            userObservableList.clear();
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        TableColumn[] tableColumns = new TableColumn[5];
        tableColumns[0] = new ModelTableColumn<User, Integer>("#", "codUsu", TableColumnType.Inteiro);
        tableColumns[1] = new ModelTableColumn<User, Boolean>("Ativo", "ativoUsu", TableColumnType.Lógico);
        tableColumns[2] = new ModelTableColumn<User, String>("Nome", "nomeUsu", TableColumnType.Texto_Pequeno);
        tableColumns[3] = new ModelTableColumn<User, String>("Login", "loginUsu", TableColumnType.Texto_Pequeno);
        tableColumns[4] = new ModelTableColumn<User, String>("Senha", null, TableColumnType.Texto_Pequeno);
        tableColumns[4].setCellValueFactory((Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getPassUsu().getPassword()));
        return tableColumns;
    }

    private void showInForm(User user) {
        if (getFrameStatus() != FrameStatus.Status.Visualizando) {
            int choice = FormattedDialog.getYesNoDialog(this.getClass(),
                    "Foram detectadas alterações não salvas\nDeseja salvar estas alterações antes de sair do registro?",
                    new String[]{"Salvar", "Cancelar"});
            if (choice == 0)
                btnSalvar.fire();
            if (getFrameStatus() != FrameStatus.Status.Visualizando) //Deu erro na tentativa de salvar
                return;
        }
        if (user == null) return;
        setAtualizando(true);
        setCachedUser(user);
        txfCodigo.setText(String.valueOf(user.getCodUsu()));
        ckbAtivo.setSelected(user.getAtivoUsu());
        txfLogin.setText(user.getLoginUsu());
        txfNome.setText(user.getNomeUsu());
        pwfSenha.setText(user.getPassUsu().getUncryptedPassword());
        ctrlLinhasTab(tbvUsers.getItems().indexOf(user), true);
        setAtualizando(false);
    }

    @Override
    public void ctrlAction(FrameAction frameAction) {
        switch (frameAction) {
            case ChangeView:
                paneForm.setVisible(!paneForm.isVisible());
                break;
            case Atualizar:
                setFrameStatus(FrameStatus.Status.Visualizando);
                disableButtons(false);
                loadTableValues();
                try {
                    tbvUsers.getSelectionModel().select(tbvUsers.getItems().stream()
                            .filter(user -> user.getCodUsu() == getCachedUser().getCodUsu())
                            .findFirst().get());
                } catch (NoSuchElementException ex) {
                    sendMensagem(lblMensagem, false, "Registro pré-selecionado não existe mais");
                    tbvUsers.getSelectionModel().clearAndSelect(0);
                }
                break;
            case Adicionar:
                paneForm.setVisible(true);
                showInForm(new User());
                setFrameStatus(FrameStatus.Status.Adicionando);
                txfCodigo.clear();
                disableButtons(true);
                break;
            case Salvar:
                if (getFrameStatus() == FrameStatus.Status.Adicionando) {
                    try {
                        int idInserted = DBBoss.inseriUser(this.getClass(), getCachedUser());
                        getCachedUser().setCodUsu(idInserted);
                        disableButtons(false);
                        setFrameStatus(FrameStatus.Status.Visualizando);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, String.format("Usuário %d - %s cadastrado com sucesso",
                                getCachedUser().getCodUsu(), getCachedUser().getLoginUsu()));
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar cadastrar novo usuário\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                } else if (getFrameStatus() == FrameStatus.Status.Alterando) {
                    try {
                        DBBoss.alteraUser(this.getClass(), getCachedUser());
                        disableButtons(false);
                        setFrameStatus(FrameStatus.Status.Visualizando);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, String.format("Usuário %d - %s alterado com sucesso",
                                getCachedUser().getCodUsu(), getCachedUser().getLoginUsu()));
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar alterar usuário\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                }
                break;
            case Cancelar:
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
                for (User user : tbvUsers.getSelectionModel().getSelectedItems()) {
                    salas.append(String.format("\n%d - %s", user.getCodUsu(), user.getLoginUsu()));
                }
                int resp = FormattedDialog.getYesNoDialog(this.getClass(),
                        "Deseja realmente excluir o(s) usuário(s) selecionado(s)?" + salas.toString(),
                        new String[]{"Confirmar", "Cancelar"});
                if (resp == 0)
                    try {
                        ArrayList<Integer> codUsers = new ArrayList<>();
                        tbvUsers.getSelectionModel().getSelectedItems().forEach(user -> codUsers.add(user.getCodUsu()));
                        DBBoss.excluiUser(this.getClass(), codUsers);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, "Usuário(s) excluído(s) com sucesso");
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar excluir usuário(s)\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                break;
            case Primeiro:
                ctrlLinhasTab(0, false);
                break;
            case Anterior:
                int selectedIndexA = tbvUsers.getSelectionModel().getSelectedIndex();
                if (selectedIndexA == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexA - 1, false);
                break;
            case Proximo:
                int selectedIndexB = tbvUsers.getSelectionModel().getSelectedIndex();
                if (selectedIndexB == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexB + 1, false);
                break;
            case Ultimo:
                ctrlLinhasTab(tbvUsers.getItems().size() - 1, false);
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
        if (tbvUsers.getItems().size() == 1) return;
        if (!alreadySelected) tbvUsers.getSelectionModel().clearAndSelect(newIndex);
        if (newIndex == 0) {
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex < tbvUsers.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex == tbvUsers.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
        }
    }

    public User getCachedUser() {
        return cachedUser;
    }

    public void setCachedUser(User cachedUser) {
        this.cachedUser = cachedUser;
    }
}
