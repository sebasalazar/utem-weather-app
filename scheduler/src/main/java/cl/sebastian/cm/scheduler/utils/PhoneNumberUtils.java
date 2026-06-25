package cl.sebastian.cm.scheduler.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Clase utilitaria para la normalización de números de teléfono chilenos.
 * <p>
 * Proporciona métodos para limpiar, validar y convertir números telefónicos al
 * formato internacional estándar para Chile (código de país +56).
 * </p>
 * <p>
 * Esta clase es {@code final} y su constructor es privado, por lo que no puede
 * ser instanciada ni extendida. Todos sus métodos son estáticos.
 * </p>
 * <p>
 * <strong>Comportamiento esperado:</strong>
 * </p>
 * <ul>
 * <li>Elimina todos los caracteres no numéricos (espacios, paréntesis, guiones,
 * etc.).</li>
 * <li>Acepta números en diversos formatos:
 * <ul>
 * <li>Celular de 9 dígitos (ej. 912345678)</li>
 * <li>Celular con cero inicial (ej. 0912345678)</li>
 * <li>Celular con código de país (ej. 56912345678)</li>
 * <li>Números con formato libre (con o sin prefijos)</li>
 * </ul>
 * </li>
 * <li>Normaliza a un estándar de 9 dígitos comenzando con '9' (celular
 * chileno).</li>
 * <li>Concatena el código de país '+56' al resultado.</li>
 * <li>Retorna {@code 0L} si el número no es un celular chileno válido.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see StringUtils
 * @see NumberUtils
 */
public final class PhoneNumberUtils {

    /**
     * Constructor privado para impedir la instanciación de la clase.
     * <p>
     * Lanza {@link IllegalStateException} si alguien intenta crear una
     * instancia mediante reflexión.
     * </p>
     */
    private PhoneNumberUtils() {
        throw new IllegalStateException("Clase utilitaria");
    }

    /**
     * Normaliza un número de teléfono chileno al formato internacional.
     * <p>
     * Este método limpia el texto de entrada, eliminando todos los caracteres
     * no numéricos, y aplica las siguientes reglas según la longitud del
     * número:
     * </p>
     * <ul>
     * <li><strong>Longitud ≤ 9:</strong> Rellena con ceros a la izquierda hasta
     * alcanzar 9 dígitos (asume que es un número sin prefijo).</li>
     * <li><strong>Longitud = 10:</strong> Si comienza con '0', elimina el cero
     * inicial (asume formato de marcación nacional).</li>
     * <li><strong>Longitud = 11:</strong> Si comienza con '56', elimina el
     * código de país (asume formato internacional).</li>
     * <li><strong>Otras longitudes:</strong> No se procesan y retornan
     * {@code 0L}.</li>
     * </ul>
     * <p>
     * Posteriormente, valida que el resultado sea un celular chileno válido:
     * </p>
     * <ul>
     * <li>Debe tener exactamente 9 dígitos.</li>
     * <li>Debe comenzar con el dígito '9' (característica de los celulares
     * chilenos).</li>
     * </ul>
     * <p>
     * Si la validación es exitosa, se antepone el código de país '56' al número
     * y se convierte a {@code long}. En caso contrario, retorna {@code 0L}.
     * </p>
     * <p>
     * <strong>Ejemplos de entrada y salida:</strong>
     * </p>
     * <table border="1" summary="Ejemplos de normalización">
     * <tr><th>Entrada</th><th>Salida (long)</th><th>Descripción</th></tr>
     * <tr><td>"912345678"</td><td>56912345678</td><td>Formato estándar de 9
     * dígitos</td></tr>
     * <tr><td>"0912345678"</td><td>56912345678</td><td>Con cero
     * inicial</td></tr>
     * <tr><td>"56912345678"</td><td>56912345678</td><td>Con código de
     * país</td></tr>
     * <tr><td>"+56 9 1234 5678"</td><td>56912345678</td><td>Con formato libre
     * (espacios y símbolos)</td></tr>
     * <tr><td>"987654321"</td><td>56987654321</td><td>Otro celular
     * válido</td></tr>
     * <tr><td>"212345678"</td><td>0L</td><td>No comienza con 9 (no es
     * celular)</td></tr>
     * <tr><td>"123"</td><td>0L</td><td>Longitud insuficiente</td></tr>
     * <tr><td>null</td><td>0L</td><td>Entrada nula</td></tr>
     * </table>
     *
     * @param input el número de teléfono en formato libre (puede ser
     * {@code null} o vacío).
     * @return el número normalizado con código de país (+56) como {@code long},
     * o {@code 0L} si la normalización falla o el número no es un celular
     * chileno válido.
     * @see NumberUtils#toLong(String)
     * @see StringUtils#EMPTY
     */
    public static long normalizeChileanPhone(String input) {
        // Si la entrada es null o vacía, retorna 0L
        if (StringUtils.isEmpty(input)) {
            return 0L;
        }

        // Elimina todos los caracteres no numéricos
        String soloDigitos = input.replaceAll("\\D", "");
        int len = soloDigitos.length();

        String numeroBase = StringUtils.EMPTY;

        // Normaliza según la longitud del número
        if (len <= 9) {
            // Rellena con ceros a la izquierda hasta 9 dígitos
            numeroBase = String.format("%09d", NumberUtils.toLong(soloDigitos));
        } else if (len == 10) {
            // Si tiene cero inicial, lo elimina
            if (soloDigitos.startsWith("0")) {
                numeroBase = soloDigitos.substring(1);
            }
        } else if (len == 11) {
            // Si tiene código de país, lo elimina
            if (soloDigitos.startsWith("56")) {
                numeroBase = soloDigitos.substring(2);
            }
        }

        // Valida que el número base sea un celular chileno (9 dígitos y comienza con 9)
        if (numeroBase == null || numeroBase.length() != 9 || !numeroBase.startsWith("9")) {
            return 0L;
        }

        // Antepone el código de país +56 y convierte a long
        return NumberUtils.toLong("56" + numeroBase);
    }
}
