package exception;

public class UninstantiatedException extends RuntimeException {

    public UninstantiatedException() {
        super("You must instantiate before using it");
    }

    public UninstantiatedException(String message) {
        super(message);
    }

}
