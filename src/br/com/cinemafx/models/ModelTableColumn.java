package br.com.cinemafx.models;

import br.com.cinemafx.methods.Functions;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;

import java.sql.Timestamp;

public class ModelTableColumn<S, T> extends TableColumn {

    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    private final ImageView imgFilterEmpty = ImageViewBuilder.create()
            .image(new Image("/br/com/cinemafx/views/images/Icone_Filtro_Vazio.png"))
            .style("-fx-alignment: RIGHT")
            .build();
    private final ImageView imgFilterFill = ImageViewBuilder.create()
            .image(new Image("/br/com/cinemafx/views/images/Icone_Filtro_Cheio.png"))
            .style("-fx-alignment: RIGHT")
            .build();

    public ModelTableColumn(String titulo, String key, TableColumnType tipo) {
        this.setText(titulo);
        this.setStyle("-fx-alignment: CENTER");
        if (key != null)
            createCellValueFactory(key);
        if (tipo != null)
            setCustomCellFactory(tipo);
    }

    private void createCellValueFactory(String key) {
        this.setCellValueFactory(new PropertyValueFactory<>(key));
    }

    private void setCustomCellFactory(TableColumnType tipo) {
        switch (tipo) {
            case Lógico:
                this.setCellFactory(param -> {
                    final Text textLogico = new Text();
                    TableCell<S, Boolean> cell = new TableCell<S, Boolean>() {
                        public void updateItem(Boolean item, boolean empty) {
                            if (item != null) {
                                if (item)
                                    textLogico.setText("Sim");
                                else
                                    textLogico.setText("Não");
                            } else
                                textLogico.setText(null);
                        }
                    };
                    cell.setGraphic(textLogico);
                    return cell;
                });
                break;
            case Opcoes:
                break;
            case Inteiro:
                break;
            case Texto_Grande:
                Platform.runLater(() -> this.getTableView().setFixedCellSize(100));
                this.setCellFactory(param -> {
                    JFXTextArea text = new JFXTextArea();
                    text.setEditable(false);
                    text.prefWidthProperty().bind(this.widthProperty());
                    text.setPrefHeight(100);
                    TableCell<S, String> cell = new TableCell<S, String>() {
                        public void updateItem(String item, boolean empty) {
                            text.setText(item);
                        }
                    };
                    cell.setGraphic(text);
                    return cell;
                });
                break;
            case Double:
                break;
            case Dinheiro:
                this.setCellFactory(param -> {
                    final Text textDh = new Text();
                    TableCell<S, Double> cell = new TableCell<S, Double>() {
                        public void updateItem(Double item, boolean empty) {
                            if (item != null)
                                textDh.setText(String.format("%,.2f", item));
                            else
                                textDh.setText(null);
                        }
                    };
                    cell.setGraphic(textDh);
                    return cell;
                });
                break;
            case Texto_Pequeno:
                break;
            case Data_Hora:
                this.setCellFactory(param -> {
                    final Text textDh = new Text();
                    TableCell<S, Timestamp> cell = new TableCell<S, Timestamp>() {
                        public void updateItem(Timestamp item, boolean empty) {
                            textDh.setText(Functions.getDataFormatted(Functions.dataHoraFormater, item));
                        }
                    };
                    cell.setGraphic(textDh);
                    return cell;
                });
                break;
            case Imagem:
                Platform.runLater(() -> this.getTableView().setFixedCellSize(100));
                this.setCellFactory(param -> {
                    final ImageView imageview = new ImageView();
                    imageview.fitWidthProperty().bind(this.widthProperty());
                    imageview.setFitHeight(100);
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem menuItemCopyClipboard = new MenuItem("Copiar imagem");
                    menuItemCopyClipboard.setOnAction(e -> {
                        content.putImage(imageview.getImage());
                        clipboard.setContent(content);
                    });
                    contextMenu.getItems().addAll(menuItemCopyClipboard);
                    TableCell<S, Object> cell = new TableCell<S, Object>() {
                        public void updateItem(Object item, boolean empty) {
                            Image imagem = null;
                            if (item == null)
                                imageview.setImage(null);
                            else if (item.getClass() == Image.class)
                                imagem = (Image) item;
                            else
                                imagem = Functions.getImgFromBlob((byte[]) item);
                            imageview.setImage(imagem);
                        }
                    };
                    cell.setGraphic(imageview);
                    cell.setContextMenu(contextMenu);
                    return cell;
                });
                break;
        }
    }

    public TableColumn setPercentSize(Double percent) {
        Platform.runLater(() -> this.prefWidthProperty().bind(this.getTableView().widthProperty().multiply(percent).divide(100)));
        return this;
    }

    public TableColumn setPercentSize(Integer percent) {
        Platform.runLater(() -> this.prefWidthProperty().bind(this.getTableView().widthProperty().multiply(percent).divide(100)));
        return this;
    }
}
