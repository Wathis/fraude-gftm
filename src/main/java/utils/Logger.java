package utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    final static boolean ERROR_ACTIVATED = true;
    final static boolean INFO_ACTIVATED = true;
    static TextArea loggerView = null;

    public static void info(String arg) {
        if (!INFO_ACTIVATED)
            return;
        System.out.println(arg);
    }

    public static void info(String arg, boolean onUI) {
        if (onUI)
            printInLoggerView(arg);
        info(arg);
    }

    public static void err(String arg) {
        if (!ERROR_ACTIVATED)
            return;
        System.err.println(arg);
    }

    public static void err(String arg, boolean onUI) {
        if (onUI)
            printInLoggerView(arg);
        err(arg);
    }

    public static void printInLoggerView(String arg) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String message = "["+ dateFormat.format(date) +"] " + arg + "\n";
        if (Platform.isFxApplicationThread()) {
            loggerView.appendText(message);
        } else {
            Platform.runLater(() -> loggerView.appendText(message));
        }
        loggerView.setScrollTop(0);
        loggerView.setScrollLeft(0);
    }
    public static void setLoggerView(TextArea textArea) {
        loggerView = textArea;
    }

}
