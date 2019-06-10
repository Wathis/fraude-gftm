package utils;

public class Logger {

    final static boolean ERROR_ACTIVATED = true;
    final static boolean INFO_ACTIVATED = true;

    public static void info(String arg) {
        if (!INFO_ACTIVATED)
            return;
        System.out.println(arg);
    }

    public static void err(String arg) {
        if (!ERROR_ACTIVATED)
            return;
        System.err.println(arg);
    }

}
