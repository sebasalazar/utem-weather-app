package cl.sebastian.cm.rest.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe.
 *
 * <p>
 * Indica que una búsqueda en la base de datos no retornó resultados. Se traduce
 * a HTTP 404 Not Found en el handler global.
 * </p>
 *
 * <p>
 * <b>Casos de uso:</b>
 * <ul>
 * <li>Cliente/jugador no encontrado por token</li>
 * <li>Campaña no encontrada</li>
 * <li>Tienda no existe</li>
 * <li>Staff no registrado en el sistema</li>
 * <li>Recurso solicitado eliminado o nunca existió</li>
 * </ul>
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
public class NoDataException extends RuntimeException {

    /**
     * Crea una excepción de recurso no encontrado con mensaje por defecto.
     */
    public NoDataException() {
        super("No se han encontrado datos para su requerimiento");
    }

    /**
     * Crea una excepción de recurso no encontrado con mensaje personalizado.
     *
     * @param message descripción detallada de qué recurso no se encontró
     */
    public NoDataException(String message) {
        super(message);
    }

}
