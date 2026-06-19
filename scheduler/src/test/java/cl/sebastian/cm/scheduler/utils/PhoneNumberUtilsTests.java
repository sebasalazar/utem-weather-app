package cl.sebastian.cm.scheduler.utils;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Pruebas unitarias para la clase utilitaria {@link PhoneNumberUtils}.
 * <p>
 * Verifica el correcto funcionamiento del método
 * {@link PhoneNumberUtils#normalizeChileanPhone(String)} que normaliza
 * números telefónicos chilenos al formato internacional estándar (+569XXXXXXXX).
 * </p>
 * <p>
 * Las pruebas cubren:
 * </p>
 * <ul>
 *   <li><strong>Casos válidos:</strong> números en distintos formatos (9 dígitos,
 *       con cero inicial, con código de país, con espacios, guiones, paréntesis)
 *       que deben normalizarse correctamente.</li>
 *   <li><strong>Casos inválidos:</strong> entradas nulas, vacías, números fijos,
 *       demasiado cortos, con longitud inválida o que no comienzan con 9; todos
 *       deben retornar {@code 0L}.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see PhoneNumberUtils
 */
@DisplayName("PhoneNumberUtils - Normalización de números telefónicos chilenos")
class PhoneNumberUtilsTests {

    // -------------------------------------------------------------------------
    // Constantes de prueba (elimina números mágicos y strings dispersos)
    // -------------------------------------------------------------------------

    // Casos válidos: entradas y salidas esperadas

    /** Teléfono celular de 9 dígitos. */
    private static final String INPUT_NINE_DIGIT_CELL = "912345678";

    /** Teléfono celular con cero inicial. */
    private static final String INPUT_WITH_LEADING_ZERO = "0912345678";

    /** Teléfono celular con código de país explícito. */
    private static final String INPUT_WITH_COUNTRY_CODE = "56912345678";

    /** Teléfono celular con formato libre (espacios y símbolos). */
    private static final String INPUT_FORMATTED_WITH_SPACES = "+56 9 1234 5678";

    /** Teléfono celular con guiones separadores. */
    private static final String INPUT_WITH_DASHES = "9-1234-5678";

    /** Teléfono celular con paréntesis en el primer dígito. */
    private static final String INPUT_WITH_PARENTHESES = "(9) 1234-5678";

    /** Otro teléfono celular válido (diferente suscriptor). */
    private static final String INPUT_ANOTHER_VALID_CELL = "987654321";

    /** Teléfono celular con código de país y formato con espacios. */
    private static final String INPUT_WITH_COUNTRY_CODE_AND_SPACES = "+56 987 654 321";

    /** Resultado esperado para el primer número normalizado. */
    private static final Long EXPECTED_NORMALIZED_1 = 56912345678L;

    /** Resultado esperado para el segundo número normalizado. */
    private static final Long EXPECTED_NORMALIZED_2 = 56987654321L;

    // Casos inválidos: entradas que deben retornar 0L

    /** Entrada {@code null}. */
    private static final String INPUT_NULL = null;

    /** Cadena vacía. */
    private static final String INPUT_EMPTY = "";

    /** Número que no comienza con 9 (no es celular). */
    private static final String INPUT_NON_CELLULAR = "212345678";

    /** Número de teléfono fijo (comienza con 2). */
    private static final String INPUT_FIXED_LINE = "227777777";

    /** Número demasiado corto. */
    private static final String INPUT_TOO_SHORT = "123";

    /** Número que después de rellenar no comienza con 9. */
    private static final String INPUT_PADDED_BUT_INVALID = "9123";

    /** Número con longitud inválida después de procesar. */
    private static final String INPUT_INVALID_LENGTH = "123456789012";

    /** Valor esperado para todos los casos inválidos. */
    private static final Long EXPECTED_INVALID = 0L;

    // -------------------------------------------------------------------------
    // Pruebas Parametrizadas: Casos Válidos
    // -------------------------------------------------------------------------

    /**
     * Prueba parametrizada que verifica que {@link PhoneNumberUtils#normalizeChileanPhone(String)}
     * normaliza correctamente números telefónicos chilenos en diversos formatos
     * válidos, retornando siempre el estándar internacional {@code 569XXXXXXXX}.
     *
     * @param input          la cadena de entrada (formato libre).
     * @param expectedOutput el resultado esperado (long).
     */
    @ParameterizedTest(name = "should normalize ''{0}'' to {1}")
    @MethodSource("provideValidPhoneNumbers")
    @DisplayName("normalizeChileanPhone: normaliza formatos válidos al estándar internacional 569XXXXXXXX")
    void shouldNormalizeValidChileanPhoneNumbers(String input, Long expectedOutput) {
        // Arrange
        // (No requiere configuración adicional, los datos vienen del proveedor)

        // Act
        long result = PhoneNumberUtils.normalizeChileanPhone(input);

        // Assert
        Assertions.assertEquals(expectedOutput, result,
            String.format("normalizeChileanPhone('%s') debe retornar %d", input, expectedOutput));
    }

    /**
     * Proveedor de argumentos para los casos válidos.
     *
     * @return un flujo de pares (entrada, salida esperada).
     */
    static Stream<Arguments> provideValidPhoneNumbers() {
        return Stream.of(
            // Formato básico de 9 dígitos
            Arguments.of(INPUT_NINE_DIGIT_CELL, EXPECTED_NORMALIZED_1),

            // Con cero inicial (formato antiguo)
            Arguments.of(INPUT_WITH_LEADING_ZERO, EXPECTED_NORMALIZED_1),

            // Con código de país explícito
            Arguments.of(INPUT_WITH_COUNTRY_CODE, EXPECTED_NORMALIZED_1),

            // Con formato libre (espacios y símbolos)
            Arguments.of(INPUT_FORMATTED_WITH_SPACES, EXPECTED_NORMALIZED_1),

            // Con guiones separadores
            Arguments.of(INPUT_WITH_DASHES, EXPECTED_NORMALIZED_1),

            // Con paréntesis en el primer dígito
            Arguments.of(INPUT_WITH_PARENTHESES, EXPECTED_NORMALIZED_1),

            // Otro número celular válido (diferente suscriptor)
            Arguments.of(INPUT_ANOTHER_VALID_CELL, EXPECTED_NORMALIZED_2),

            // Con código de país y formato con espacios
            Arguments.of(INPUT_WITH_COUNTRY_CODE_AND_SPACES, EXPECTED_NORMALIZED_2)
        );
    }

    // -------------------------------------------------------------------------
    // Pruebas Parametrizadas: Casos Inválidos
    // -------------------------------------------------------------------------

    /**
     * Prueba parametrizada que verifica que {@link PhoneNumberUtils#normalizeChileanPhone(String)}
     * rechaza entradas inválidas (nulas, vacías, números fijos, formatos incorrectos)
     * retornando {@code 0L}.
     *
     * @param input          la cadena de entrada (formato libre).
     * @param expectedOutput el resultado esperado (siempre 0L).
     */
    @ParameterizedTest(name = "should reject ''{0}'' and return 0")
    @MethodSource("provideInvalidPhoneNumbers")
    @DisplayName("normalizeChileanPhone: rechaza entradas inválidas retornando 0L")
    void shouldRejectInvalidPhoneNumbersAndReturnZero(String input, Long expectedOutput) {
        // Arrange
        // (No requiere configuración adicional, los datos vienen del proveedor)

        // Act
        long result = PhoneNumberUtils.normalizeChileanPhone(input);

        // Assert
        Assertions.assertEquals(expectedOutput, result,
            String.format("normalizeChileanPhone('%s') debe retornar 0L (número inválido)", input));
    }

    /**
     * Proveedor de argumentos para los casos inválidos.
     *
     * @return un flujo de pares (entrada, salida esperada, siempre 0L).
     */
    static Stream<Arguments> provideInvalidPhoneNumbers() {
        return Stream.of(
            // Entrada null
            Arguments.of(INPUT_NULL, EXPECTED_INVALID),

            // Cadena vacía
            Arguments.of(INPUT_EMPTY, EXPECTED_INVALID),

            // Número que no comienza con 9 (no es celular)
            Arguments.of(INPUT_NON_CELLULAR, EXPECTED_INVALID),

            // Número de teléfono fijo (comienza con 2)
            Arguments.of(INPUT_FIXED_LINE, EXPECTED_INVALID),

            // Número demasiado corto
            Arguments.of(INPUT_TOO_SHORT, EXPECTED_INVALID),

            // Número que después de rellenar no comienza con 9
            Arguments.of(INPUT_PADDED_BUT_INVALID, EXPECTED_INVALID),

            // Número con longitud inválida después de procesar
            Arguments.of(INPUT_INVALID_LENGTH, EXPECTED_INVALID)
        );
    }
}