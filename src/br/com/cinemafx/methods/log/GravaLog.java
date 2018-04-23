package br.com.cinemafx.methods.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;

public class GravaLog {

    private final static Logger log = Logger.getLogger(GravaLog.class);
    private final static URL url = GravaLog.class.getResource("/br/com/cinemafx/methods/log/Log4j.properties");
    private static Boolean configurado = false;

    public static void gravaInfo(Class invoker, String info) {
        if (isConfigurado()) {
            log.info("[" + invoker + "] : " + info);
        }
    }

    public static void gravaAlerta(Class invoker, String info) {
        if (isConfigurado()) {
            log.warn("[" + invoker + "] : " + info);
        }
    }

    public static void gravaErro(Class invoker, String info) {
        if (isConfigurado()) {
            log.error("[" + invoker + "] : " + info);
        }
    }

    public static void gravaErro(Class invoker, String info, Throwable trhows) {
        if (isConfigurado()) {
            log.error("[" + invoker + "] : " + info, trhows);
        }
    }

    private static Boolean isConfigurado() {
        if (!configurado) {
            PropertyConfigurator.configure(url);
            configurado = true;
        }
        return true;
    }
}