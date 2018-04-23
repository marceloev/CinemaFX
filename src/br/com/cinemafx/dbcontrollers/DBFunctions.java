package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.models.ParametroType;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.util.Pair;

import java.util.ArrayList;

public class DBFunctions {

    public static String getBooToString(boolean value, int lenght) {
        if (value) {
            if (lenght == 1)
                return "S";
            else
                return "Sim";
        } else {
            if (lenght == 1)
                return "N";
            else
                return "Não";
        }
    }

    public static int checkIfExists(Class invocador, String nomeTabela, Pair<String, Object> filtro) throws Exception {
        Conexao conex = new Conexao(invocador); //No Pair, a chave já tem que vir com o operador relacional Ex: Pair<>("CODSALA <>", 1)
        try {
            conex.createStatement(String.format("SELECT COUNT(1) FROM %s\n" +
                    "WHERE 1 = 1\n" +
                    "AND %s ?", nomeTabela, filtro.getKey()));
            conex.addParametro(filtro.getValue());
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static int checkIfExists(Class invocador, String nomeTabela, ArrayList<Pair<String, Object>> filtros) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            StringBuilder strBuildFiltros = new StringBuilder();
            ArrayList<Object> objFiltros = new ArrayList<>();
            for (Pair<String, Object> filtro : filtros) {
                objFiltros.add(filtro.getValue());
                strBuildFiltros.append(String.format("\nAND %s ?", filtro.getKey()));
            }
            conex.createStatement(String.format("SELECT COUNT(1) FROM %s\n" +
                    "WHERE 1 = 1 %s", nomeTabela, strBuildFiltros.toString()));
            conex.addParametro(objFiltros);
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static Object getUserParametro(Class invocador, String chave, ParametroType parametroType, int codUsu) {
        Conexao conex = new Conexao(invocador);
        Object resp = -1;
        try {
            conex.createStatement(String.format("SELECT %s FROM TPARAMETROS\n" +
                            "WHERE CHAVE = ?\n" +
                            "AND CODUSU IN (?, 0)\n" +
                            "ORDER BY CODUSU DESC", parametroType.toString().toUpperCase()));
            conex.addParametro(chave, codUsu);
            conex.createSet();
            if (conex.rs.next()) {
                resp = conex.rs.getObject(1);
            } else {
                resp = -1;
            }
        } catch (Exception ex) {
            new ModelException(invocador, String.format("Erro ao tentar buscar parâmetro %s\n%s", chave, ex.getMessage()), ex)
                    .getAlert().showAndWait();
        } finally {
            conex.desconecta();
            return resp;
        }
    }
}
