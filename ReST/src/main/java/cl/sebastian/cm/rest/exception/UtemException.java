package cl.sebastian.cm.rest.exception;

/**
 * Excepción base para errores de dominio de LY Gamification.
 *
 * <p>
 * Excepción no verificada (extends {@link RuntimeException}) que representa
 * errores de lógica de negocio que el sistema puede manejar y que deben
 * comunicarse al cliente con un HTTP status específico.
 * </p>
 *
 * <p>
 * Cada instancia mantiene un código interno (por defecto 412 o 422) que se usa
 * para determinar el HTTP status de la respuesta en {@link
 * cl.lygamification.backend.api.ApiExceptionHandler}.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
public class UtemException extends RuntimeException {

    private int code = 412;

    /**
     * Crea una excepción de dominio con mensaje por defecto.
     *
     * <p>
     * Asigna código 412 (Precondition Failed) por defecto.
     * </p>
     */
    public UtemException() {
        super("Error al procesar");
        this.code = 412;
    }

    /**
     * Crea una excepción de dominio con mensaje personalizado.
     *
     * <p>
     * Asigna código 412 (Precondition Failed) por defecto.
     * </p>
     *
     * @param message descripción del error
     */
    public UtemException(String message) {
        super(message);
        this.code = 412;
    }

    /**
     * Crea una excepción de dominio con código y mensaje personalizados.
     *
     * @param code código HTTP o interno personalizado
     * @param message descripción del error
     */
    public UtemException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Retorna el código interno asociado a esta excepción.
     *
     * @return código HTTP o código interno (412, 422, u otro valor
     * personalizado)
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Crea una excepción de dominio con causa raíz.
     *
     * <p>
     * Asigna código 422 (Unprocessable Content) para indicar un error de
     * procesamiento causado por una excepción técnica.
     * </p>
     *
     * @param message descripción del error
     * @param cause excepción original que causó este error
     */
    public UtemException(String message, Throwable cause) {
        super(message, cause);
        this.code = 422;
    }
}
