package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.models.ParametroType;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.util.Pair;

import java.util.List;

public class DBFunctions {

    public static String getBooToString(boolean value, int lenght) {
        if (value) {
            if (lenght == 1) return "S";
            else return "Sim";
        } else {
            if (lenght == 1) return "N";
            else return "Não";
        }
    }

    public static int checkIfExists(Class invocador, String nomeTabela, Pair<OpRelacional, Pair<String, List<Object>>>... filtros) {
        Conexao conex = new Conexao(invocador);
        StringBuilder strBuilder = new StringBuilder();
        int retorno = -1;
        strBuilder.append("SELECT COUNT(1) FROM " + nomeTabela);
        strBuilder.append("\nWHERE 1 = 1");
        try {
            for (Pair<OpRelacional, Pair<String, List<Object>>> filtro : filtros) {
                strBuilder.append(getParametros(filtro.getKey(), filtro.getValue().getKey(), filtro.getValue().getValue()));
            }
            conex.createStatement(strBuilder.toString());
            for (Pair<OpRelacional, Pair<String, List<Object>>> filtro : filtros)
                conex.addParametro(filtro.getValue().getValue());
            conex.createSet();
            conex.rs.next();
            retorno = conex.rs.getInt(1);
        } catch (Exception ex) {
            new ModelException(invocador, "Erro ao tentar contar registros\n" + strBuilder.toString() + "\n" + ex.getMessage(), ex)
                    .getAlert().showAndWait();
            retorno = -1;
        } finally {
            conex.desconecta();
            return retorno;
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

    private static String getParametros(OpRelacional opRelacional, String coluna, List<Object> values) {
        String retorno = "";
        switch (opRelacional) {
            case EQUALS:
                retorno = retorno.concat(String.format("\nAND %s IN (%s)", coluna, Functions.paramBuilder(values)));
                break;
            case DIFFERENT:
                retorno = retorno.concat(String.format("\nAND %s NOT IN (%s)", coluna, Functions.paramBuilder(values)));
                break;
            case LIKE:
                retorno = retorno.concat(String.format("\nAND %s LIKE ?", coluna));
                break;
            case NOT_LIKE:
                retorno = retorno.concat(String.format("\nAND %s NOT LIKE ?", coluna));
                break;
        }
        return retorno;
    }
}
