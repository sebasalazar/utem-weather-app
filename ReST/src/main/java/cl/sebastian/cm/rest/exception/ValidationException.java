package cl.sebastian.cm.rest.exception;

/**
 * Excepción lanzada cuando un valor no cumple con reglas de validación de
 * negocio.
 *
 * <p>
 * Indica que un parámetro, campo o estado de la solicitud viola una regla de
 * negocio del dominio. Se traduce a HTTP 400 Bad Request en el handler global.
 * </p>
 *
 * <p>
 * <b>Casos de uso:</b>
 * <ul>
 * <li>RUT inválido (formato o dígito verificador)</li>
 * <li>Token malformado o con formato incorrecto</li>
 * <li>Tipo de juego no soportado</li>
 * <li>Email inválido</li>
 * <li>Número de opciones no coincide con la configuración</li>
 * <li>Términos y condiciones no aceptados</li>
 * <li>Campaña no jugable (audiencia incompatible)</li>
 * </ul>
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
public class ValidationException extends RuntimeException {

    /**
     * Crea una excepción de validación con mensaje por defecto.
     */
    public ValidationException() {
        super("Error de validación");
    }

    /**
     * Crea una excepción de validación con mensaje personalizado.
     *
     * @param message descripción detallada de qué validación falló y por qué
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Crea una excepción de validación con causa raíz.
     *
     * @param message descripción del error
     * @param cause excepción que causó el fallo de validación
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
