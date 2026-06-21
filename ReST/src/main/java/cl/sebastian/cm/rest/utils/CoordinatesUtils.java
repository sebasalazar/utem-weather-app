package cl.sebastian.cm.rest.utils;

/**
 * Clase utilitaria para validación de coordenadas geográficas en el sistema de
 * referencia WGS84.
 * <p>
 * Proporciona un método estático para verificar que los valores de latitud y
 * longitud se encuentren dentro de los rangos válidos definidos por el estándar
 * WGS84, que es el sistema de coordenadas utilizado por GPS y la mayoría de
 * sistemas de información geográfica.
 * </p>
 * <p>
 * Esta clase es {@code final} y su constructor es privado, por lo que no puede
 * ser instanciada ni extendida. Todos sus métodos son estáticos.
 * </p>
 *
 * <h2>Rangos de validez (WGS84)</h2>
 * <ul>
 * <li><strong>Latitud:</strong> entre {@code -90.0} y {@code 90.0} grados
 * decimales (inclusive).</li>
 * <li><strong>Longitud:</strong> entre {@code -180.0} y {@code 180.0} grados
 * decimales (inclusive).</li>
 * </ul>
 *
 * <h2>Uso típico</h2>
 * <pre>
 * if (CoordinatesUtils.areValid(-33.456, -70.654)) {
 *     // coordenadas válidas, proceder con la operación
 * }
 * </pre>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see <a href="https://en.wikipedia.org/wiki/World_Geodetic_System">WGS84</a>
 */
public final class CoordinatesUtils {

    /**
     * Límite inferior de latitud válida (WGS84).
     */
    private static final double MIN_LATITUDE = -90.0;

    /**
     * Límite superior de latitud válida (WGS84).
     */
    private static final double MAX_LATITUDE = 90.0;

    /**
     * Límite inferior de longitud válida (WGS84).
     */
    private static final double MIN_LONGITUDE = -180.0;

    /**
     * Límite superior de longitud válida (WGS84).
     */
    private static final double MAX_LONGITUDE = 180.0;

    /**
     * Constructor privado para impedir la instanciación de la clase.
     * <p>
     * Al ser una clase utilitaria, no se deben crear instancias. Este
     * constructor lanza {@link IllegalStateException} si es invocado por
     * reflexión, reforzando el patrón de clase no instanciable.
     * </p>
     */
    private CoordinatesUtils() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable");
    }

    /**
     * Valida que las coordenadas estén dentro de los rangos geográficos
     * permitidos por el sistema WGS84.
     * <p>
     * Una coordenada es válida si se cumplen ambas condiciones:
     * </p>
     * <ul>
     * <li>Latitud entre {@code -90.0} y {@code 90.0} (inclusive).</li>
     * <li>Longitud entre {@code -180.0} y {@code 180.0} (inclusive).</li>
     * </ul>
     * <p>
     * Esta validación es fundamental para garantizar que las coordenadas sean
     * geográficamente plausibles antes de realizar operaciones espaciales como
     * búsquedas por distancia o cálculos de ruta.
     * </p>
     *
     * @param latitude latitud a validar, en grados decimales.
     * @param longitude longitud a validar, en grados decimales.
     * @return {@code true} si ambas coordenadas están dentro de los rangos
     * válidos, {@code false} en caso contrario.
     * @see
     * <a href="https://en.wikipedia.org/wiki/World_Geodetic_System">WGS84</a>
     */
    public static boolean areValid(
            final double latitude,
            final double longitude) {
        return latitude >= MIN_LATITUDE
                && latitude <= MAX_LATITUDE
                && longitude >= MIN_LONGITUDE
                && longitude <= MAX_LONGITUDE;
    }
}
