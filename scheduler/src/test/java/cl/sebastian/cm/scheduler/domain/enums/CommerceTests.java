package cl.sebastian.cm.scheduler.domain.enums;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Pruebas unitarias para la enumeración {@link Commerce}.
 * <p>
 * Verifica el correcto funcionamiento de los métodos de la enumeración,
 * especialmente {@link Commerce#fromLabel(String)} para la obtención de
 * instancias a partir de etiquetas, así como los getters y el comportamiento de
 * {@link #toString()}.
 * </p>
 * <p>
 * Las pruebas cubren:
 * </p>
 * <ul>
 * <li>Conversión de etiquetas válidas (con diferentes formatos de mayúsculas) a
 * la enumeración correspondiente.</li>
 * <li>Manejo de etiquetas inexistentes, verificando que se lance la excepción
 * esperada.</li>
 * <li>Verificación de los métodos {@code getLabel()} y {@code toString()}.</li>
 * <li>Comprobación de la estructura del enum (presencia de {@code DESCONOCIDO}
 * y cantidad de valores).</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see Commerce
 */
@DisplayName("Commerce - Enumeración de cadenas farmacéuticas")
class CommerceTests {

    // -------------------------------------------------------------------------
    // Pruebas Parametrizadas: fromLabel (Casos de Éxito)
    // -------------------------------------------------------------------------
    /**
     * Prueba parametrizada que verifica que {@link Commerce#fromLabel(String)}
     * retorna la enumeración correcta para etiquetas válidas, ignorando
     * mayúsculas/minúsculas.
     * <p>
     * Los casos de prueba incluyen etiquetas con diferentes formatos de
     * capitalización para asegurar la insensibilidad a mayúsculas.
     * </p>
     *
     * @param label la etiqueta de entrada (ej. "Ahumada", "CRUZ VERDE").
     * @param expectedCommerce la enumeración esperada correspondiente.
     */
    @ParameterizedTest(name = "should return {1} when label is ''{0}''")
    @MethodSource("provideValidLabels")
    @DisplayName("fromLabel: retorna la enumeración correcta para etiquetas válidas (case-insensitive)")
    void shouldReturnCorrectCommerceWhenLabelIsValid(String label, Commerce expectedCommerce) {
        // Arrange
        // (No requiere configuración adicional, los datos vienen del proveedor)

        // Act
        Commerce actualCommerce = Commerce.fromLabel(label);

        // Assert
        Assertions.assertEquals(expectedCommerce, actualCommerce,
                String.format("Se esperaba %s para el label '%s'", expectedCommerce, label));
    }

    /**
     * Proveedor de argumentos para la prueba parametrizada de etiquetas
     * válidas.
     *
     * @return un flujo de pares (label, Commerce esperado).
     */
    static Stream<Arguments> provideValidLabels() {
        return Stream.of(
                Arguments.of("Ahumada", Commerce.AHUMADA),
                Arguments.of("Cruz Verde", Commerce.CRUZ_VERDE),
                Arguments.of("ahumada", Commerce.AHUMADA),
                Arguments.of("CRUZ VERDE", Commerce.CRUZ_VERDE),
                Arguments.of("Simi", Commerce.SIMI),
                Arguments.of("Salcobrand", Commerce.SALCOBRAND)
        );
    }

    // -------------------------------------------------------------------------
    // Pruebas Unitarias: fromLabel (Casos de Error)
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica que {@link Commerce#fromLabel(String)} lanza
     * {@link IllegalArgumentException} cuando la etiqueta proporcionada no
     * coincide con ninguna de las enumeraciones definidas.
     * <p>
     * Se espera que la excepción sea lanzada y no sea nula.
     * </p>
     */
    @Test
    @DisplayName("fromLabel: lanza IllegalArgumentException para etiqueta inexistente")
    void shouldThrowExceptionWhenLabelIsNotFound() {
        // Arrange
        String invalidLabel = "Farmacia Desconocida Inexistente";

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Commerce.fromLabel(invalidLabel)
        );

        Assertions.assertNotNull(exception, "La excepción lanzada no debe ser nula");
    }

    // -------------------------------------------------------------------------
    // Pruebas Unitarias: Getters y Overrides
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica que {@link Commerce#getLabel()} devuelve la etiqueta
     * correcta para instancias específicas del enum.
     * <p>
     * Comprueba tanto un valor conocido como {@code DESCONOCIDO}.
     * </p>
     */
    @Test
    @DisplayName("getLabel: devuelve el label correcto para instancias específicas")
    void shouldReturnCorrectLabelForSpecificInstances() {
        // Arrange & Act & Assert
        Assertions.assertEquals("Ahumada", Commerce.AHUMADA.getLabel(), "El label de AHUMADA es incorrecto");
        Assertions.assertEquals("Desconocida", Commerce.DESCONOCIDO.getLabel(), "El label de DESCONOCIDO es incorrecto");
    }

    /**
     * Prueba que verifica que el método {@link Commerce#toString()} retorna la
     * etiqueta de la enumeración (delegando en {@code getLabel()}).
     */
    @Test
    @DisplayName("toString: devuelve el label de la enumeración")
    void shouldReturnLabelWhenToStringIsCalled() {
        // Arrange
        Commerce commerce = Commerce.AHUMADA;

        // Act
        String result = commerce.toString();

        // Assert
        Assertions.assertEquals("Ahumada", result, "toString() debe retornar el label de la farmacia");
    }

    // -------------------------------------------------------------------------
    // Pruebas Unitarias: Estructura del Enum (values)
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica que la enumeración {@code Commerce} contiene la
     * instancia {@link Commerce#DESCONOCIDO} como valor de fallback.
     * <p>
     * Esto asegura que siempre exista un valor por defecto para casos no
     * reconocidos.
     * </p>
     */
    @Test
    @DisplayName("values: contiene la instancia DESCONOCIDO como fallback")
    void shouldContainDesconocidoInstance() {
        // Arrange
        Commerce[] allCommerces = Commerce.values();

        // Act
        boolean containsDesconocido = Arrays.stream(allCommerces)
                .anyMatch(commerce -> commerce == Commerce.DESCONOCIDO);

        // Assert
        Assertions.assertTrue(containsDesconocido, "El enum Commerce debe incluir DESCONOCIDO como valor por defecto");
    }

    /**
     * Prueba que verifica que la enumeración contiene un número significativo
     * de valores (más de 10), lo que sugiere que el enum está bien poblado.
     * <p>
     * Este test es principalmente para detectar regresiones accidentales que
     * pudieran reducir el número de elementos.
     * </p>
     */
    @Test
    @DisplayName("values: contiene un número significativo de cadenas farmacéuticas")
    void shouldContainMultiplePharmacies() {
        // Arrange
        int minimumExpectedCommerces = 10;
        Commerce[] allCommerces = Commerce.values();

        // Act
        int actualSize = allCommerces.length;

        // Assert
        Assertions.assertTrue(actualSize > minimumExpectedCommerces,
                String.format("Se esperaban más de %d farmacias, pero se encontraron %d",
                        minimumExpectedCommerces, actualSize));
    }
}
