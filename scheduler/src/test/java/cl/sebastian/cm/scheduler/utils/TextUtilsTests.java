package cl.sebastian.cm.scheduler.utils;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Pruebas unitarias para la clase utilitaria {@link TextUtils}.
 * <p>
 * Verifica el correcto funcionamiento de los métodos de normalización,
 * conversión a mayúsculas/minúsculas, escape para logs y la constante
 * {@code UNKNOWN}.
 * </p>
 * <p>
 * Las pruebas cubren:
 * </p>
 * <ul>
 * <li>Normalización: trim, colapso de espacios y manejo de {@code null}.</li>
 * <li>Conversión a mayúsculas y minúsculas con normalización previa.</li>
 * <li>Escape para logs: manejo de {@code null}, saltos de línea, caracteres de
 * control, truncamiento por defecto y personalizado.</li>
 * <li>Valor de la constante {@code UNKNOWN}.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see TextUtils
 */
@DisplayName("TextUtils - Utilidades de manipulación de texto")
class TextUtilsTests {

    // -------------------------------------------------------------------------
    // Constantes de prueba (elimina strings mágicos dispersos)
    // -------------------------------------------------------------------------
    /**
     * Entrada con espacios al inicio y al final.
     */
    private static final String INPUT_WITH_LEADING_TRAILING_SPACES = "  Hola Mundo  ";

    /**
     * Entrada con múltiples espacios internos.
     */
    private static final String INPUT_WITH_MULTIPLE_INTERNAL_SPACES = "Hola   Mundo   Cruel";

    /**
     * Entrada combinada con espacios internos y externos.
     */
    private static final String INPUT_COMBINED_SPACES = "  Farmacia   de   turno  ";

    /**
     * Entrada en minúsculas con espacios para probar upper().
     */
    private static final String INPUT_LOWERCASE_WITH_SPACES = "  farmacia cruz blanca  ";

    /**
     * Entrada en mayúsculas con espacios para probar lower().
     */
    private static final String INPUT_UPPERCASE_WITH_SPACES = "  FARMACIA AHUMADA  ";

    /**
     * Entrada con salto de línea para probar escape.
     */
    private static final String INPUT_WITH_NEWLINE = "Línea 1\nLínea 2";

    /**
     * Entrada con carácter de control para probar escape.
     */
    private static final String INPUT_WITH_CONTROL_CHAR = "Texto\u0001Peligroso";

    /**
     * Cadena larga de 600 caracteres para probar truncamiento por defecto.
     */
    private static final String LONG_STRING_600_CHARS = "a".repeat(600);

    /**
     * Cadena de 100 caracteres para probar truncamiento personalizado.
     */
    private static final String LONG_STRING_100_CHARS = "a".repeat(100);

    /**
     * Límite de truncamiento por defecto (512).
     */
    private static final int DEFAULT_TRUNCATION_LIMIT = 512;

    /**
     * Límite de truncamiento personalizado (64).
     */
    private static final int CUSTOM_TRUNCATION_LIMIT = 64;

    /**
     * Valor esperado para la constante UNKNOWN.
     */
    private static final String EXPECTED_UNKNOWN_VALUE = "(Sin especificar)";

    // -------------------------------------------------------------------------
    // Pruebas Parametrizadas: normalize()
    // -------------------------------------------------------------------------
    /**
     * Prueba parametrizada que verifica que {@link TextUtils#normalize(String)}
     * recorta espacios al inicio/final y colapsa múltiples espacios internos a
     * uno solo. También maneja correctamente la entrada {@code null}.
     *
     * @param input la cadena de entrada.
     * @param expectedOutput la salida esperada tras la normalización.
     */
    @ParameterizedTest(name = "should normalize ''{0}'' to ''{1}''")
    @MethodSource("provideNormalizeCases")
    @DisplayName("normalize: trim, colapso de espacios y manejo de null")
    void shouldNormalizeInputByTrimmingAndCollapsingSpaces(String input, String expectedOutput) {
        // Arrange
        // (No requiere configuración adicional, los datos vienen del proveedor)

        // Act
        String result = TextUtils.normalize(input);

        // Assert
        Assertions.assertEquals(expectedOutput, result,
                String.format("normalize('%s') debe retornar '%s'", input, expectedOutput));
    }

