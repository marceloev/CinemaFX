package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.DBBoss;
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
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FilmeCtrl implements Initializable, CadCtrlIntface {

    ObservableList<Filme> filmeObservableList = FXCollections.observableArrayList();
    ObservableList<String> generoObservableList = FXCollections.observableArrayList();
    private Filme filmeCached = new Filme();
    private ImageView imgView = ImageViewBuilder.create().image(imgGrade).fitHeight(31).fitWidth(35).build();

    @FXML
    private AnchorPane paneGrade, paneForm;
    @FXML
    private TableView<Filme> tbvFilmes;
    @FXML
    private Button btnView, btnAtualizar, btnAdicionar, btnSalvar, btnCancelar,
            btnEditar, btnDuplicar, btnExcluir, btnPrimeiro, btnAnterior, btnProximo, btnUltimo,
            btnLimparImg, btnDownloadImg, btnUploadImg;
    @FXML
    private Label lblMensagem;
    @FXML
    private TextField txfCodigo, txfNome, txfCusto;
    @FXML
    private Spinner<Integer> spnDuracao;
    @FXML
    private ComboBox<String> cbbGenero;
    @FXML
    private ImageView imgFilme;
    @FXML
    private TextArea txaSinopse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
        appCalls();
        init();
    }

    @Override
    public void estrutura() {
        tbvFilmes.getColumns().addAll(getTableColumns());
        tbvFilmes.setItems(filmeObservableList);
        cbbGenero.setItems(generoObservableList);
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
        tbvFilmes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbvFilmes.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> showInForm(newItem));
        tbvFilmes.setOnMouseClicked(e -> {
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
        btnLimparImg.setOnAction(e -> ctrlImageViewButtons(ImageController.Limpar));
        btnDownloadImg.setOnAction(e -> ctrlImageViewButtons(ImageController.Download));
        btnUploadImg.setOnAction(e -> ctrlImageViewButtons(ImageController.Upload));
        MaskField.NumberField(txfCodigo, 11);
        MaskField.MaxCharField(txfNome, 45);
        MaskField.MoneyField(txfCusto, 11);
        MaskField.SpnFieldCtrl(spnDuracao, 1, 999);
        MaskField.MaxCharField(cbbGenero.getEditor(), 45);
        MaskField.CharField(txaSinopse, 4000);
        txfCodigo.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV && !isAtualizando() && getFrameStatus() == FrameStatus.Status.Visualizando) { //FocusLost to Search
                tbvFilmes.getSelectionModel().clearSelection();
                if (txfCodigo.getText().isEmpty()) {
                    sendMensagem(lblMensagem, false, "Informe algum código válido para pesquisar");
                    tbvFilmes.getSelectionModel().clearAndSelect(0);
                    return;
                }
                long exists = filmeObservableList.stream()
                        .filter(filme -> filme.getCodFilme() == Integer.valueOf(txfCodigo.getText()))
                        .count();
                if (exists > 0)
                    tbvFilmes.getSelectionModel().select(
                            filmeObservableList.stream()
                                    .filter(filme -> filme.getCodFilme() == Integer.valueOf(txfCodigo.getText())).findFirst().get());
                else {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            String.format("Filme não encontrada para o código: %s", txfCodigo.getText())).getAlert().showAndWait();
                    tbvFilmes.getSelectionModel().clearAndSelect(0);
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
            notifyEdit(btnEditar, () -> getFilmeCached().setNomeFilme(newV));
        });
        txfCusto.textProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getFilmeCached().setCustoFilme(Functions.getDoubleFrom(newV))); //Mais zero pra evitar Null
        });
        spnDuracao.getValueFactory().valueProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getFilmeCached().setMinFilme(newV));
        });
        cbbGenero.valueProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            if (newV == null || newV.isEmpty())
                getFilmeCached().setGenero(DBObjects.getGeneros().get(0));
            else {
                long exists = generoObservableList.stream()
                        .filter(genero -> Functions.translate(genero).equals(Functions.translate(newV))).count();
                if (exists > 0)
                    notifyEdit(btnEditar, () -> getFilmeCached().setGenero(
                            DBObjects.getGeneros().stream()
                                    .filter(genero -> Functions.translate(genero.getNomeGenero()).equals(Functions.translate(newV))).findFirst().get()
                    ));
                else
                    notifyEdit(btnEditar, () -> getFilmeCached().setGenero(new Genero(-1, newV)));
            }
        });
        txaSinopse.textProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            notifyEdit(btnEditar, () -> getFilmeCached().setSinopse(newV));
        });
        imgFilme.imageProperty().addListener((obs, oldV, newV) -> {
            if (isAtualizando()) return;
            if (newV == null) imgFilme.setImage(Functions.noImageFilme);
            notifyEdit(btnEditar, () -> getFilmeCached().setCartazFilme(newV));
        });
    }

    @Override
    public void init() {
        loadTableValues();
        tbvFilmes.getSelectionModel().clearAndSelect(0);
    }

    @Override
    public void loadTableValues() {
        try {
            generoObservableList.clear();
            DBObjects.reloadGeneros().stream().filter(genero -> genero.getCodGenero() != 0)
                    .forEach(genero -> generoObservableList.add(genero.getNomeGenero()));
            filmeObservableList.clear();
            filmeObservableList.addAll(DBObjects.reloadFilmes().stream()
                    .filter(filme -> filme.getCodFilme() != 0).collect(Collectors.toList()));
            sendMensagem(lblMensagem, true, "Tabela de Filmes atualizada com sucesso!");
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar atualizar tabela de filmes\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            filmeObservableList.clear();
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        TableColumn[] tableColumns = new TableColumn[7];
        tableColumns[0] = new ModelTableColumn<Filme, Integer>("#", "codFilme", TableColumnType.Inteiro)
                .setPercentSize(3.5);
        tableColumns[1] = new ModelTableColumn<Filme, String>("Nome", "nomeFilme", TableColumnType.Texto_Pequeno);
        tableColumns[2] = new ModelTableColumn<Filme, Double>("Custo", "custoFilme", TableColumnType.Dinheiro);
        tableColumns[3] = new ModelTableColumn<Filme, Integer>("Duração (Min.)", "minFilme", TableColumnType.Inteiro);
        tableColumns[4] = new ModelTableColumn<Filme, String>("Gênero", "genero", TableColumnType.Texto_Pequeno);
        tableColumns[5] = new ModelTableColumn<Filme, String>("Sinopse", "sinopse", TableColumnType.Texto_Grande)
                .setPercentSize(46.5);
        tableColumns[6] = new ModelTableColumn<Filme, ImageView>("Cartaz", "cartazFilme", TableColumnType.Imagem)
                .setPercentSize(17.0);
        return tableColumns;
    }

    private void showInForm(Filme filme) {
        if (getFrameStatus() != FrameStatus.Status.Visualizando) {
            int choice = FormattedDialog.getYesNoDialog(this.getClass(),
                    "Foram detectadas alterações não salvas\nDeseja salvar estas alterações antes de sair do registro?",
                    new String[]{"Salvar", "Cancelar"});
            if (choice == 0)
                btnSalvar.fire();
            if (getFrameStatus() != FrameStatus.Status.Visualizando) //Deu erro na tentativa de salvar
                return;
        }
        if (filme == null) return;
        setAtualizando(true);
        setFilmeCached(filme);
        txfCodigo.setText(String.valueOf(filme.getCodFilme()));
        txfNome.setText(filme.getNomeFilme());
        txfCusto.setText(filme.getCustoFilme().toString());
        spnDuracao.getValueFactory().setValue(filme.getMinFilme());
        cbbGenero.setValue(filme.getGenero().getNomeGenero());
        txaSinopse.setText(filme.getSinopse());
        imgFilme.setImage(filme.getCartazFilme());
        setAtualizando(false);
        ctrlLinhasTab(tbvFilmes.getItems().indexOf(filme), true);
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
                    tbvFilmes.getSelectionModel().select(tbvFilmes.getItems().stream()
                            .filter(filme -> filme.getCodFilme() == getFilmeCached().getCodFilme())
                            .findFirst().get());
                } catch (NoSuchElementException ex) {
                    sendMensagem(lblMensagem, false, "Registro pré-selecionado não existe mais");
                    tbvFilmes.getSelectionModel().clearAndSelect(0);
                }
                break;
            case Adicionar:
                paneForm.setVisible(true);
                setFrameStatus(FrameStatus.Status.Adicionando);
                txfCodigo.clear(); //Não precisa colocar runEdits pois, o FrameStatus = Adicionando não ativa o EditMode
                txfNome.clear();
                txfCusto.clear();
                spnDuracao.getValueFactory().setValue(1);
                cbbGenero.setValue("");
                txaSinopse.clear();
                imgFilme.setImage(Functions.noImageFilme);
                disableButtons(true);
                break;
            case Salvar:
                if (getFrameStatus() == FrameStatus.Status.Adicionando) {
                    try {
                        int idInserted = DBBoss.inseriFilme(this.getClass(), getFilmeCached());
                        getFilmeCached().setCodFilme(idInserted);
                        disableButtons(false);
                        setFrameStatus(FrameStatus.Status.Visualizando);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, String.format("Filme %d - %s cadastrado com sucesso",
                                getFilmeCached().getCodFilme(), getFilmeCached().getNomeFilme()));
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar cadastrar novo filme\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                } else if (getFrameStatus() == FrameStatus.Status.Alterando) {
                    try {
                        DBBoss.alteraFilme(this.getClass(), getFilmeCached());
                        disableButtons(false);
                        setFrameStatus(FrameStatus.Status.Visualizando);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, String.format("Filme %d - %s alterada com sucesso",
                                getFilmeCached().getCodFilme(), getFilmeCached().getNomeFilme()));
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar alterar filme\n%s", ex.getMessage()), ex).getAlert().showAndWait();
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
                StringBuilder filmes = new StringBuilder();
                for (Filme filme : tbvFilmes.getSelectionModel().getSelectedItems()) {
                    filmes.append(String.format("\n%d - %s", filme.getCodFilme(), filme.getNomeFilme()));
                }
                int resp = FormattedDialog.getYesNoDialog(this.getClass(),
                        "Deseja realmente excluir o(s) filme(s) selecionado(s)?" + filmes.toString(),
                        new String[]{"Confirmar", "Cancelar"});
                if (resp == 0)
                    try {
                        ArrayList<Integer> codFilmes = new ArrayList<>();
                        tbvFilmes.getSelectionModel().getSelectedItems().forEach(filme -> codFilmes.add(filme.getCodFilme()));
                        DBBoss.excluiFilme(this.getClass(), codFilmes);
                        ctrlAction(FrameAction.Atualizar);
                        sendMensagem(lblMensagem, true, "Filme(s) excluída(s) com sucesso");
                    } catch (Exception ex) {
                        new ModelException(this.getClass(),
                                String.format("Erro ao tentar excluir filme(s)\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                    }
                break;
            case Primeiro:
                ctrlLinhasTab(0, false);
                break;
            case Anterior:
                int selectedIndexA = tbvFilmes.getSelectionModel().getSelectedIndex();
                if (selectedIndexA == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexA - 1, false);
                break;
            case Proximo:
                int selectedIndexB = tbvFilmes.getSelectionModel().getSelectedIndex();
                if (selectedIndexB == -1) ctrlLinhasTab(0, false);
                else ctrlLinhasTab(selectedIndexB + 1, false);
                break;
            case Ultimo:
                ctrlLinhasTab(tbvFilmes.getItems().size() - 1, false);
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
        if (tbvFilmes.getItems().size() == 1) return;
        if (!alreadySelected) tbvFilmes.getSelectionModel().clearAndSelect(newIndex);
        if (newIndex == 0) {
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex < tbvFilmes.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
            btnProximo.setDisable(false);
            btnUltimo.setDisable(false);
        } else if (newIndex == tbvFilmes.getItems().size() - 1) {
            btnPrimeiro.setDisable(false);
            btnAnterior.setDisable(false);
        }
    }

    private void ctrlImageViewButtons(ImageController imageController) {
        switch (imageController) {
            case Limpar:
                imgFilme.setImage(Functions.noImageFilme);
                break;
            case Download:
                try {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle(String.format("Download de cartaz do Filme: %d - %s",
                            getFilmeCached().getCodFilme(), getFilmeCached().getNomeFilme()));
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivo de Imagem",
                            "*.jpg", "*.png", "*.jpeg");
                    fileChooser.getExtensionFilters().add(extFilter);
                    File file = fileChooser.showSaveDialog(btnDownloadImg.getScene().getWindow());
                    if (file != null) {
                        ImageIO.write(SwingFXUtils.fromFXImage(imgFilme.getImage(), null), "png", file);
                        sendMensagem(lblMensagem, true, "Download de cartaz concluído com sucesso");
                    } else
                        sendMensagem(lblMensagem, false, "Operação cancelada pelo usuário");
                } catch (IOException ex) {
                    new ModelException(this.getClass(),
                            String.format("Erro ao tentar efetuar download de cartaz\n%s", ex.getMessage()), ex).getAlert().showAndWait();
                }
                break;
            case Upload:
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle(String.format("Upload de cartaz do Filme: %d - %s",
                        getFilmeCached().getCodFilme(), getFilmeCached().getNomeFilme()));
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivo de Imagem",
                        "*.jpg", "*.png", "*.jpeg");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(btnUploadImg.getScene().getWindow());
                if (file != null) {
                    imgFilme.setImage(new Image(file.toURI().toString()));
                    sendMensagem(lblMensagem, true, "Upload de cartaz concluído com sucesso");
                } else
                    sendMensagem(lblMensagem, false, "Operação cancelada pelo usuário");
                break;
        }
    }

    public Filme getFilmeCached() {
        return filmeCached;
    }

    public void setFilmeCached(Filme filmeCached) {
        this.filmeCached = filmeCached;
    }
}
