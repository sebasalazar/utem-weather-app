package cl.sebastian.cm.rest.manager;

import cl.sebastian.cm.rest.domain.data.IdDistance;
import cl.sebastian.cm.rest.domain.model.Observation;
import cl.sebastian.cm.rest.domain.model.Station;
import cl.sebastian.cm.rest.domain.repository.ObservationRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de {@link MeteoManager}.
 *
 * <p>
 * Valida la lógica de negocio para encontrar la última observación
 * meteorológica más cercana a una ubicación geográfica dada, considerando:</p>
 * <ul>
 * <li>Validación de coordenadas geográficas</li>
 * <li>Distancia máxima de búsqueda configurable</li>
 * <li>Recuperación de observaciones desde estaciones meteorológicas</li>
 * </ul>
 *
 * <h3>Glosario para lectores no técnicos</h3>
 * <ul>
 * <li><b>Observación meteorológica:</b> Registro de condiciones climáticas
 * (temperatura, humedad, presión, etc.) capturado en un momento específico por
 * una estación.</li>
 * <li><b>Estación meteorológica:</b> Punto de medición equipado con sensores
 * que registra datos climáticos. Cada estación tiene una ubicación geográfica
 * fija.</li>
 * <li><b>Distancia máxima:</b> Radio máximo (en metros) dentro del cual se
 * buscan observaciones. Valor por defecto: 1 metro (configurable).</li>
 * <li><b>IdDistance:</b> Estructura que contiene el identificador de una
 * observación y su distancia (en metros) respecto al punto de búsqueda.</li>
 * <li><b>OffsetDateTime:</b> Fecha y hora con información de zona horaria. Se
 * usa UTC para estandarizar las mediciones meteorológicas.</li>
 * </ul>
 *
 * <p>
 * Estructura: los escenarios se agrupan por responsabilidad mediante clases
 * {@code @Nested} y se parametrizan cuando comparten la misma aserción,
 * siguiendo la convención Arrange-Act-Assert (AAA).</p>
 */
@ExtendWith(MockitoExtension.class)
class MeteoManagerTests {

    @Mock
    private ObservationRepository observationRepository;

    private MeteoManager meteoManager;

    @BeforeEach
    void setUp() {
        meteoManager = new MeteoManager(observationRepository, 15000);
    }

    // -------------------------------------------------------------------------
    // Factory methods para creación de objetos de prueba
    // -------------------------------------------------------------------------
    private Station crearEstacion(long id) {
        Station station = new Station();
        station.setId(id);
        return station;
    }

    private Observation crearObservacion(long id, long estacionId, double temperatura) {
        Observation observation = new Observation();
        observation.setId(id);
        observation.setStation(crearEstacion(estacionId));
        observation.setTemperature(temperatura);
        observation.setDate(OffsetDateTime.now(ZoneOffset.UTC));
        return observation;
    }

    private IdDistance crearIdDistance(long id, int distancia) {
        return new IdDistance(id, distancia);
    }

    // -------------------------------------------------------------------------
    // Constructor y configuración de distancia máxima
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Configuración de distancia máxima de búsqueda")
    class ConfiguracionDistanciaMaximaTests {

        @Test
        @DisplayName("Debe usar distancia por defecto de 1 metro cuando se pasa null")
        void constructorConDistanciaNulaDebeUsarValorPorDefecto() {
            // Arrange & Act
            MeteoManager manager = new MeteoManager(observationRepository, null);

            // Assert
            org.junit.jupiter.api.Assertions.assertEquals(
                    1,
                    manager.getMaxDistance(),
                    "La distancia máxima por defecto debe ser 1 metro"
            );
        }

        @Test
        @DisplayName("Debe usar distancia mínima de 1 metro cuando se pasa 0")
        void constructorConDistanciaCeroDebeUsarValorMinimo() {
            // Arrange & Act
            MeteoManager manager = new MeteoManager(observationRepository, 0);

            // Assert
            org.junit.jupiter.api.Assertions.assertEquals(
                    1,
                    manager.getMaxDistance(),
                    "La distancia mínima debe ser 1 metro"
            );
        }