    /**
     * Proveedor de argumentos para la prueba parametrizada de normalización.
     *
     * @return un flujo de pares (entrada, salida esperada).
     */
    static Stream<Arguments> provideNormalizeCases() {
        return Stream.of(
                Arguments.of(null, ""),
                Arguments.of(INPUT_WITH_LEADING_TRAILING_SPACES, "Hola Mundo"),
                Arguments.of(INPUT_WITH_MULTIPLE_INTERNAL_SPACES, "Hola Mundo Cruel"),
                Arguments.of(INPUT_COMBINED_SPACES, "Farmacia de turno")
        );
    }

    // -------------------------------------------------------------------------
    // Pruebas Unitarias: upper() y lower()
    // -------------------------------------------------------------------------
    /**
     * Verifica que {@link TextUtils#upper(String)} normalice la cadena (trim y
     * colapso de espacios) y luego la convierta a mayúsculas.
     */
    @Test
    @DisplayName("upper: normaliza y convierte a mayúsculas")
    void shouldNormalizeAndConvertToUpperCase() {
        // Arrange
        String input = INPUT_LOWERCASE_WITH_SPACES;

        // Act
        String result = TextUtils.upper(input);

        // Assert
        Assertions.assertEquals("FARMACIA CRUZ BLANCA", result,
                "upper() debe normalizar espacios y convertir a mayúsculas");
    }

    /**
     * Verifica que {@link TextUtils#upper(String)} retorne cadena vacía cuando
     * la entrada es {@code null}.
     */
    @Test
    @DisplayName("upper: retorna cadena vacía para null")
    void shouldReturnEmptyStringWhenInputIsNullForUpper() {
        // Arrange
        // (No requiere configuración; null es el SUT)

        // Act
        String result = TextUtils.upper(null);

        // Assert
        Assertions.assertEquals("", result,
                "upper(null) debe retornar cadena vacía, no null");
    }

    /**
     * Verifica que {@link TextUtils#lower(String)} normalice la cadena (trim y
     * colapso de espacios) y luego la convierta a minúsculas.
     */
    @Test
    @DisplayName("lower: normaliza y convierte a minúsculas")
    void shouldNormalizeAndConvertToLowerCase() {
        // Arrange
        String input = INPUT_UPPERCASE_WITH_SPACES;

        // Act
        String result = TextUtils.lower(input);

        // Assert
        Assertions.assertEquals("farmacia ahumada", result,
                "lower() debe normalizar espacios y convertir a minúsculas");
    }

    /**
     * Verifica que {@link TextUtils#lower(String)} retorne cadena vacía cuando
     * la entrada es {@code null}.
     */
    @Test
    @DisplayName("lower: retorna cadena vacía para null")
    void shouldReturnEmptyStringWhenInputIsNullForLower() {
        // Arrange
        // (No requiere configuración; null es el SUT)

        // Act
        String result = TextUtils.lower(null);

        // Assert
        Assertions.assertEquals("", result,
                "lower(null) debe retornar cadena vacía, no null");
    }

    // -------------------------------------------------------------------------
    // Pruebas Unitarias: escapeForLog()
    // -------------------------------------------------------------------------
    /**
     * Verifica que {@link TextUtils#escapeForLog(String)} retorne cadena vacía
     * cuando la entrada es {@code null}.
     */
    @Test
    @DisplayName("escapeForLog: retorna cadena vacía para null")
    void shouldReturnEmptyStringWhenInputIsNullForEscapeForLog() {
        // Arrange
        // (No requiere configuración; null es el SUT)

        // Act
        String result = TextUtils.escapeForLog(null);

        // Assert
        Assertions.assertNotNull(result, "escapeForLog(null) no debe retornar null");
        Assertions.assertEquals("", result,
                "escapeForLog(null) debe retornar cadena vacía");
    }

