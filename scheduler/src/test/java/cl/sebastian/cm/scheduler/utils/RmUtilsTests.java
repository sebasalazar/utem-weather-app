package cl.sebastian.cm.scheduler.utils;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Pruebas unitarias para la clase utilitaria {@link RmUtils}.
 * <p>
 * Verifica el correcto funcionamiento de los métodos que manejan valores
 * numéricos provenientes de RedMeteo, específicamente {@code getValue(Double)}
 * y {@code getValue(Long)}. Ambos métodos deben retornar el valor de entrada si
 * no es {@code null}, o el valor por defecto correspondiente
 * ({@link RmUtils#DEFAULT_DOUBLE} o {@link RmUtils#DEFAULT_LONG}) en caso
 * contrario.
 * </p>
 * <p>
 * <strong>Cobertura de escenarios:</strong>
 * </p>
 * <ul>
 * <li>Valores {@code Double}: normales, cero, negativos, muy grandes y
 * {@code null}.</li>
 * <li>Valores {@code Long}: normales, cero, negativos, muy grandes y
 * {@code null}.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see RmUtils
 */
@DisplayName("RmUtils - Utilidades para valores numéricos de RedMeteo")
class RmUtilsTests {

    // -------------------------------------------------------------------------
    // Constantes de prueba (elimina números mágicos dispersos)
    // -------------------------------------------------------------------------
    /**
     * Valor Double normal de prueba.
     */
    private static final Double SAMPLE_DOUBLE_NORMAL = 25.5;

    /**
     * Valor Double cero de prueba.
     */
    private static final Double SAMPLE_DOUBLE_ZERO = 0.0;

    /**
     * Valor Double negativo de prueba.
     */
    private static final Double SAMPLE_DOUBLE_NEGATIVE = -15.5;

    /**
     * Valor Double muy grande de prueba.
     */
    private static final Double SAMPLE_DOUBLE_VERY_LARGE = 1_000_000.123;

    /**
     * Valor Long normal de prueba.
     */
    private static final Long SAMPLE_LONG_NORMAL = 45L;

    /**
     * Valor Long cero de prueba.
     */
    private static final Long SAMPLE_LONG_ZERO = 0L;

    /**
     * Valor Long negativo de prueba.
     */
    private static final Long SAMPLE_LONG_NEGATIVE = -100L;

    /**
     * Valor Long muy grande de prueba.
     */
    private static final Long SAMPLE_LONG_VERY_LARGE = 9_999_999_999L;

    // -------------------------------------------------------------------------
    // Pruebas Parametrizadas: getValue(Double)
    // -------------------------------------------------------------------------
    /**
     * Prueba parametrizada que verifica que {@link RmUtils#getValue(Double)}
     * retorna el valor de entrada cuando no es {@code null}, o el valor
     * {@link RmUtils#DEFAULT_DOUBLE} (0.0) cuando es {@code null}.
     *
     * @param input el valor Double de entrada (puede ser {@code null}).
     * @param expectedOutput el resultado esperado.
     */
    @ParameterizedTest(name = "should return {1} when Double input is {0}")
    @MethodSource("provideDoubleCases")
    @DisplayName("getValue(Double): retorna el valor de entrada o DEFAULT_DOUBLE si es null")
    void shouldReturnInputValueOrDefaultForDouble(Double input, double expectedOutput) {
        // Arrange
        // (No requiere configuración adicional, los datos vienen del proveedor)

        // Act
        double result = RmUtils.getValue(input);

        // Assert
        Assertions.assertEquals(expectedOutput, result,
                String.format("getValue(%s) debe retornar %f", input, expectedOutput));
    }

    /**
     * Proveedor de argumentos para la prueba parametrizada de {@code Double}.
     *
     * @return un flujo de pares (entrada Double, salida esperada).
     */
    static Stream<Arguments> provideDoubleCases() {
        return Stream.of(
                Arguments.of(SAMPLE_DOUBLE_NORMAL, SAMPLE_DOUBLE_NORMAL),
                Arguments.of(null, RmUtils.DEFAULT_DOUBLE),
                Arguments.of(SAMPLE_DOUBLE_ZERO, SAMPLE_DOUBLE_ZERO),
                Arguments.of(SAMPLE_DOUBLE_NEGATIVE, SAMPLE_DOUBLE_NEGATIVE),
                Arguments.of(SAMPLE_DOUBLE_VERY_LARGE, SAMPLE_DOUBLE_VERY_LARGE)
        );
    }

    // -------------------------------------------------------------------------
    // Pruebas Parametrizadas: getValue(Long)
    // -------------------------------------------------------------------------
    /**
     * Prueba parametrizada que verifica que {@link RmUtils#getValue(Long)}
     * retorna el valor de entrada cuando no es {@code null}, o el valor
     * {@link RmUtils#DEFAULT_LONG} (0L) cuando es {@code null}.
     *
     * @param input el valor Long de entrada (puede ser {@code null}).
     * @param expectedOutput el resultado esperado.
     */
    @ParameterizedTest(name = "should return {1} when Long input is {0}")
    @MethodSource("provideLongCases")
    @DisplayName("getValue(Long): retorna el valor de entrada o DEFAULT_LONG si es null")
    void shouldReturnInputValueOrDefaultForLong(Long input, long expectedOutput) {
        // Arrange
        // (No requiere configuración adicional, los datos vienen del proveedor)

        // Act
        long result = RmUtils.getValue(input);

        // Assert
        Assertions.assertEquals(expectedOutput, result,
                String.format("getValue(%s) debe retornar %d", input, expectedOutput));
    }

    /**
     * Proveedor de argumentos para la prueba parametrizada de {@code Long}.
     *
     * @return un flujo de pares (entrada Long, salida esperada).
     */
    static Stream<Arguments> provideLongCases() {
        return Stream.of(
                Arguments.of(SAMPLE_LONG_NORMAL, SAMPLE_LONG_NORMAL),
                Arguments.of(null, RmUtils.DEFAULT_LONG),
                Arguments.of(SAMPLE_LONG_ZERO, SAMPLE_LONG_ZERO),
                Arguments.of(SAMPLE_LONG_NEGATIVE, SAMPLE_LONG_NEGATIVE),
                Arguments.of(SAMPLE_LONG_VERY_LARGE, SAMPLE_LONG_VERY_LARGE)
        );
    }
}
