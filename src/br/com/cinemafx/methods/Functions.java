package br.com.cinemafx.methods;

import br.com.cinemafx.views.dialogs.ModelException;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Functions {

    public static final Image noImageFilme = new Image("/br/com/cinemafx/views/images/Icon_Sem_Imagem.png");
    public static final SimpleDateFormat dataHoraFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter timeHoraformatter = DateTimeFormatter.ofPattern("HH:mm");
    public static final SimpleDateFormat dataFormater = new SimpleDateFormat("dd/MM");

    public static String getDataFormatted(SimpleDateFormat dataHoraFormater, Object value) {
        String valor = "";
        if (value == null || value == "")
            return valor;
        else {
            try {
                valor = dataHoraFormater.format(value);
            } catch (Exception ex) {
                new ModelException(Functions.class, "Erro ao tentar formatar data\n" + ex.getMessage(), ex).getAlert().showAndWait();
            }
        }
        return valor;
    }

    public static final String Nvl(String valor) {
        if (valor == null) return "";
        else return valor;
    }

    public static final String Nvl(String valor, String padrao) {
        if (valor == null || valor.isEmpty()) return padrao;
        else return valor;
    }

    public static Boolean ToBoo(String valor) {
        if (valor.equals("S") || valor.equals("Sim") || valor.equals("s") || valor.equals("sim") || valor.equals("1"))
            return true;
        else
            return false;
    }

    public static Boolean ToBoo(long valor) {
        if (valor > 0)
            return true;
        else
            return false;
    }


    public static final Image getImgFromBlob(byte[] bytes) {
        if (bytes != null) {
            InputStream input = new ByteArrayInputStream(bytes);
            Image imgUsu = new Image(input);
            return imgUsu;
        } else return noImageFilme;
    }

    public static StringBuilder paramBuilder(ArrayList<?> array) {
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (i == 0) paramBuilder.append("?");
            else paramBuilder.append(", ?");
        }
        return paramBuilder;
    }

    public static int getOnlyNumbers(String valor) {
        return Integer.valueOf(valor.replaceAll("[^0-9]", ""));
    }

    public static Double getDoubleFrom(String valor) {
        if (valor.contains(".") || valor.contains(",")) {
            return Double.valueOf(valor.replace(",", "."));
        } else {
            int value = Integer.valueOf("0" + valor.replaceAll("[^0-9]", ""));
            return Double.valueOf(value);
        }
    }

    public static String translate(String valor) {
        valor = Nvl(valor);
        String retorno = valor.toUpperCase();
        retorno = retorno.replaceAll(" ", "");
        return retorno;
    }

}
