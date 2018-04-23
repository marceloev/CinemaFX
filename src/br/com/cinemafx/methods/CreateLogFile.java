package br.com.cinemafx.methods;

import br.com.cinemafx.methods.log.GravaLog;
import br.com.cinemafx.models.User;
import br.com.cinemafx.views.dialogs.ModelDialog;
import br.com.cinemafx.views.dialogs.ModelException;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CreateLogFile {

    public static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CreateLogFile(MenuButton btn) {
        GravaLog.gravaInfo(this.getClass(), "Efetuando download de log do sistema");
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Escolha onde o log do sistema será salvo");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pacote de Log", ".zip");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialFileName("ServerLog");
            File file = fileChooser.showSaveDialog(btn.getScene().getWindow());
            if (file != null) {
                FileOutputStream fileOps = new FileOutputStream(file);
                ZipOutputStream zipOps = new ZipOutputStream(fileOps);
                /*Log atual*/
                addFileToZip(zipOps, "Server.log", String.format("log/Sinergia_%s.log", logDateFormat.format(Timestamp.from(Instant.now()))));
                /*Arquivo de propriedades*/
                addFileToZip(zipOps, "Server.properties", getAppProperties());
                zipOps.close();
                fileOps.close();
                new ModelDialog(this.getClass(), Alert.AlertType.INFORMATION,
                        "Download do log do sistema concluído com sucesso").getAlert().showAndWait();
            }
        } catch (Exception ex) {
            new ModelException(this.getClass(), "Erro ao tentar efetuar download do log\n" + ex.getMessage(), ex).getAlert().showAndWait();
        }
    }

    private void addFileToZip(ZipOutputStream zipOps, String nomeArq, String urlArq) throws Exception {
        FileInputStream fileIps = new FileInputStream(urlArq);
        ZipEntry zipEntry = new ZipEntry(nomeArq);
        zipOps.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fileIps.read(bytes)) >= 0)
            zipOps.write(bytes, 0, length);
        zipOps.closeEntry();
        fileIps.close();
    }

    private void addFileToZip(ZipOutputStream zipOps, String nomeArq, File file) throws Exception {
        FileInputStream fileIps = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(nomeArq);
        zipOps.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fileIps.read(bytes)) >= 0)
            zipOps.write(bytes, 0, length);
        zipOps.closeEntry();
        fileIps.close();
    }

    private File getAppProperties() throws Exception {
        File arqProperties = new File("AppProperties.ini");
        FileOutputStream fileIps = new FileOutputStream(arqProperties);
        OutputStreamWriter writerOps = new OutputStreamWriter(fileIps);
        Writer w = new BufferedWriter(writerOps);
        writeNewLine(w, String.format("Usuário: %d - %s : %s", User.getCurrent().getCodUsu(),
                User.getCurrent().getLoginUsu(), User.getCurrent().getNomeUsu()));
        writeNewLine(w, String.format("Versão do Executável: %s", AppInfo.getInfo().getVersaoExec()));
        writeNewLine(w, String.format("Versão do Banco de Dados: %s", AppInfo.getInfo().getVersaoBD()));
        writeNewLine(w, String.format("IP da Máquina: %s", AppInfo.getInfo().getIPMaquina()));
        writeNewLine(w, String.format("Nome da Máquina: %s", AppInfo.getInfo().getNomeMaquina()));
        writeNewLine(w, String.format("Data/Hora Login: %s", Functions.getDataFormatted(Functions.dataHoraFormater, AppInfo.getInfo().getDhLogin())));
        PrintWriter printWriter = new PrintWriter(w);
        System.getProperties().list(printWriter);
        w.close();
        return arqProperties;
    }

    private void writeNewLine(Writer writer, String value) throws Exception {
        writer.write(value);
        ((BufferedWriter) writer).newLine();
    }

}