    /**
     * Verifica que {@link TextUtils#escapeForLog(String)} reemplace los saltos
     * de línea por su representación escapada (por ejemplo, {@code \n}).
     */
    @Test
    @DisplayName("escapeForLog: reemplaza saltos de línea por representación escapada")
    void shouldEscapeNewlineCharacters() {
        // Arrange
        String input = INPUT_WITH_NEWLINE;

        // Act
        String result = TextUtils.escapeForLog(input);

        // Assert
        Assertions.assertFalse(result.contains("\n"),
                "escapeForLog() debe eliminar o reemplazar saltos de línea reales");
    }

    /**
     * Verifica que {@link TextUtils#escapeForLog(String)} elimine o reemplace
     * caracteres de control no imprimibles (como {@code \u0001}).
     */
    @Test
    @DisplayName("escapeForLog: elimina caracteres de control no imprimibles")
    void shouldRemoveNonPrintableControlCharacters() {
        // Arrange
        String input = INPUT_WITH_CONTROL_CHAR;

        // Act
        String result = TextUtils.escapeForLog(input);

        // Assert
        Assertions.assertFalse(result.contains("\u0001"),
                "escapeForLog() debe eliminar caracteres de control como \\u0001");
        Assertions.assertTrue(result.contains("Texto") && result.contains("Peligroso"),
                "escapeForLog() debe preservar el contenido textual legible");
    }

    /**
     * Verifica que {@link TextUtils#escapeForLog(String)} trunque la cadena a
     * 512 caracteres por defecto y añada un indicador visual ("...").
     */
    @Test
    @DisplayName("escapeForLog: trunca a 512 caracteres por defecto con indicador visual")
    void shouldTruncateToDefaultLimitWithVisualIndicator() {
        // Arrange
        String input = LONG_STRING_600_CHARS;

        // Act
        String result = TextUtils.escapeForLog(input);

        // Assert
        Assertions.assertTrue(result.length() <= DEFAULT_TRUNCATION_LIMIT + 3,
                String.format("escapeForLog() debe truncar a aproximadamente %d caracteres", DEFAULT_TRUNCATION_LIMIT));
        Assertions.assertTrue(result.contains("..."),
                "escapeForLog() debe añadir '...' para indicar truncamiento");
    }

    /**
     * Verifica que {@link TextUtils#escapeForLog(String, int)} respete el
     * límite de truncamiento personalizado proporcionado.
     */
    @Test
    @DisplayName("escapeForLog: respeta límite personalizado de truncamiento")
    void shouldRespectCustomTruncationLimit() {
        // Arrange
        String input = LONG_STRING_100_CHARS;
        int customLimit = CUSTOM_TRUNCATION_LIMIT;

        // Act
        String result = TextUtils.escapeForLog(input, customLimit);

        // Assert
        Assertions.assertTrue(result.length() <= customLimit + 5,
                String.format("escapeForLog() con límite %d debe truncar apropiadamente", customLimit));
    }

    // -------------------------------------------------------------------------
    // Pruebas Unitarias: Constantes
    // -------------------------------------------------------------------------
    /**
     * Verifica que la constante {@link TextUtils#UNKNOWN} tenga el valor
     * documentado {@code "(Sin especificar)"}.
     */
    @Test
    @DisplayName("UNKNOWN: contiene el valor por defecto documentado")
    void shouldContainCorrectDefaultValueForUnknownConstant() {
        // Arrange & Act & Assert
        Assertions.assertEquals(EXPECTED_UNKNOWN_VALUE, TextUtils.UNKNOWN,
                "La constante UNKNOWN debe contener '(Sin especificar)' como valor por defecto");
    }
}
