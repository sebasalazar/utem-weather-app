package cl.sebastian.cm.scheduler.exception;

public class SchException extends RuntimeException {

    public SchException() {
        super("Error en tareas programadas");
    }

    public SchException(String message) {
        super(message);
    }

    public SchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchException(Throwable cause) {
        super(cause);
    }
}
