package cl.sebastian.cm.rest.exception;

/**
 * Excepción lanzada cuando la autenticación falla.
 *
 * <p>
 * Indica que las credenciales proporcionadas (token JWT, token+key,
 * RUT+contraseña) son inválidas, ausentes o expiradas. Se traduce a HTTP 401
 * Unauthorized en el handler global.
 * </p>
 *
 * <p>
 * <b>Casos de uso:</b>
 * <ul>
 * <li>Token Bearer no encontrado o corrupto</li>
 * <li>Credenciales técnicas (token/key) inválidas</li>
 * <li>Usuario/staff intenta autenticarse sin credenciales válidas</li>
 * <li>JWT expirado o con firma incorrecta</li>
 * </ul>
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
public class AuthException extends RuntimeException {

    /**
     * Crea una excepción de autenticación con mensaje por defecto.
     */
    public AuthException() {
        super("Autenticación fallida");
    }

    /**
     * Crea una excepción de autenticación con mensaje personalizado.
     *
     * @param message descripción detallada del motivo de la autenticación
     * fallida
     */
    public AuthException(String message) {
        super(message);
    }

    /**
     * Crea una excepción de autenticación con causa raíz.
     *
     * @param message descripción del error
     * @param cause excepción técnica que causó el fallo
     */
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
