package cl.sebastian.cm.rest.exception;

/**
 * Excepción lanzada cuando los datos recuperados de la base de datos son
 * inconsistentes.
 *
 * <p>
 * Indica que se encontró un registro, pero sus datos están corruptos,
 * incompletos o violan invariantes de integridad referencial. Típicamente
 * indica un error en la base de datos que requiere intervención operacional.
 * </p>
 *
 * <p>
 * <b>Casos de uso:</b>
 * <ul>
 * <li>Comercio sin tenant asociado</li>
 * <li>Tienda sin comercio asignado</li>
 * <li>Campaña con datos incompletos</li>
 * <li>Integridad referencial violada</li>
 * </ul>
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
public class BadDataException extends RuntimeException {

    /**
     * Crea una excepción de datos inconsistentes sin mensaje específico.
     */
    public BadDataException() {
    }

    /**
     * Crea una excepción de datos inconsistentes con mensaje personalizado.
     *
     * @param message descripción de la inconsistencia detectada
     */
    public BadDataException(String message) {
        super(message);
    }

    /**
     * Crea una excepción de datos inconsistentes con causa raíz.
     *
     * @param message descripción del error
     * @param cause excepción técnica que reveló la inconsistencia
     */
    public BadDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
