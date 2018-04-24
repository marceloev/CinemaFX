package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.models.*;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

import static br.com.cinemafx.dbcontrollers.DBFunctions.*;
import static br.com.cinemafx.dbcontrollers.DBFunctions.getBooToString;

public class DBBoss {

    public static int inseriUser(Class invocador, User user) throws Exception {
        if (checkIfExists(invocador, "TUSU",
                new Pair(OpRelacional.EQUALS, new Pair("LOGIN", Arrays.asList(user.getLoginUsu())))) > 0)
            throw new Exception("Já existe um usuário com este login");
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TUSU (NOMEUSU, LOGIN, SENHA, ATIVO)\n" +
                    "VALUES (?, ?, ?, ?)");
            conex.addParametro(user.getNomeUsu(), user.getLoginUsu(), user.getPassUsu().getPassword(),
                    getBooToString(user.getAtivoUsu(), 1));
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) { //Precisamos fazer o catch mesmo trhows por causa do desconecta.
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraUser(Class invocador, User user) throws Exception {
        if (checkIfExists(invocador, "TUSU",
                new Pair(OpRelacional.EQUALS, new Pair("LOGIN", Arrays.asList(user.getLoginUsu()))),
                new Pair(OpRelacional.DIFFERENT, new Pair("CODUSU", Arrays.asList(user.getCodUsu())))) > 0)
            throw new Exception("Já existe um usuário com este login");
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("UPDATE TUSU\n" +
                    "SET NOMEUSU = ?,\n" +
                    "LOGIN = ?,\n" +
                    "SENHA = ?,\n" +
                    "ATIVO = ?\n" +
                    "WHERE CODUSU = ?");
            conex.addParametro(user.getNomeUsu(), user.getLoginUsu(), user.getPassUsu().getPassword(),
                    getBooToString(user.getAtivoUsu(), 1), user.getCodUsu());
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiUser(Class invocador, ArrayList<Integer> users) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TUSU WHERE CODUSU IN (%s)", Functions.paramBuilder(users)));
            conex.addParametro(users);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static int inseriSala(Class invocador, Sala sala) throws Exception {
        if (checkIfExists(invocador, "TSALAS",
                new Pair(OpRelacional.EQUALS, new Pair("REFSALA", Arrays.asList(sala.getRefSala())))) > 0)
            throw new Exception("Já existe uma sala com esta referência");
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TSALAS (REFSALA, CAPACIDADE)\n" +
                    "VALUES (?, ?)");
            conex.addParametro(sala.getRefSala(), sala.getCapacidade());
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) { //Precisamos fazer o catch mesmo trhows por causa do desconecta.
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraSala(Class invocador, Sala sala) throws Exception {
        if (checkIfExists(invocador, "TSALAS",
                new Pair(OpRelacional.EQUALS, new Pair("REFSALA", Arrays.asList(sala.getRefSala()))),
                new Pair(OpRelacional.DIFFERENT, new Pair("CODSALA", Arrays.asList(sala.getCodSala())))) > 0)
            throw new Exception("Já existe uma sala com esta referência");
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("UPDATE TSALAS\n" +
                    "SET REFSALA = ?,\n" +
                    "CAPACIDADE = ?\n" +
                    "WHERE CODSALA = ?");
            conex.addParametro(sala.getRefSala(), sala.getCapacidade(), sala.getCodSala());
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiSala(Class invocador, ArrayList<Integer> salas) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TSALAS WHERE CODSALA IN (%s)", Functions.paramBuilder(salas)));
            conex.addParametro(salas);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static int inseriExibicao(Class invocador, Exibicao exibicao) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TEXIBS (NOMEEXIB, VLREXIB)\n" +
                    "VALUES (?, ?)");
            conex.addParametro(exibicao.getNomeExibicao(), exibicao.getVlrExibicao());
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraExibicao(Class invocador, Exibicao exibicao) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("UPDATE TEXIBS\n" +
                    "SET NOMEEXIB = ?,\n" +
                    "VLREXIB = ?\n" +
                    "WHERE CODEXIB = ?");
            conex.addParametro(exibicao.getNomeExibicao(), exibicao.getVlrExibicao(), exibicao.getCodExibicao());
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiExibicao(Class invocador, ArrayList<Integer> exibicoes) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TEXIBS WHERE CODEXIB IN (%s)", Functions.paramBuilder(exibicoes)));
            conex.addParametro(exibicoes);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static Genero inseriGenero(Class invocador, Genero genero) throws Exception {
        if (checkIfExists(invocador, "TGENEROS",
                new Pair(OpRelacional.EQUALS, new Pair("NOMEGENERO", genero.getNomeGenero()))) > 0)
            throw new Exception("Já existe um gênero com este nome");
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TGENEROS (NOMEGENERO)\n" +
                    "VALUES (?)");
            conex.addParametro(genero.getNomeGenero());
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return new Genero(conex.rs.getInt(1), genero.getNomeGenero());
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static int inseriFilme(Class invocador, Filme filme) throws Exception {
        if (filme.getGenero().getCodGenero() == -1)
            filme.setGenero(inseriGenero(invocador, filme.getGenero()));
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("INSERT INTO TFILMES (NOMEFILME, CUSTOFILME, MINFILME, CODGENERO, SINOPSE, IMAGEM)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?)");
            conex.addParametro(filme.getNomeFilme(), filme.getCustoFilme(), filme.getMinFilme(),
                    filme.getGenero().getCodGenero(), filme.getSinopse(), filme.getCartazFilme());
            conex.execute();
            conex.createStatement("SELECT LAST_INSERT_ID()");
            conex.createSet();
            conex.rs.next();
            return conex.rs.getInt(1);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void alteraFilme(Class invocador, Filme filme) throws Exception {
        if (filme.getGenero().getCodGenero() == -1)
            filme.setGenero(inseriGenero(invocador, filme.getGenero()));
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement("UPDATE TFILMES\n" +
                    "SET NOMEFILME = ?,\n" +
                    "CUSTOFILME = ?,\n" +
                    "MINFILME = ?,\n" +
                    "CODGENERO = ?,\n" +
                    "SINOPSE = ?,\n" +
                    "IMAGEM = ?\n" +
                    "WHERE CODFILME = ?");
            conex.addParametro(filme.getNomeFilme(), filme.getCustoFilme(), filme.getMinFilme(),
                    filme.getGenero().getCodGenero(), filme.getSinopse(), filme.getCartazFilme(), filme.getCodFilme());
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiFilme(Class invocador, ArrayList<Integer> filmes) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TFILMES WHERE CODFILME IN (%s)", Functions.paramBuilder(filmes)));
            conex.addParametro(filmes);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void excluiSessao(Class invocador, ArrayList<Integer> sessoes) throws Exception {
        Conexao conex = new Conexao(invocador);
        try {
            conex.createStatement(String.format("DELETE FROM TSESSOES WHERE CODSESSAO IN (%s)", Functions.paramBuilder(sessoes)));
            conex.addParametro(sessoes);
            conex.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
        }
    }

    public static void inseriSessao(Class invocador, ObservableList<Sessao> sessoes) throws Exception {
        if (sessoes == null || sessoes.isEmpty()) {
            throw new Exception("Não foram definidas sessões para o cadastro");
        }
        Conexao conex = null;
        ArrayList<Sessao> sessoesToExc = new ArrayList<>();
        try {
            for (Sessao sessao : sessoes) {
                conex = new Conexao(invocador);
                conex.createStatement("INSERT INTO TSESSOES (CODSALA, CODFILME, CODEXIB, DATAHORA)\n" +
                        "VALUES (?, ?, ?, ?)");
                conex.addParametro(sessao.getSala().getCodSala(),
                        sessao.getFilme().getCodFilme(),
                        sessao.getExibicao().getCodExibicao(),
                        sessao.getDataHoraExib());
                conex.execute();
                sessoesToExc.add(sessao);
                conex.desconecta();
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            conex.desconecta();
            sessoes.removeAll(sessoesToExc);
        }
    }
}
