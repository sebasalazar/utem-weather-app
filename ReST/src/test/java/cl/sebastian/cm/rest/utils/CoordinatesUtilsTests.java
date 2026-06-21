package cl.sebastian.cm.rest.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias de {@link CoordinatesUtils}.
 *
 * <p>
 * Valida el comportamiento del constructor privado (clase utilitaria) y la
 * validación de coordenadas geográficas mediante el método
 * {@code areValid(double latitude, double longitude)}.</p>
 *
 * <h3>Glosario para lectores no técnicos</h3>
 * <ul>
 * <li><b>Latitud:</b> distancia angular respecto al Ecuador. Rango válido:
 * [-90°, 90°].</li>
 * <li><b>Longitud:</b> distancia angular respecto al meridiano de Greenwich.
 * Rango válido: [-180°, 180°].</li>
 * <li><b>NaN (Not a Number):</b> valor especial de coma flotante que indica un
 * resultado matemático indefinido (ej: 0/0). Nunca debe considerarse una
 * coordenada válida.</li>
 * <li><b>Infinity:</b> valor especial de coma flotante que representa un número
 * infinito. Tampoco corresponde a una ubicación geográfica real.</li>
 * </ul>
 *
 * <p>
 * Estructura: los escenarios se agrupan por responsabilidad mediante clases
 * {@code @Nested} y se parametrizan cuando comparten la misma aserción,
 * siguiendo la convención Arrange-Act-Assert (AAA).</p>
 */
class CoordinatesUtilsTests {

    // -------------------------------------------------------------------------
    // Constructor privado (clase utilitaria)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Constructor privado de clase utilitaria")
    class ConstructorPrivadoTests {

        @Test
        @DisplayName("Debe impedir la instanciación externa lanzando UnsupportedOperationException")
        void constructorPrivadoDebeImpedirInstanciacion() throws NoSuchMethodException {
            // Arrange
            Constructor<CoordinatesUtils> constructor
                    = CoordinatesUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            // Act & Assert
            InvocationTargetException exception
                    = org.junit.jupiter.api.Assertions.assertThrows(
                            InvocationTargetException.class,
                            () -> constructor.newInstance()
                    );

            org.junit.jupiter.api.Assertions.assertInstanceOf(
                    UnsupportedOperationException.class,
                    exception.getCause(),
                    "La causa raíz debe ser UnsupportedOperationException"
            );
        }
    }

    // -------------------------------------------------------------------------
    // Coordenadas válidas (incluye límites del rango geográfico)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Coordenadas válidas dentro del rango geográfico")
    class CoordenadasValidasTests {

