package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.methods.log.GravaLog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

public class Conexao {

    private PreparedStatement pst;
    public ResultSet rs;
    private Class invocador;
    private Connection connection;
    private int index = 1;

    public Conexao(Class invocador) {
        setInvocador(invocador);
        initProperties();
    }

    private void initProperties() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            /* Cloud
            Properties properties = new Properties();
            properties.setProperty("user", "bc4d7761e0441c");
            properties.setProperty("password", "945f226c6587454");
            properties.setProperty("connectTimeout", "200000");
            setConnection(DriverManager.getConnection("jdbc:mysql://us-cdbr-iron-east-05.cleardb.net:3306/heroku_384bec9114c87a4", properties));
            */
            /* Local */
            Properties properties = new Properties();
            properties.setProperty("user", "root");
            properties.setProperty("password", "");
            properties.setProperty("connectTimeout", "200000");
            properties.setProperty("useSSL", "true");
            setConnection(DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/cinemafx", properties));
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar comunicar com banco de dados\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        }
    }

    public void remakeConexao() {
        this.index = 1;
        this.pst = null;
        this.rs = null;
        this.desconecta();
        initProperties();
    }

    public void createStatement(String statement) throws SQLException {
        GravaLog.gravaInfo(getInvocador(), statement);
        this.pst = getConnection().prepareStatement(statement);
    }

    public void desconecta() {
        try {
            getConnection().close();
        } catch (Exception ex) {
            new ModelException(this.getClass(),
                    String.format("Erro ao tentar desconectar com banco de dados\n%s", ex.getMessage(), ex)).getAlert().showAndWait();
        }
    }

    public int execute() throws SQLException {
        long rsTimeInit = System.currentTimeMillis(); //Gravar o tempo gasto para executar o statement
        int rows = this.pst.executeUpdate();
        long rsTimeTotal = System.currentTimeMillis() - rsTimeInit;
        GravaLog.gravaInfo(getInvocador(), "Statement Source retornado em " + rsTimeTotal + "ms, " + rows + " linha(s) afetadas");
        return rows;
    }

    public void createSet() throws SQLException {
        long rsTimeInit = System.currentTimeMillis(); //Gravar o tempo gasto para criar o resulSet
        this.rs = this.pst.executeQuery();
        long rsTimeTotal = System.currentTimeMillis() - rsTimeInit;
        GravaLog.gravaInfo(getInvocador(), "ResultSet Source criado em " + rsTimeTotal + "ms");
    }

    public int countRows() throws SQLException {
        this.rs.last();
        int rows = this.rs.getRow();
        this.rs.beforeFirst();
        return rows;
    }

    public void addParametro(Object... objetos) throws SQLException {
        for (Object objeto : objetos) addParametro(objeto);
    }

    public void addParametro(Object objeto) throws SQLException {
        GravaLog.gravaInfo(invocador, String.format("%dº Parâmetro(%s): %s", index, objeto.getClass().getTypeName(), objeto));
        if (objeto == null)
            addToStatement(index, null, "java.cinemafx.NullParameter");
        else
            addToStatement(index, objeto, objeto.getClass().getTypeName());
        index++;
    }

    private void addToStatement(int index, Object objeto, String type) throws SQLException {
        switch (type) {
            case "java.cinemafx.NullParameter":
                pst.setObject(index, null);
                break;
            case "java.lang.Double":
                pst.setDouble(index, (Double) objeto);
                break;
            case "java.lang.Integer":
                pst.setInt(index, (Integer) objeto);
                break;
            case "java.sql.Date":
                pst.setDate(index, (Date) objeto);
                break;
            case "java.time.LocalDate":
                pst.setDate(index, Date.valueOf((LocalDate) objeto));
                break;
            case "java.lang.Character":
                pst.setString(index, objeto.toString());
                break;
            case "java.lang.String":
                pst.setString(index, (String) objeto);
                break;
            case "java.sql.Timestamp":
                pst.setTimestamp(index, (Timestamp) objeto);
                break;
            case "byte[]":
                pst.setBytes(index, (byte[]) objeto);
                break;
            case "javafx.scene.image.Image":
                try {
                    BufferedImage bImage = SwingFXUtils.fromFXImage((Image) objeto, null);
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "png", s);
                    pst.setBytes(index, s.toByteArray());
                } catch (IOException ex) {
                    throw new SQLException(ex);
                }
                break;
            case "java.lang.Object[]":
                for (Object obj : (Object[]) objeto)
                    addParametro(obj);
                this.index--;
                break;
            case "java.util.ArrayList":
                for (Object obj : (ArrayList<Object>) objeto)
                    addParametro(obj);
                this.index--;
                break;
            default:
                throw new SQLException(String.format("Erro ao tentar inserir parâmetro no Statement\n" +
                        "Tipo de parâmetro: %s não configurado", type));
        }
    }

    public Class getInvocador() {
        return invocador;
    }

    public void setInvocador(Class invocador) {
        this.invocador = invocador;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
