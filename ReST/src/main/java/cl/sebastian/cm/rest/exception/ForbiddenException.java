package cl.sebastian.cm.rest.exception;

/**
 * Excepción lanzada cuando el usuario no tiene permiso para acceder a un
 * recurso.
 *
 * <p>
 * Diferencia clave con {@link AuthException}: aquí el usuario IS autenticado,
 * pero no tiene permiso para acceder a ese recurso específico. Se traduce a
 * HTTP 403 Forbidden en el handler global.
 * </p>
 *
 * <p>
 * <b>Casos de uso:</b>
 * <ul>
 * <li>Staff de un comercio intenta acceder a datos de otro comercio</li>
 * <li>Comercio deshabilitado intenta hacer una solicitud</li>
 * <li>Tenant en mantenimiento rechaza operaciones</li>
 * <li>RUT del cliente no coincide con el token proporcionado</li>
 * </ul>
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Crea una excepción de acceso prohibido con mensaje por defecto.
     */
    public ForbiddenException() {
        super("Acceso NO permitido");
    }

    /**
     * Crea una excepción de acceso prohibido con mensaje personalizado.
     *
     * @param message descripción del motivo por el cual el acceso está
     * prohibido
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