        @ParameterizedTest(name = "lat={0}, lon={1} debe ser válida")
        @CsvSource({
            // Centro del sistema
            "0.0,    0.0",
            // Coordenadas chilenas representativas (Santiago, Antofagasta, Puerto Montt)
            "-33.4,  -70.6",
            "-23.63, -70.39",
            "-41.46, -72.93",
            // Límites absolutos del sistema geográfico
            "90.0,   180.0",
            "-90.0, -180.0",
            // Límites de latitud con longitud cero
            "90.0,   0.0",
            "-90.0,  0.0",
            // Límites de longitud con latitud cero
            "0.0,    180.0",
            "0.0,   -180.0"
        })
        void coordenadasDentroDelRangoDebenSerValidas(double latitud, double longitud) {
            // Arrange (implícito en los parámetros del test)

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitud, longitud);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado,
                    String.format("Se esperaba válida la coordenada (%f, %f)", latitud, longitud)
            );
        }
    }

    // -------------------------------------------------------------------------
    // Coordenadas inválidas: latitud fuera de rango
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Coordenadas inválidas por latitud fuera de rango [-90, 90]")
    class LatitudInvalidaTests {

        @ParameterizedTest(name = "lat={0} debe ser inválida")
        @ValueSource(doubles = {-180.0, -100.0, -91.0, -90.1, 90.1, 91.0, 100.0, 180.0})
        void latitudFueraDeRangoDebeSerInvalida(double latitud) {
            // Arrange
            double longitudValida = 0.0;

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitud, longitudValida);

            // Assert
            org.junit.jupiter.api.Assertions.assertFalse(
                    resultado,
                    String.format("Se esperaba inválida la latitud %f", latitud)
            );
        }
    }

    // -------------------------------------------------------------------------
    // Coordenadas inválidas: longitud fuera de rango
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Coordenadas inválidas por longitud fuera de rango [-180, 180]")
    class LongitudInvalidaTests {

        @ParameterizedTest(name = "lon={0} debe ser inválida")
        @ValueSource(doubles = {-360.0, -200.0, -181.0, -180.1, 180.1, 181.0, 200.0, 360.0})
        void longitudFueraDeRangoDebeSerInvalida(double longitud) {
            // Arrange
            double latitudValida = 0.0;

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitudValida, longitud);

            // Assert
            org.junit.jupiter.api.Assertions.assertFalse(
                    resultado,
                    String.format("Se esperaba inválida la longitud %f", longitud)
            );
        }
    }

    // -------------------------------------------------------------------------
    // Coordenadas inválidas: ambos ejes fuera de rango simultáneamente
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Coordenadas inválidas cuando ambos ejes están fuera de rango")
    class AmbosEjesInvalidosTests {

        @ParameterizedTest(name = "lat={0}, lon={1} debe ser inválida")
        @CsvSource({
            "91.0,   181.0",
            "-91.0, -181.0",
            "100.0, -200.0",
            "-100.0, 200.0"
        })
        void coordenadasConAmbosEjesFueraDeRangoDebenSerInvalidas(
                double latitud, double longitud) {
            // Arrange (implícito en los parámetros)

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitud, longitud);

            // Assert
            org.junit.jupiter.api.Assertions.assertFalse(
                    resultado,
                    String.format("Se esperaba inválida la coordenada (%f, %f)",
                            latitud, longitud)
            );
        }
    }

    // -------------------------------------------------------------------------
    // Casos borde especiales de coma flotante
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Casos borde: valores especiales de coma flotante (NaN, Infinity)")
    class CasosBordeComaFlotanteTests {

        @Test
        @DisplayName("Debe rechazar latitud NaN aunque la longitud sea válida")
        void latitudNaNDebeSerInvalida() {
            // Arrange
            double latitudNaN = Double.NaN;
            double longitudValida = 0.0;

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitudNaN, longitudValida);

            // Assert
            org.junit.jupiter.api.Assertions.assertFalse(
                    resultado,
                    "NaN en latitud debe considerarse inválido"
            );
        }

        @Test
        @DisplayName("Debe rechazar longitud NaN aunque la latitud sea válida")
        void longitudNaNDebeSerInvalida() {
            // Arrange
            double latitudValida = 0.0;
            double longitudNaN = Double.NaN;

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitudValida, longitudNaN);

            // Assert
            org.junit.jupiter.api.Assertions.assertFalse(
                    resultado,
                    "NaN en longitud debe considerarse inválido"
            );
        }

        @Test
        @DisplayName("Debe rechazar latitud positiva infinita")
        void latitudInfinitoPositivoDebeSerInvalida() {
            // Arrange
            double latitudInfinita = Double.POSITIVE_INFINITY;
            double longitudValida = 0.0;

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitudInfinita, longitudValida);

            // Assert
            org.junit.jupiter.api.Assertions.assertFalse(
                    resultado,
                    "Infinity en latitud debe considerarse inválido"
            );
        }

        @Test
        @DisplayName("Debe rechazar longitud negativa infinita")
        void longitudInfinitoNegativoDebeSerInvalida() {
            // Arrange
            double latitudValida = 0.0;
            double longitudInfinita = Double.NEGATIVE_INFINITY;

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitudValida, longitudInfinita);

            // Assert
            org.junit.jupiter.api.Assertions.assertFalse(
                    resultado,
                    "Infinity en longitud debe considerarse inválido"
            );
        }

        @Test
        @DisplayName("Debe rechazar coordenadas donde ambos valores son NaN")
        void ambosValoresNaNDebenSerInvalidos() {
            // Arrange
            double latitudNaN = Double.NaN;
            double longitudNaN = Double.NaN;

            // Act
            boolean resultado = CoordinatesUtils.areValid(latitudNaN, longitudNaN);

            // Assert
            org.junit.jupiter.api.Assertions.assertFalse(
                    resultado,
                    "Coordenadas completamente NaN deben considerarse inválidas"
            );
        }
    }
}
