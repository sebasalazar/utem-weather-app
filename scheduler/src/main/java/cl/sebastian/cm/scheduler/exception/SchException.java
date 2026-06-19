package cl.sebastian.cm.scheduler.exception;

/**
 * Excepción personalizada no verificada para errores ocurridos durante las
 * tareas programadas del sistema.
 * <p>
 * Esta clase extiende {@link RuntimeException}, por lo que su uso no requiere
 * ser declarada en la cláusula {@code throws} ni ser capturada
 * obligatoriamente. Se utiliza para envolver y unificar los distintos tipos de
 * errores que pueden ocurrir en el módulo de scheduler (problemas de red,
 * errores HTTP, fallos de persistencia, etc.), proporcionando un mensaje claro
 * y opcionalmente la causa raíz.
 * </p>
 * <p>
 * <strong>Uso típico:</strong>
 * </p>
 * <pre>
 * try {
 *     // operación que puede fallar
 * } catch (HttpClientErrorException e) {
 *     throw new SchException("Error al consultar el servicio externo", e);
 * }
 * </pre>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see RuntimeException
 */
public class SchException extends RuntimeException {

    /**
     * Constructor por defecto que crea una excepción con el mensaje
     * {@code "Error en tareas programadas"}.
     */
    public SchException() {
        super("Error en tareas programadas");
    }

    /**
     * Constructor que permite especificar un mensaje personalizado.
     *
     * @param message descripción detallada del error.
     */
    public SchException(String message) {
        super(message);
    }

    /**
     * Constructor que permite especificar un mensaje y la causa original de la
     * excepción.
     *
     * @param message descripción detallada del error.
     * @param cause causa original (puede ser {@code null}).
     */
    public SchException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor que permite especificar únicamente la causa original. El
     * mensaje se obtiene de {@link Throwable#toString()} de la causa.
     *
     * @param cause causa original (puede ser {@code null}).
     */
    public SchException(Throwable cause) {
        super(cause);
    }
}