        @Test
        @DisplayName("Debe usar la distancia máxima especificada cuando es válida")
        void constructorConDistanciaValidaDebeUsarValorEspecifico() {
            // Arrange & Act
            MeteoManager manager = new MeteoManager(observationRepository, 5000);

            // Assert
            org.junit.jupiter.api.Assertions.assertEquals(
                    5000,
                    manager.getMaxDistance(),
                    "La distancia máxima debe ser 5000 metros"
            );
        }
    }

    // -------------------------------------------------------------------------
    // Validación de entrada: coordenadas geográficas
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Validación de coordenadas geográficas")
    class ValidacionCoordenadasTests {

        @ParameterizedTest(name = "lat={0}, lon={1} debe ser inválida")
        @CsvSource({
            // Latitud fuera de rango
            "91.0,    0.0",
            "-91.0,   0.0",
            // Longitud fuera de rango
            "0.0,     181.0",
            "0.0,    -181.0",
            // Ambos fuera de rango
            "100.0,   200.0",
            "-100.0, -200.0"
        })
        @DisplayName("Debe retornar vacío cuando las coordenadas están fuera del rango válido")
        void getLastNearbyConCoordenadasInvalidasDebeRetornarVacio(
                double latitud, double longitud) {
            // Arrange (implícito en los parámetros)

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitud, longitud);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    String.format("El resultado debe estar vacío para coordenadas (%f, %f)",
                            latitud, longitud)
            );
            org.mockito.Mockito.verify(
                    observationRepository,
                    org.mockito.Mockito.never()
            ).searchByDistance(
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyLong()
            );
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la latitud es NaN")
        void getLastNearbyConLatitudNaNDebeRetornarVacio() {
            // Arrange
            double latitudNaN = Double.NaN;
            double longitudValida = 0.0;

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitudNaN, longitudValida);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando la latitud es NaN"
            );
            org.mockito.Mockito.verify(
                    observationRepository,
                    org.mockito.Mockito.never()
            ).searchByDistance(
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyLong()
            );
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la longitud es NaN")
        void getLastNearbyConLongitudNaNDebeRetornarVacio() {
            // Arrange
            double latitudValida = 0.0;
            double longitudNaN = Double.NaN;

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitudValida, longitudNaN);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando la longitud es NaN"
            );
            org.mockito.Mockito.verify(
                    observationRepository,
                    org.mockito.Mockito.never()
            ).searchByDistance(
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyLong()
            );
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la latitud es infinito positivo")
        void getLastNearbyConLatitudInfinitaDebeRetornarVacio() {
            // Arrange
            double latitudInfinita = Double.POSITIVE_INFINITY;
            double longitudValida = 0.0;

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitudInfinita, longitudValida);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando la latitud es infinita"
            );
            org.mockito.Mockito.verify(
                    observationRepository,
                    org.mockito.Mockito.never()
            ).searchByDistance(
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyLong()
            );
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la longitud es infinito negativo")
        void getLastNearbyConLongitudInfinitaDebeRetornarVacio() {
            // Arrange
            double latitudValida = 0.0;
            double longitudInfinita = Double.NEGATIVE_INFINITY;

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitudValida, longitudInfinita);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando la longitud es infinita"
            );
            org.mockito.Mockito.verify(
                    observationRepository,
                    org.mockito.Mockito.never()
            ).searchByDistance(
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyLong()
            );
        }
    }

    // -------------------------------------------------------------------------
    // Búsqueda sin resultados
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Búsqueda sin observaciones encontradas")
    class BusquedaSinResultadosTests {

        @Test
        @DisplayName("Debe retornar vacío cuando no hay observaciones en el radio de búsqueda")
        void getLastNearbySinObservacionesCercanasDebeRetornarVacio() {
            // Arrange
            double latitud = -33.4567;
            double longitud = -70.6543;
            long distanciaMaxima = 15000L;

            org.mockito.Mockito.when(
                    observationRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(Collections.emptyList());

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitud, longitud);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando no hay observaciones cercanas"
            );
            org.mockito.Mockito.verify(observationRepository).searchByDistance(
                    latitud, longitud, distanciaMaxima);
            org.mockito.Mockito.verify(
                    observationRepository,
                    org.mockito.Mockito.never()
            ).findById(org.mockito.ArgumentMatchers.anyLong());
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la observación encontrada no existe en BD")
        void getLastNearbyConObservacionNoExistenteDebeRetornarVacio() {
            // Arrange
            double latitud = -33.4567;
            double longitud = -70.6543;
            long distanciaMaxima = 15000L;
            IdDistance idDistance = crearIdDistance(1L, 5000);

            org.mockito.Mockito.when(
                    observationRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(observationRepository.findById(1L))
                    .thenReturn(Optional.empty());

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitud, longitud);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando la observación no existe"
            );
            org.mockito.Mockito.verify(observationRepository).searchByDistance(
                    latitud, longitud, distanciaMaxima);
            org.mockito.Mockito.verify(observationRepository).findById(1L);
        }
    }

    // -------------------------------------------------------------------------
    // Búsqueda exitosa con observaciones
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Búsqueda exitosa de observaciones meteorológicas")
    class BusquedaExitosaTests {

        @Test
        @DisplayName("Debe retornar observación cuando existe dentro del radio de búsqueda")
        void getLastNearbyConObservacionValidaDebeRetornarObservacion() {
            // Arrange
            double latitud = -33.4567;
            double longitud = -70.6543;
            long distanciaMaxima = 15000L;
            Observation observacion = crearObservacion(1L, 1L, 25.5);
            IdDistance idDistance = crearIdDistance(1L, 5000);

            org.mockito.Mockito.when(
                    observationRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(observationRepository.findById(1L))
                    .thenReturn(Optional.of(observacion));

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitud, longitud);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    "Debe retornar la observación encontrada"
            );
            org.junit.jupiter.api.Assertions.assertEquals(
                    1L,
                    resultado.get().getId(),
                    "El ID de la observación debe ser 1"
            );
            org.junit.jupiter.api.Assertions.assertEquals(
                    25.5,
                    resultado.get().getTemperature(),
                    "La temperatura debe ser 25.5°C"
            );
            org.mockito.Mockito.verify(observationRepository).searchByDistance(
                    latitud, longitud, distanciaMaxima);
            org.mockito.Mockito.verify(observationRepository).findById(1L);
        }

        @Test
        @DisplayName("Debe retornar la primera observación encontrada cuando hay múltiples")
        void getLastNearbyConMultiplesObservacionesDebeRetornarPrimera() {
            // Arrange
            double latitud = -33.4567;
            double longitud = -70.6543;
            long distanciaMaxima = 15000L;
            Observation observacion1 = crearObservacion(1L, 1L, 25.5);
            IdDistance id1 = crearIdDistance(1L, 3000);
            IdDistance id2 = crearIdDistance(2L, 5000);

            org.mockito.Mockito.when(
                    observationRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(id1, id2));
            org.mockito.Mockito.when(observationRepository.findById(1L))
                    .thenReturn(Optional.of(observacion1));

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(latitud, longitud);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    "Debe retornar una observación"
            );
            org.junit.jupiter.api.Assertions.assertEquals(
                    1L,
                    resultado.get().getId(),
                    "Debe retornar la primera observación encontrada (ID 1)"
            );
            org.mockito.Mockito.verify(observationRepository).searchByDistance(
                    latitud, longitud, distanciaMaxima);
            org.mockito.Mockito.verify(observationRepository).findById(1L);
            org.mockito.Mockito.verify(
                    observationRepository,
                    org.mockito.Mockito.never()
            ).findById(2L);
        }

        @Test
        @DisplayName("Debe retornar observación para coordenadas chilenas representativas")
        void getLastNearbyConCoordenadasChilenasDebeRetornarObservacion() {
            // Arrange
            double latitudSantiago = -33.8668;
            double longitudSantiago = -70.1693;
            long distanciaMaxima = 15000L;
            Observation observacion = crearObservacion(1L, 1L, 20.0);
            IdDistance idDistance = crearIdDistance(1L, 100);

            org.mockito.Mockito.when(
                    observationRepository.searchByDistance(latitudSantiago, longitudSantiago, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(observationRepository.findById(1L))
                    .thenReturn(Optional.of(observacion));

            // Act
            Optional<Observation> resultado = meteoManager.getLastNearby(
                    latitudSantiago, longitudSantiago);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    "Debe retornar observación para coordenadas de Santiago"
            );
            org.mockito.Mockito.verify(observationRepository).searchByDistance(
                    latitudSantiago, longitudSantiago, distanciaMaxima);
            org.mockito.Mockito.verify(observationRepository).findById(1L);
        }
    }
}
