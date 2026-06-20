package cl.sebastian.cm.rest.exception;

/**
 * Excepción lanzada cuando se ha alcanzado un límite de operaciones.
 *
 * <p>
 * Indica que un límite configurable ha sido superado (ej: número máximo de
 * jugadas por tienda, intentos de validación, etc.). Se traduce a HTTP 429 Too
 * Many Requests en el handler global.
 * </p>
 *
 * <p>
 * <b>Casos de uso:</b>
 * <ul>
 * <li>Número máximo de jugadas en una tienda alcanzado</li>
 * <li>Demasiados intentos de restablecimiento de contraseña</li>
 * <li>Rate limiting: demasiadas solicitudes en corto tiempo</li>
 * <li>Límite de transacciones alcanzado</li>
 * </ul>
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
public class LimitException extends RuntimeException {

    /**
     * Crea una excepción de límite alcanzado con mensaje por defecto.
     */
    public LimitException() {
        super("Límite alcanzado");
    }

    /**
     * Crea una excepción de límite alcanzado con mensaje personalizado.
     *
     * @param message descripción del límite que fue superado
     */
    public LimitException(String message) {
        super(message);
    }

    /**
     * Crea una excepción de límite alcanzado con causa raíz.
     *
     * @param message descripción del error
     * @param cause excepción técnica que causó el límite
     */
    public LimitException(String message, Throwable cause) {
        super(message, cause);
    }

}
