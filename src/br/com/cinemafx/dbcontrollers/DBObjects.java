package br.com.cinemafx.dbcontrollers;

import br.com.cinemafx.methods.Functions;
import br.com.cinemafx.models.*;
import br.com.cinemafx.views.dialogs.ModelException;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class DBObjects {

    private static Conexao conex = new Conexao(DBObjects.class);
    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Sala> salas = new ArrayList<Sala>();
    private static ArrayList<Exibicao> exibicoes = new ArrayList<Exibicao>();
    private static ArrayList<Genero> generos = new ArrayList<Genero>();
    private static ArrayList<Filme> filmes = new ArrayList<Filme>();
    private static ArrayList<Sessao> sessoes = new ArrayList<Sessao>();

    public static ArrayList<User> reloadUsers() {
        conex.remakeConexao();
        users.clear();
        try {
            conex.createStatement("SELECT CODUSU, NOMEUSU, LOGIN, SENHA, ATIVO\n" +
                    "FROM TUSU");
            conex.createSet();
            while (conex.rs.next()) {
                users.add(new User(conex.rs.getInt(1),
                        conex.rs.getString(2),
                        conex.rs.getString(3),
                        new Password(true, conex.rs.getString(4)),
                        conex.rs.getString(5).equals("S")));
            }
        } catch (Exception ex) {
            users.clear();
            new ModelException(DBObjects.class,
                    String.format("Erro ao tentar atualizar lista de usuários\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
            return users;
        }
    }

    public static ArrayList<Sala> reloadSala() {
        conex.remakeConexao();
        salas.clear();
        try {
            conex.createStatement("SELECT CODSALA, REFSALA, CAPACIDADE FROM TSALAS");
            conex.createSet();
            while (conex.rs.next()) {
                salas.add(new Sala(
                        conex.rs.getInt(1),
                        conex.rs.getString(2),
                        conex.rs.getInt(3)
                ));
            }
        } catch (Exception ex) {
            salas.clear();
            new ModelException(DBObjects.class,
                    String.format("Erro ao tentar atualizar lista de salas\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
            return salas;
        }
    }

    public static ArrayList<Exibicao> reloadExibicoes() {
        conex.remakeConexao();
        exibicoes.clear();
        try {
            conex.createStatement("SELECT CODEXIB, NOMEEXIB, VLREXIB FROM TEXIBS");
            conex.createSet();
            while (conex.rs.next()) {
                exibicoes.add(new Exibicao(
                        conex.rs.getInt(1),
                        conex.rs.getString(2),
                        conex.rs.getDouble(3)
                ));
            }
        } catch (Exception ex) {
            exibicoes.clear();
            new ModelException(DBObjects.class,
                    String.format("Erro ao tentar atualizar lista de exibições\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
            return exibicoes;
        }
    }

    public static ArrayList<Genero> reloadGeneros() {
        conex.remakeConexao();
        generos.clear();
        try {
            conex.createStatement("SELECT CODGENERO, NOMEGENERO FROM TGENEROS");
            conex.createSet();
            while (conex.rs.next()) {
                generos.add(new Genero(
                        conex.rs.getInt(1),
                        conex.rs.getString(2)
                ));
            }
        } catch (Exception ex) {
            generos.clear();
            new ModelException(DBObjects.class,
                    String.format("Erro ao tentar atualizar lista de generos\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
            return generos;
        }
    }

    public static ArrayList<Filme> reloadFilmes() {
        conex.remakeConexao();
        filmes.clear();
        try {
            //reloadGeneros();
            conex.createStatement("SELECT CODFILME, NOMEFILME, SINOPSE,\n" +
                    "CUSTOFILME, CODGENERO, MINFILME, IMAGEM\n" +
                    "FROM TFILMES");
            conex.createSet();
            while (conex.rs.next()) {
                int codGenero = conex.rs.getInt(5);
                filmes.add(new Filme(
                        conex.rs.getInt(1),
                        conex.rs.getString(2),
                        conex.rs.getString(3),
                        conex.rs.getDouble(4),
                        generos.stream().filter(genero ->
                                genero.getCodGenero() == codGenero).findFirst().get(),
                        conex.rs.getInt(6),
                        Functions.getImgFromBlob(conex.rs.getBytes(7))
                ));
            }
        } catch (Exception ex) {
            filmes.clear();
            new ModelException(DBObjects.class,
                    String.format("Erro ao tentar atualizar lista de filmes\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
            return filmes;
        }
    }

    public static ArrayList<Sessao> reloadSessoes() {
        conex.remakeConexao();
        sessoes.clear();
        try {
            reloadSala();
            reloadFilmes();
            reloadGeneros();
            reloadExibicoes();
            conex = new Conexao(DBObjects.class);
            conex.createStatement("SELECT CODSESSAO, CODSALA, CODFILME, CODEXIB, DATAHORA FROM TSESSOES");
            conex.createSet();
            while (conex.rs.next()) {
                int codSala = conex.rs.getInt(2);
                int codFilme = conex.rs.getInt(3);
                int codExib = conex.rs.getInt(4);
                sessoes.add(new Sessao(
                        conex.rs.getInt(1),
                        salas.stream().filter(sala -> sala.getCodSala() == codSala).findFirst().get(),
                        filmes.stream().filter(filme -> filme.getCodFilme() == codFilme).findFirst().get(),
                        exibicoes.stream().filter(sessao -> sessao.getCodExibicao() == codExib).findFirst().get(),
                        conex.rs.getTimestamp(5)
                ));
            }
        } catch (Exception ex) {
            sessoes.clear();
            new ModelException(DBObjects.class,
                    String.format("Erro ao tentar atualizar lista de sessões\n%s", ex.getMessage()), ex).getAlert().showAndWait();
        } finally {
            conex.desconecta();
            return sessoes;
        }
    }

    public static ArrayList<User> getUsers() {
        if (users.isEmpty()) reloadUsers();
        return users;
    }

    public static ArrayList<Sala> getSalas() {
        if (salas.isEmpty()) reloadSala();
        return salas;
    }

    public static ArrayList<Exibicao> getExibicoes() {
        if (exibicoes.isEmpty()) reloadExibicoes();
        return exibicoes;
    }

    public static ArrayList<Genero> getGeneros() {
        if (generos.isEmpty()) reloadGeneros();
        return generos;
    }

    public static ArrayList<Filme> getFilmes() {
        if (generos.isEmpty()) reloadGeneros();
        if (filmes.isEmpty()) reloadFilmes();
        return filmes;
    }

    public static ArrayList<Sessao> getSessoes() {
        if (sessoes.isEmpty()) reloadSessoes();
        return sessoes;
    }

    public static void reloadAll() {
        reloadSala();
        reloadGeneros();
        reloadFilmes();
        reloadExibicoes();
    }

    public static Boolean salaContains(Integer codSala) {
        if (DBObjects.getSalas().stream().filter(sala -> sala.getCodSala() == codSala).count() == 0)
            return false;
        else
            return true;
    }

    public static Sala getSalaByCod(Class invocador, Integer codSala) {
        Sala sala = null;
        try {
            sala = DBObjects.getSalas().stream().filter(salas -> salas.getCodSala() == codSala).findFirst().get();
        } catch (NoSuchElementException ex) {
            new ModelException(invocador, String.format("Sala para código %d não encontrada", codSala)).getAlert().showAndWait();
        } catch (Exception ex) {
            new ModelException(invocador, String.format("Erro ao tentar localizar sala por código\n%s", ex.getMessage())).getAlert().showAndWait();
        } finally {
            return sala;
        }
    }

    public static Boolean filmeContains(Integer codFilme) {
        if (DBObjects.getFilmes().stream().filter(filme -> filme.getCodFilme() == codFilme).count() == 0)
            return false;
        else
            return true;
    }

    public static Filme getFilmeByCod(Class invocador, Integer codFilme) {
        Filme filme = null;
        try {
            filme = DBObjects.getFilmes().stream().filter(filmes -> filmes.getCodFilme() == codFilme).findFirst().get();
        } catch (NoSuchElementException ex) {
            new ModelException(invocador, String.format("Filme para código %d não encontrada", filme)).getAlert().showAndWait();
        } catch (Exception ex) {
            new ModelException(invocador, String.format("Erro ao tentar localizar filme por código\n%s", ex.getMessage())).getAlert().showAndWait();
        } finally {
            return filme;
        }
    }

    public static Boolean exibContains(Integer codExib) {
        if (DBObjects.getExibicoes().stream().filter(exib -> exib.getCodExibicao() == codExib).count() == 0)
            return false;
        else
            return true;
    }

    public static Exibicao getExibicaoByCod(Class invocador, Integer codExibicao) {
        Exibicao exibicao = null;
        try {
            exibicao = DBObjects.getExibicoes().stream().filter(exib -> exib.getCodExibicao() == codExibicao).findFirst().get();
        } catch (NoSuchElementException ex) {
            new ModelException(invocador, String.format("Exibição para código %d não encontrada", codExibicao)).getAlert().showAndWait();
        } catch (Exception ex) {
            new ModelException(invocador, String.format("Erro ao tentar localizar exibição por código\n%s", ex.getMessage())).getAlert().showAndWait();
        } finally {
            return exibicao;
        }
    }

    public static Boolean sessaoContains(Integer codSessao) {
        if (DBObjects.getSessoes().stream().filter(sessao -> sessao.getCodSessao() == codSessao).count() == 0)
            return false;
        else
            return true;
    }

    public static Sessao getSessaoByCod(Class invocador, Integer codSessao) {
        Sessao sessao = null;
        try {
            sessao = DBObjects.getSessoes().stream().filter(sessoes -> sessoes.getCodSessao() == codSessao).findFirst().get();
        } catch (NoSuchElementException ex) {
            new ModelException(invocador, String.format("Sessão para código %d não encontrada", codSessao)).getAlert().showAndWait();
        } catch (Exception ex) {
            new ModelException(invocador, String.format("Erro ao tentar localizar sessão por código\n%s", ex.getMessage())).getAlert().showAndWait();
        } finally {
            return sessao;
        }
    }

}
