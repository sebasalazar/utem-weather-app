package cl.sebastian.cm.scheduler.utils;

/**
 * Utilidades para obtener valores primitivos a partir de wrappers manejando
 * {@code null} con un valor por defecto. Se usa con los objetos de RedMeteo.
 * <p>
 * La idea es evitar {@link NullPointerException} cuando se trabaja con
 * {@link Double} y {@link Long} que pueden venir nulos, devolviendo un
 * primitivo con un “sentinel” configurable (aquí: -1.0 y -1L).
 * </p>
 *
 * <h2>Advertencia</h2>
 * Usar -32768 como valor por defecto es una convención: solo es seguro si en el
 * dominio de datos ese valor no es válido. Si -32768 puede ser un valor
 * legítimo, hay que considerar usar otra estrategia (por ejemplo, valores por
 * defecto distintos, constantes de dominio o {@link java.util.Optional}).
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
public final class RmUtils {

    /**
     * Valor por defecto para {@code double} cuando el wrapper es {@code null}.
     */
    public static final double DEFAULT_DOUBLE = Short.MIN_VALUE;

    /**
     * Valor por defecto para {@code long} cuando el wrapper es {@code null}.
     */
    public static final long DEFAULT_LONG = Short.MIN_VALUE;

    /**
     * Clase de utilidades: evitar instanciación.
     */
    private RmUtils() {
        throw new IllegalStateException("Clase utilitaria no instanciable");
    }

    /**
     * Devuelve el valor primitivo de un {@link Double}, o
     * {@link #DEFAULT_DOUBLE} si el parámetro es {@code null}.
     *
     * @param value valor a desempaquetar; puede ser {@code null}
     * @return el valor como primitivo, o {@link #DEFAULT_DOUBLE} si es
     * {@code null}
     */
    public static double getValue(final Double value) {
        return (value != null) ? value : DEFAULT_DOUBLE;
    }

    /**
     * Devuelve el valor primitivo de un {@link Long}, o {@link #DEFAULT_LONG}
     * si el parámetro es {@code null}.
     *
     * @param value valor a desempaquetar; puede ser {@code null}
     * @return el valor como primitivo, o {@link #DEFAULT_LONG} si es
     * {@code null}
     */
    public static long getValue(final Long value) {
        return (value != null) ? value : DEFAULT_LONG;
    }
}
