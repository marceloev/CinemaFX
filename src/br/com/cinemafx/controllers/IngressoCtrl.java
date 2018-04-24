package br.com.cinemafx.controllers;

import br.com.cinemafx.dbcontrollers.Conexao;
import br.com.cinemafx.dbcontrollers.DBObjects;
import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.methods.KeySearchStage;
import br.com.cinemafx.methods.MaskField;
import br.com.cinemafx.methods.charts.VendasPorDia;
import br.com.cinemafx.methods.charts.VendasPorExib;
import br.com.cinemafx.methods.charts.VendasPorFilme;
import br.com.cinemafx.methods.charts.VendasPorSala;
import br.com.cinemafx.methods.log.GravaLog;
import br.com.cinemafx.models.*;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static br.com.cinemafx.methods.Functions.Nvl;

public class IngressoCtrl implements Initializable {

    private ObservableList<Vendas> vendasObservableList = FXCollections.observableArrayList();
    ObservableList<PieChart.Data> pieChartDataSala = FXCollections.observableArrayList();
    ObservableList<PieChart.Data> pieChartDataExib = FXCollections.observableArrayList();
    ObservableList<Filme> filmeFiltroList = FXCollections.observableArrayList();

    @FXML
    private StackedBarChart<String, Double> sbcVendasFilme;
    @FXML
    private AreaChart<String, Double> sacVendasDia;
    @FXML
    private PieChart picVendasSala, picVendasExibicao;
    @FXML
    private Button btnAtualizar, btnAddFilme, btnExcFilme;
    @FXML
    private TextField txfCodSessao, txfNomeSessao, txfCodSala, txfNomeSala,
            txfCodExib, txfNomeExib, txfCodFilme, txfNomeFilme;
    @FXML
    private ImageView imgBuscaSessao, imgBuscaSala, imgBuscaExib, imgBuscaFilme;
    @FXML
    private DatePicker dtpInicial, dtpFinal;
    @FXML
    private ListView<Filme> listFilmes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
        appCalls();
        init();
    }

    private void estrutura() {
        MaskField.NumberField(txfCodSessao, 11);
        MaskField.NumberField(txfCodSala, 11);
        MaskField.NumberField(txfCodExib, 11);
        MaskField.NumberField(txfCodFilme, 11);
        dtpInicial.setValue(LocalDate.now().minusDays(30));
        dtpFinal.setValue(LocalDate.now().plusDays(1));
        listFilmes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listFilmes.setItems(filmeFiltroList);
    }

    private void appCalls() {
        btnAtualizar.setOnAction(e -> loadTableValues());
        dtpInicial.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                dtpInicial.setValue(oldV);
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("É obrigatório informar data inicial para filtro")).getAlert().showAndWait();
            } else if (newV.isAfter(dtpFinal.getValue())) {
                dtpInicial.setValue(oldV);
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("Data inicial não pode ser posterior a data final")).getAlert().showAndWait();
            }
        });
        dtpFinal.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                dtpFinal.setValue(oldV);
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("É obrigatório informar data final para filtro")).getAlert().showAndWait();
            } else if (newV.isBefore(dtpInicial.getValue())) {
                dtpFinal.setValue(oldV);
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        String.format("Data final não pode ser anterior a data inicial")).getAlert().showAndWait();
            }
        });
        btnAddFilme.setOnAction(e -> {
            if (txfCodFilme.getText().isEmpty())
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        "Selecione algum filme para filtrar").getAlert().showAndWait();
            else
                addLinhasFilme(Integer.valueOf(txfCodFilme.getText()));
        });
        btnExcFilme.setOnAction(e -> {
            if (listFilmes.getSelectionModel().getSelectedIndices().isEmpty())
                new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                        "Selecione alguma linha para remover").getAlert().showAndWait();
            else
                excLinhasFilme(listFilmes.getSelectionModel().getSelectedItems());
        });
        /*Cria keySearch de Sessão*/
        Platform.runLater(() -> {
            txfCodSessao.focusedProperty().addListener((obs, oldV, newV) -> {
                if (oldV && Nvl(txfCodSessao.getText()).isEmpty()) {
                    txfNomeSessao.clear();
                } else if (oldV && !Nvl(txfCodSessao.getText()).isEmpty()) {
                    int cod = Integer.valueOf(txfCodSessao.getText());
                    if (DBObjects.sessaoContains(cod)) {
                        Sessao sessao = DBObjects.getSessaoByCod(this.getClass(), cod);
                        txfNomeSessao.setText(String.valueOf(sessao.getFilme().getCodFilme()).concat(" - ")
                                .concat(sessao.getFilme().getNomeFilme()));
                    } else {
                        new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                                String.format("Sessão não encontrada para código: %s", txfCodSessao.getText())).getAlert().showAndWait();
                        txfCodSessao.clear();
                        txfNomeSessao.clear();
                    }
                }
            });
            KeySearchStage<Sessao> sessaoKeySearchStage = new KeySearchStage("Sessões", imgBuscaSessao,
                    "SELECT CODSESSAO, DATAINI, DATAFIM, CONCAT(CODSALA, ' - ', REFSALA), CONCAT(CODFILME,' - ', NOMEFILME)\n" +
                            "FROM VSESSOES\n" +
                            "ORDER BY 1",
                    Arrays.asList(new Pair<>("Cód. Sessão", TableColumnType.Inteiro),
                            new Pair<>("Data/Hora Inicial", TableColumnType.Data_Hora),
                            new Pair<>("Data/Hora Final", TableColumnType.Data_Hora),
                            new Pair<>("Sala", TableColumnType.Texto_Pequeno),
                            new Pair<>("Filme", TableColumnType.Texto_Pequeno)));
            sessaoKeySearchStage.setOnCloseRequest(e -> {
                List<Object> retorno = sessaoKeySearchStage.getKeyReturn();
                if (!retorno.isEmpty()) {
                    txfCodSessao.setText(retorno.get(0).toString());
                    txfNomeSessao.setText(retorno.get(4).toString());
                }
            });
        });
        /*Cria keySearch de Sala*/
        Platform.runLater(() -> {
            txfCodSala.focusedProperty().addListener((obs, oldV, newV) -> {
                if (oldV && Nvl(txfCodSala.getText()).isEmpty()) {
                    txfNomeSala.clear();
                } else if (oldV && !Nvl(txfCodSala.getText()).isEmpty()) {
                    int cod = Integer.valueOf(txfCodSala.getText());
                    if (DBObjects.salaContains(cod)) {
                        txfNomeSala.setText(DBObjects.getSalaByCod(this.getClass(), cod).getRefSala());
                    } else {
                        new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                                String.format("Sala não encontrada para código: %s", txfCodSala.getText())).getAlert().showAndWait();
                        txfCodSala.clear();
                        txfNomeSala.clear();
                    }
                }
            });
            KeySearchStage<Sala> salaKeySearchStage = new KeySearchStage("Salas", imgBuscaSala,
                    "SELECT CODSALA, REFSALA, CAPACIDADE FROM TSALAS",
                    Arrays.asList(new Pair<>("Cód. Sala", TableColumnType.Inteiro),
                            new Pair<>("Referência", TableColumnType.Texto_Pequeno),
                            new Pair<>("Capacidade", TableColumnType.Inteiro)));
            salaKeySearchStage.setOnCloseRequest(e -> {
                List<Object> retorno = salaKeySearchStage.getKeyReturn();
                if (!retorno.isEmpty()) {
                    txfCodSala.setText(retorno.get(0).toString());
                    txfNomeSala.setText(retorno.get(1).toString());
                }
            });
        });
        /*Cria keySearch de Exibição*/
        Platform.runLater(() -> {
            txfCodExib.focusedProperty().addListener((obs, oldV, newV) -> {
                if (oldV && Nvl(txfCodExib.getText()).isEmpty()) {
                    txfNomeExib.clear();
                } else if (oldV && !Nvl(txfCodExib.getText()).isEmpty()) {
                    int cod = Integer.valueOf(txfCodExib.getText());
                    if (DBObjects.exibContains(cod)) {
                        txfNomeExib.setText(DBObjects.getExibicaoByCod(this.getClass(), cod).getNomeExibicao());
                    } else {
                        new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                                String.format("Exibição não encontrada para código: %s", txfCodExib.getText())).getAlert().showAndWait();
                        txfCodExib.clear();
                        txfNomeExib.clear();
                    }
                }
            });
            KeySearchStage<Exibicao> exibicaoKeySearchStage = new KeySearchStage("Exibições", imgBuscaExib,
                    "SELECT CODEXIB, NOMEEXIB, VLREXIB FROM TEXIBS",
                    Arrays.asList(new Pair<>("Cód. Exibição", TableColumnType.Inteiro),
                            new Pair<>("Nome Exibição", TableColumnType.Texto_Pequeno),
                            new Pair<>("Vlr. Exibição", TableColumnType.Dinheiro)));
            exibicaoKeySearchStage.setOnCloseRequest(e -> {
                List<Object> retorno = exibicaoKeySearchStage.getKeyReturn();
                if (!retorno.isEmpty()) {
                    txfCodExib.setText(retorno.get(0).toString());
                    txfNomeExib.setText(retorno.get(1).toString());
                }
            });
        });
        /*Cria keySearch de Filme*/
        Platform.runLater(() -> {
            txfCodFilme.focusedProperty().addListener((obs, oldV, newV) -> {
                if (oldV && Nvl(txfCodFilme.getText()).isEmpty()) {
                    txfNomeFilme.clear();
                } else if (oldV && !Nvl(txfCodFilme.getText()).isEmpty()) {
                    int cod = Integer.valueOf(txfCodFilme.getText());
                    if (DBObjects.filmeContains(cod)) {
                        txfNomeFilme.setText(DBObjects.getFilmeByCod(this.getClass(), cod).getNomeFilme());
                    } else {
                        new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                                String.format("Filme não encontrada para código: %s", txfCodFilme.getText())).getAlert().showAndWait();
                        txfCodFilme.clear();
                        txfNomeFilme.clear();
                    }
                }
            });
            KeySearchStage<Filme> filmeKeySearchStage = new KeySearchStage("Filmes", imgBuscaFilme,
                    "SELECT CODFILME, NOMEFILME, MINFILME, SINOPSE, IMAGEM FROM TFILMES",
                    Arrays.asList(new Pair<>("Cód. Filme", TableColumnType.Inteiro),
                            new Pair<>("Nome", TableColumnType.Texto_Pequeno),
                            new Pair<>("Duração", TableColumnType.Inteiro),
                            new Pair<>("Sinopse", TableColumnType.Texto_Grande),
                            new Pair<>("Cartaz", TableColumnType.Imagem)));
            filmeKeySearchStage.setOnCloseRequest(e -> {
                List<Object> retorno = filmeKeySearchStage.getKeyReturn();
                if (!retorno.isEmpty()) {
                    txfCodFilme.setText(retorno.get(0).toString());
                    txfNomeFilme.setText(retorno.get(1).toString());
                }
            });
        });
    }

    private void init() {
        picVendasSala.setData(pieChartDataSala);
        picVendasExibicao.setData(pieChartDataExib);
        loadTableValues();
    }

    private void applyFilters() {
        GravaLog.gravaInfo(this.getClass(), "O usuário solicitou a atualização dos gráficos.");
        /*StackBarChart - Vendas por Filme*/
        Platform.runLater(() -> {
            sbcVendasFilme.getData().clear();
            final XYChart.Series<String, Double> linhaFilmeInteira = new XYChart.Series<>();
            linhaFilmeInteira.setName("Inteira");
            final XYChart.Series<String, Double> linhaFilmeMeia = new XYChart.Series<>();
            linhaFilmeMeia.setName("Meia");
            /*Vendas por filme*/
            ArrayList<Integer> codFilmes = new ArrayList<>();
            vendasObservableList.stream().sorted(Comparator.comparing(Vendas::getCodFilme)).forEach(vendas -> {
                if (!codFilmes.contains(vendas.getCodFilme())) codFilmes.add(vendas.getCodFilme());
            });
            for (Integer codFilme : codFilmes) {
                String nomeFilme = DBObjects.getFilmeByCod(this.getClass(), codFilme).getNomeFilme();
                if (nomeFilme.length() > 20) nomeFilme = nomeFilme.substring(0, 17).concat("...");
                VendasPorFilme vendasPorFilme = new VendasPorFilme(codFilme, nomeFilme);
                vendasObservableList.stream().filter(vendas -> vendas.getCodFilme() == codFilme).forEach(vendas ->
                        vendasPorFilme.setQtdVlr(vendas.isIntegral(), vendas.getQtdIngresso(), vendas.getVlrTotal()));
                String textoChart = String.valueOf(codFilme).concat(" - ").concat(nomeFilme)
                        .concat(String.format("\nVlr. Total: %,.2f", vendasPorFilme.getVlrIntegral() + vendasPorFilme.getVlrMeia()));
                XYChart.Data<String, Double> linhaInt = new XYChart.Data<>(textoChart, vendasPorFilme.getVlrIntegral());
                linhaFilmeInteira.getData().add(linhaInt);
                XYChart.Data<String, Double> linhaMeia = new XYChart.Data<>(textoChart, vendasPorFilme.getVlrMeia());
                linhaFilmeMeia.getData().add(linhaMeia);
                Platform.runLater(() -> { //Cria o Tooltip e botão de click
                    Tooltip tooltip = TooltipBuilder.create()
                            .text(String.format("Qtd.: %d - R$:%,.2f",
                                    vendasPorFilme.getQtdIngressosInt(), vendasPorFilme.getVlrIntegral()))
                            .font(new Font(15))
                            .build();
                    Tooltip tooltip2 = TooltipBuilder.create()
                            .text(String.format("Qtd.: %d - R$:%,.2f",
                                    vendasPorFilme.getQtdIngressosMeia(), vendasPorFilme.getVlrMeia()))
                            .font(new Font(15))
                            .build();
                    Tooltip.install(linhaInt.getNode(), tooltip);
                    Tooltip.install(linhaMeia.getNode(), tooltip2);
                });
            }
            sbcVendasFilme.getData().addAll(linhaFilmeInteira, linhaFilmeMeia);
        });
        /*PieChart - Vendas por Sala*/
        Platform.runLater(() -> {
            pieChartDataSala.clear();
            ArrayList<Integer> codSalas = new ArrayList<>();
            vendasObservableList.stream().sorted(Comparator.comparing(Vendas::getCodSala)).forEach(vendas -> {
                if (!codSalas.contains(vendas.getCodSala()))
                    codSalas.add(vendas.getCodSala());
            });
            for (Integer codSala : codSalas) {
                final String nomeSala = DBObjects.getSalaByCod(this.getClass(), codSala).getRefSala();
                VendasPorSala vendasPorSala = new VendasPorSala(codSala, nomeSala);
                vendasObservableList.stream().filter(vendas -> vendas.getCodSala() == codSala).forEach(vendas -> {
                    vendasPorSala.setQtdVlr(vendas.getQtdIngresso(), vendas.getVlrTotal());
                });
                PieChart.Data dadosSala = new PieChart.Data(String.format("%d - %s", vendasPorSala.getCodSala(), vendasPorSala.getNomeSala()),
                        vendasPorSala.getQtdIngresso());
                Platform.runLater(() -> {
                    Tooltip tooltip = TooltipBuilder.create()
                            .text(String.format("Qtd.: %d - R$:%,.2f",
                                    vendasPorSala.getQtdIngresso(), vendasPorSala.getVlrIngresso()))
                            .font(new Font(15))
                            .build();
                    Tooltip.install(dadosSala.getNode(), tooltip);
                });
                pieChartDataSala.add(dadosSala);
            }
        });
        /*PieChart - Vendas por Exibição*/
        Platform.runLater(() -> {
            pieChartDataExib.clear();
            ArrayList<Integer> codExibs = new ArrayList<>();
            vendasObservableList.stream().sorted(Comparator.comparing(Vendas::getCodExib)).forEach(vendas -> {
                if (!codExibs.contains(vendas.getCodExib()))
                    codExibs.add(vendas.getCodExib());
            });
            for (Integer codExib : codExibs) {
                final String nomeExib = DBObjects.getExibicaoByCod(this.getClass(), codExib).getNomeExibicao();
                VendasPorExib vendasPorExib = new VendasPorExib(codExib, nomeExib);
                vendasObservableList.stream().filter(vendas -> vendas.getCodExib() == codExib).forEach(vendas -> {
                    vendasPorExib.setQtdVlr(vendas.getQtdIngresso(), vendas.getVlrTotal());
                });
                PieChart.Data dadosExib = new PieChart.Data(String.format("%d - %s", vendasPorExib.getCodExib(), vendasPorExib.getNomeExib()),
                        vendasPorExib.getQtdIngresso());
                Platform.runLater(() -> {
                    Tooltip tooltip = TooltipBuilder.create()
                            .text(String.format("Qtd.: %d - R$:%,.2f",
                                    vendasPorExib.getQtdIngresso(), vendasPorExib.getVlrIngresso()))
                            .font(new Font(15))
                            .build();
                    Tooltip.install(dadosExib.getNode(), tooltip);
                });
                pieChartDataExib.add(dadosExib);
            }
        });
        /*LineChart - Vendas por Dia*/
        Platform.runLater(() -> {
            sacVendasDia.getData().clear();
            final XYChart.Series<String, Double> linhaDiaInteira = new XYChart.Series<>();
            linhaDiaInteira.setName("Inteira");
            final XYChart.Series<String, Double> linhaDiaMeia = new XYChart.Series<>();
            linhaDiaMeia.setName("Meia");
            ArrayList<Date> dias = new ArrayList<>();
            vendasObservableList.stream().sorted(Comparator.comparing(Vendas::getDataHoraSes)).forEach(vendas -> {
                if (!dias.contains(Date.valueOf(vendas.getDataHoraSes().toLocalDateTime().toLocalDate())))
                    dias.add(Date.valueOf(vendas.getDataHoraSes().toLocalDateTime().toLocalDate()));
            });
            for (Date dia : dias) {
                VendasPorDia vendasPorDia = new VendasPorDia(dia);
                vendasObservableList.stream().filter(vendas ->
                        !Date.valueOf(vendas.getDataHoraSes().toLocalDateTime().toLocalDate()).after(dia)
                                && !Date.valueOf(vendas.getDataHoraSes().toLocalDateTime().toLocalDate()).before(dia))
                        .forEach(vendas -> vendasPorDia.setVlrQtd(vendas.isIntegral(), vendas.getQtdIngresso(), vendas.getVlrTotal()));
                String diaText = Functions.getDataFormatted(Functions.dataFormater, dia);
                XYChart.Data<String, Double> linhaInt = new XYChart.Data<>(diaText, vendasPorDia.getVlrVendaInt());
                linhaDiaInteira.getData().add(linhaInt);
                XYChart.Data<String, Double> linhaMeia = new XYChart.Data<>(diaText, vendasPorDia.getVlrVendaMeia());
                linhaDiaMeia.getData().add(linhaMeia);
                Platform.runLater(() -> { //Cria o Tooltip e botão de click
                    Tooltip tooltip = TooltipBuilder.create()
                            .text(String.format("Qtd.: %d - R$:%,.2f",
                                    vendasPorDia.getQtdVendaInt(), vendasPorDia.getVlrVendaInt()))
                            .font(new Font(15))
                            .build();
                    Tooltip tooltip2 = TooltipBuilder.create()
                            .text(String.format("Qtd.: %d - R$:%,.2f",
                                    vendasPorDia.getQtdVendaMeia(), vendasPorDia.getVlrVendaMeia()))
                            .font(new Font(15))
                            .build();
                    Tooltip.install(linhaInt.getNode(), tooltip);
                    Tooltip.install(linhaMeia.getNode(), tooltip2);
                });
            }
            sacVendasDia.getData().addAll(linhaDiaInteira, linhaDiaMeia);
        });
        GravaLog.gravaInfo(this.getClass(), "Gráficos alimentados e estruturados, término ás " +
                Functions.getDataFormatted(Functions.dataHoraFormater, Timestamp.from(Instant.now())));
    }

    private void loadTableValues() {
        Conexao conex = new Conexao(this.getClass());
        try {
            vendasObservableList.clear();
            conex.createStatement(String.format("SELECT CODSESSAO, CODFILME, CODEXIB, CODSALA, INTEGRAL, DATAHORA, QTDING,\n" +
                            "CASE WHEN INTEGRAL \t= 'S' THEN ROUND((QTDING * VLREXIB),2)\n" +
                            "\t ELSE ROUND(((QTDING * VLREXIB) / 2),2)\n" +
                            "END AS VLRTOTAL\n" +
                            "FROM\n" +
                            "  (SELECT COUNT(1) AS QTDING, ING.CODSESSAO, SES.CODFILME, SES.CODEXIB, SES.CODSALA, \n" +
                            "\tSES.DATAHORA, ING.INTEGRAL, AVG(EXB.VLREXIB) AS VLREXIB\n" +
                            "   FROM TINGRESSOS ING\n" +
                            "   JOIN TSESSOES SES ON (ING.CODSESSAO = SES.CODSESSAO)\n" +
                            "   JOIN TEXIBS EXB ON (SES.CODEXIB = EXB.CODEXIB)\n" +
                            "   WHERE (0 = ? OR SES.CODSESSAO = ?)\n" +
                            "   AND (0 = ? OR SES.CODSALA = ?)\n" +
                            "   AND (0 = ? OR SES.CODEXIB = ?)\n" +
                            "   AND SES.DATAHORA >= ?\n" +
                            "   AND SES.DATAHORA <= ?\n" +
                            "   AND (0 IN (%s) OR SES.CODFILME IN (%s))" +
                            "   GROUP BY ING.CODSESSAO, SES.CODFILME, SES.CODEXIB, SES.CODSALA, ING.INTEGRAL) X",
                    Functions.paramBuilder(getCodFilmes()), Functions.paramBuilder(getCodFilmes())));

            conex.addParametro(getCodSessao(), getCodSessao(), getCodSala(), getCodSala(), getCodExib(), getCodExib(),
                    dtpInicial.getValue(), dtpFinal.getValue(), getCodFilmes(), getCodFilmes());
            conex.createSet();
            while (conex.rs.next()) {
                vendasObservableList.add(new Vendas(conex.rs.getInt(1),
                        conex.rs.getInt(2),
                        conex.rs.getInt(3),
                        conex.rs.getInt(4),
                        conex.rs.getString(5).equals("S"),
                        conex.rs.getTimestamp(6),
                        conex.rs.getInt(7),
                        conex.rs.getDouble(8)));
            }
            applyFilters();
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar atualizar gráfico de vendas\n%s", ex.getMessage()), ex)
                    .getAlert().showAndWait();
            vendasObservableList.clear();
        }
    }

    private void addLinhasFilme(Integer codFilme) {
        if (filmeFiltroList.stream().filter(filme -> filme.getCodFilme() == codFilme).count() > 0) {
            new ModelDialog(this.getClass(), Alert.AlertType.WARNING,
                    "Filme selecionado já está na lista de filtros").getAlert().showAndWait();
            return;
        }
        Filme filme = DBObjects.getFilmeByCod(this.getClass(), codFilme);
        filmeFiltroList.add(filme);
        filmeFiltroList.sort(Comparator.comparing(Filme::getCodFilme));
    }

    private void excLinhasFilme(ObservableList<Filme> filmes) {
        filmeFiltroList.removeAll(filmes);
    }

    private int getCodSessao() {
        if (txfCodSessao.getText().isEmpty())
            return 0;
        else
            return Integer.valueOf(txfCodSessao.getText());
    }

    private int getCodSala() {
        if (txfCodSala.getText().isEmpty())
            return 0;
        else
            return Integer.valueOf(txfCodSala.getText());
    }

    private int getCodExib() {
        if (txfCodExib.getText().isEmpty())
            return 0;
        else
            return Integer.valueOf(txfCodExib.getText());
    }

    private ArrayList<Integer> getCodFilmes() { //Fix
        ArrayList<Integer> codFilmes = new ArrayList<>();
        if (listFilmes.getItems().size() == 0)
            codFilmes.add(0);
        else
            listFilmes.getItems().forEach(filme -> codFilmes.add(filme.getCodFilme()));
        return codFilmes;
    }
}
