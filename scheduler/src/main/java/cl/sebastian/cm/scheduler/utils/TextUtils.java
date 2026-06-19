package cl.sebastian.cm.scheduler.utils;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Clase utilitaria para operaciones comunes de manipulación de texto.
 * <p>
 * Proporciona métodos para normalizar cadenas (eliminación de espacios, recorte
 * y colapso de espacios en blanco), conversión a mayúsculas/minúsculas con el
 * locale chileno (es-CL), y escape seguro de texto para su uso en logs,
 * eliminando caracteres de control y aplicando un límite de longitud.
 * </p>
 * <p>
 * Esta clase es {@code final} y su constructor es privado, por lo que no puede
 * ser instanciada ni extendida. Todos sus métodos son estáticos.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see StringUtils
 * @see StringEscapeUtils
 */
public final class TextUtils {

    /**
     * Texto por defecto para representar valores no especificados o
     * desconocidos.
     */
    public static final String UNKNOWN = "(Sin especificar)";

    /**
     * Locale para Chile (español, Chile), utilizado en las conversiones de
     * mayúsculas y minúsculas para respetar las reglas lingüísticas locales.
     */
    private static final Locale CL = Locale.of("es", "CL");

    /**
     * Constructor privado para impedir la instanciación de la clase.
     * <p>
     * Lanza {@link IllegalStateException} si alguien intenta crear una
     * instancia mediante reflexión.
     * </p>
     */
    private TextUtils() {
        throw new IllegalStateException("Clase utilitaria");
    }

    /**
     * Normaliza una cadena de texto eliminando espacios en blanco al inicio y
     * al final, y colapsando los espacios internos a un único espacio.
     * <p>
     * Si el texto de entrada es {@code null}, se devuelve una cadena vacía.
     * </p>
     *
     * @param text el texto a normalizar (puede ser {@code null}).
     * @return el texto normalizado, o una cadena vacía si {@code text} es
     * {@code null}.
     * @see StringUtils#normalizeSpace(String)
     * @see StringUtils#trimToEmpty(String)
     */
    public static String normalize(final String text) {
        return StringUtils.normalizeSpace(StringUtils.trimToEmpty(text));
    }

    /**
     * Convierte el texto a mayúsculas después de normalizarlo.
     * <p>
     * La conversión respeta el locale chileno ({@code es-CL}) para aplicar las
     * reglas de mayusculización propias del idioma español.
     * </p>
     *
     * @param text el texto a convertir (puede ser {@code null}).
     * @return el texto normalizado y en mayúsculas, o una cadena vacía si
     * {@code text} es {@code null}.
     * @see #normalize(String)
     * @see StringUtils#upperCase(String, Locale)
     */
    public static String upper(final String text) {
        return StringUtils.upperCase(normalize(text), CL);
    }

    /**
     * Convierte el texto a minúsculas después de normalizarlo.
     * <p>
     * La conversión respeta el locale chileno ({@code es-CL}) para aplicar las
     * reglas de minusculización propias del idioma español.
     * </p>
     *
     * @param text el texto a convertir (puede ser {@code null}).
     * @return el texto normalizado y en minúsculas, o una cadena vacía si
     * {@code text} es {@code null}.
     * @see #normalize(String)
     * @see StringUtils#lowerCase(String, Locale)
     */
    public static String lower(final String text) {
        return StringUtils.lowerCase(normalize(text), CL);
    }

    /**
     * Escapa texto para uso seguro en logs.
     * <p>
     * Este método realiza las siguientes operaciones en el texto de entrada:
     * </p>
     * <ul>
     * <li>Normaliza el texto (recorte y colapso de espacios).</li>
     * <li>Reemplaza todos los caracteres de control (excepto CR, LF y TAB) por
     * un asterisco (*).</li>
     * <li>Escapa el resultado como una cadena JSON válida, convirtiendo saltos
     * de línea, retornos, tabuladores y caracteres especiales en secuencias de
     * escape (\\n, \\r, \\t, etc.).</li>
     * <li>Escapa explícitamente los separadores de línea/parágrafo Unicode
     * (U+2028 y U+2029) a las secuencias {@code \u2028} y {@code \u2029}.</li>
     * <li>Aplica un límite máximo de longitud de 512 caracteres; si se supera,
     * se trunca añadiendo un sufijo informativo con el número de caracteres
     * omitidos.</li>
     * </ul>
     *
     * @param rawText texto original (puede ser {@code null}).
     * @return cadena segura y escapada para log, nunca {@code null}.
     * @see #escapeForLog(String, int)
     */
    public static String escapeForLog(final String rawText) {
        return escapeForLog(rawText, 512);
    }

    /**
     * Escapa texto para uso seguro en logs, con límite de longitud
     * personalizable.
     * <p>
     * Este método realiza las siguientes operaciones en el texto de entrada:
     * </p>
     * <ul>
     * <li>Normaliza el texto (recorte y colapso de espacios).</li>
     * <li>Reemplaza todos los caracteres de control (excepto CR, LF y TAB) por
     * un asterisco (*).</li>
     * <li>Escapa el resultado como una cadena JSON válida, convirtiendo saltos
     * de línea, retornos, tabuladores y caracteres especiales en secuencias de
     * escape (\\n, \\r, \\t, etc.).</li>
     * <li>Escapa explícitamente los separadores de línea/parágrafo Unicode
     * (U+2028 y U+2029) a las secuencias {@code \u2028} y {@code \u2029}.</li>
     * <li>Trunca el resultado si supera la longitud máxima especificada,
     * añadiendo un sufijo informativo con el número de caracteres
     * omitidos.</li>
     * </ul>
     * <p>
     * La longitud mínima efectiva es de 64 caracteres (si {@code maxLength} es
     * menor, se ajusta internamente a este valor) para garantizar un margen
     * mínimo informativo.
     * </p>
     *
     * @param rawText texto original (puede ser {@code null}).
     * @param maxLength longitud máxima deseada para el resultado (debe ser ≥
     * 64; si es menor, se ajusta automáticamente a 64).
     * @return cadena segura y escapada para log, nunca {@code null}.
     * @throws IllegalArgumentException si {@code maxLength} es menor que 1 (no
     * se aplica, ya que se fuerza un mínimo de 64).
     * @see StringEscapeUtils#escapeJson(String)
     */
    public static String escapeForLog(final String rawText, final int maxLength) {
        final String nonNullInput = normalize(rawText);

        // 1) Limpia controles “peligrosos” excepto CR/LF/TAB (se escaparán luego).
        final String cleanedControls = nonNullInput.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "*");

        // 2) Escapa como literal Java (convierte \n, \r, \t, comillas, etc. a secuencias visibles).
        String escaped = StringEscapeUtils.escapeJson(cleanedControls);

        // 3) Endurece separadores de línea/párrafo (no siempre escapados según fuente).
        escaped = escaped
                .replace("\u2028", "\\u2028")
                .replace("\u2029", "\\u2029");

        // 4) Límite de longitud seguro
        final int safeMax = Math.max(64, maxLength);
        if (escaped.length() > safeMax) {
            final String prefix = escaped.substring(0, safeMax);
            final int omitted = escaped.length() - safeMax;
            escaped = prefix + " ... [+" + omitted + " chars]";
        }

        return StringUtils.abbreviate(escaped, safeMax);
    }
}
