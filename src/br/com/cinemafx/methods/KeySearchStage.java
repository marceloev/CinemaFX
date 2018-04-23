package br.com.cinemafx.methods;

import br.com.cinemafx.dbcontrollers.Conexao;
import br.com.cinemafx.models.ModelTableColumn;
import br.com.cinemafx.models.TableColumnType;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeySearchStage<S> extends Stage {

    private String descrSearch;
    private ImageView imgSearch;
    private String query;
    private TableView<Object[]> tableView = new TableView<>();
    private List<Pair<String, TableColumnType>> colunas = new ArrayList<>();
    private List<Object> keyReturn = new ArrayList<>();

    public KeySearchStage(String descrSearch, ImageView imgSearch, String query,
                          List<Pair<String, TableColumnType>> colunas) {
        Platform.runLater(() -> initProperties(descrSearch, imgSearch, colunas, query));
    }

    private void initProperties(String descrSearch, ImageView imgSearch,
                                List<Pair<String, TableColumnType>> colunas, String query) {
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(imgSearch.getScene().getWindow());
        this.getIcons().add(new Image("/br/com/cinemafx/views/images/Icone_Pesquisa.png"));
        this.setTitle("Pesquisando: ".concat(descrSearch)); //Pesquisando : Salas
        this.setDescrSearch(descrSearch);
        this.setImgSearch(imgSearch);
        this.setQuery(query);
        this.setColunas(colunas);
        estrutura();
        appCalls();
    }

    private void estrutura() {
        StackPane pane = new StackPane();
        Scene root = new Scene(pane, 700, 600);
        this.setScene(root);
        HBox boxButton = new HBox();
        Button[] buttons = new Button[2];
        buttons[0] = new Button("Sair");
        buttons[0].setOnAction(e -> this.close()); //Não da trigger em nada
        buttons[1] = new Button("Escolher Selecionada");
        buttons[1].setOnAction(e -> {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        "Selecione uma linha para utilização").getAlert().showAndWait();
            } else {
                Object[] objSelected = tableView.getSelectionModel().getSelectedItem();
                setKeyReturn(objSelected);
                this.fireEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
        boxButton.getChildren().addAll(buttons);
        boxButton.setSpacing(8);
        boxButton.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        getTableView().prefWidthProperty().bind(pane.widthProperty().subtract(25));
        getTableView().prefHeightProperty().bind(pane.heightProperty().subtract(25));
        getTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox box = new VBox(getTableView(), boxButton);
        box.setSpacing(5);
        box.setPadding(new Insets(3, 3, 3, 3));
        pane.getChildren().add(box);
    }

    private void appCalls() {
        this.getScene().setOnKeyReleased(e -> this.close()); //Não da trigger em nada
        this.tableView.setOnMouseClicked(e -> {
            if (e.getClickCount() > 1) {
                if (tableView.getSelectionModel().getSelectedItem() == null) {
                    new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                            "Selecione uma linha para utilização").getAlert().showAndWait();
                } else {
                    Object[] objSelected = tableView.getSelectionModel().getSelectedItem();
                    setKeyReturn(objSelected);
                    this.fireEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            }
        });
        this.getImgSearch().setOnMouseClicked(e -> showSearch());
    }

    private void showSearch() {
        reloadTableValue();
        this.show();
    }

    public void reloadTableValue() {
        Conexao conex = new Conexao(this.getClass());
        getKeyReturn().clear();
        try {
            if (getColunas() == null || getColunas().isEmpty()) {
                new ModelException(this.getClass(), String.format("Estrutura de colunas não programada para pesquisa de: %s\n" +
                        "Não será possível exibir os dados, favor revise", getDescrSearch())).getAlert().showAndWait();
                return;
            }
            conex.createStatement(getQuery());
            conex.createSet();
            int countRow = conex.countRows();
            int countCol = conex.rs.getMetaData().getColumnCount();
            Object[][] DadosTabela = new Object[countRow][countCol];
            for (int rowAtual = 0; rowAtual < countRow; rowAtual++) {
                conex.rs.next();
                ArrayList<Object> ArrayColuna = new ArrayList<>();
                for (int colAtual = 1; colAtual <= countCol; colAtual++) {
                    ArrayColuna.add(conex.rs.getObject(colAtual));
                }
                DadosTabela[rowAtual] = ArrayColuna.toArray();
            }
            ObservableList<Object[]> Dados = FXCollections.observableArrayList();
            Dados.addAll(Arrays.asList(DadosTabela));
            tableView.setItems(Dados);
        } catch (Exception ex) {
            new ModelException(this.getClass(), String.format("Erro ao tentar criar tabela de pesquisa\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            tableView.setItems(null);
        } finally {
            conex.desconecta();
        }
    }

    public String getDescrSearch() {
        return descrSearch;
    }

    public void setDescrSearch(String descrSearch) {
        this.descrSearch = descrSearch;
    }

    public ImageView getImgSearch() {
        return imgSearch;
    }

    public void setImgSearch(ImageView imgSearch) {
        this.imgSearch = imgSearch;
    }

    public void setColunas(List<Pair<String, TableColumnType>> colunas) {
        this.colunas = colunas;
        for (int i = 0; i < colunas.size(); i++) {
            int finalI = i;
            TableColumn column = new ModelTableColumn<S, Object>(colunas.get(i).getKey(), null, colunas.get(i).getValue());
            column.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Object[], Object>, ObservableValue<Object>>)
                    p -> new SimpleObjectProperty<>((p.getValue()[finalI])));
            column.setStyle("-fx-alignment: CENTER; -fx-border-color: #F8F8FF;");
            tableView.getColumns().add(column);
        }
    }

    public TableView getTableView() {
        return tableView;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Pair<String, TableColumnType>> getColunas() {
        return colunas;
    }

    public List<Object> getKeyReturn() {
        return keyReturn;
    }

    public void setKeyReturn(Object[] keyReturn) {
        this.keyReturn.clear();
        if (keyReturn != null)
            this.keyReturn = new ArrayList(Arrays.asList(keyReturn));
    }
}
