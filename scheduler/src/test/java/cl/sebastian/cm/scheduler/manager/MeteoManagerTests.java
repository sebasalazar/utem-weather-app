package cl.sebastian.cm.scheduler.manager;

import cl.sebastian.cm.scheduler.domain.data.RedMeteo;
import cl.sebastian.cm.scheduler.domain.model.Observation;
import cl.sebastian.cm.scheduler.domain.model.Station;
import cl.sebastian.cm.scheduler.domain.repository.ObservationRepository;
import cl.sebastian.cm.scheduler.domain.repository.StationRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para el servicio {@link MeteoManager}.
 * <p>
 * Verifica la lógica de negocio para la gestión de estaciones y observaciones
 * meteorológicas, incluyendo la creación de estaciones (get-or-create),
 * persistencia de observaciones con deduplicación, validaciones de entrada,
 * normalización de códigos, y consultas.
 * </p>
 * <p>
 * Las pruebas utilizan {@link Mockito} para aislar las dependencias
 * ({@link StationRepository} y {@link ObservationRepository}) y se centran en
 * el comportamiento del método {@link MeteoManager#saveObs(RedMeteo)} y los
 * métodos de consulta {@link MeteoManager#getStation(String)} y
 * {@link MeteoManager#getObservations(Station)}.
 * </p>
 * <p>
 * <strong>Cobertura de escenarios:</strong>
 * </p>
 * <ul>
 * <li>Creación de observación cuando no existen estación ni observación.</li>
 * <li>Creación de estación cuando no existe y luego creación de
 * observación.</li>
 * <li>Validaciones de entrada (DTO nulo, códigos vacíos).</li>
 * <li>Deduplicación: omisión de persistencia cuando la observación ya
 * existe.</li>
 * <li>Transformaciones de datos: normalización a mayúsculas, persistencia de
 * valores numéricos, manejo de valores nulos.</li>
 * <li>Consultas: obtención de estación por código y observaciones por
 * estación.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see MeteoManager
 * @see RedMeteo
 */
@DisplayName("MeteoManager - Gestión de datos meteorológicos")
@ExtendWith(MockitoExtension.class)
class MeteoManagerTests {

    // -------------------------------------------------------------------------
    // Constantes de prueba (elimina números mágicos y strings dispersos)
    // -------------------------------------------------------------------------
    /**
     * Código de estación de prueba.
     */
    private static final String SAMPLE_STATION_CODE = "EST001";

    /**
     * Nombre de estación de prueba.
     */
    private static final String SAMPLE_STATION_NAME = "Estación Metropolitana";

    /**
     * Latitud de prueba.
     */
    private static final Double SAMPLE_LATITUDE = -33.456;

    /**
     * Longitud de prueba.
     */
    private static final Double SAMPLE_LONGITUDE = -70.654;

    /**
     * Altitud de prueba (en metros).
     */
    private static final Integer SAMPLE_ALTITUDE = 700;

    /**
     * Código de observación de prueba.
     */
    private static final String SAMPLE_OBSERVATION_CODE = "OBS001";

    /**
     * Temperatura de prueba (en °C).
     */
    private static final Double SAMPLE_TEMPERATURE = 25.5;

    /**
     * Humedad de prueba (en %).
     */
    private static final Double SAMPLE_HUMIDITY = 65.0;

    /**
     * Velocidad del viento de prueba (en km/h).
     */
    private static final Double SAMPLE_WIND_SPEED = 10.0;

    /**
     * Dirección del viento de prueba (en grados).
     */
    private static final Long SAMPLE_WIND_DIRECTION = 180L;

    /**
     * Presión atmosférica de prueba (en hPa).
     */
    private static final Double SAMPLE_PRESSURE = 1013.25;

    /**
     * Presión absoluta de prueba (en hPa).
     */
    private static final Double SAMPLE_ABSOLUTE_PRESSURE = 1013.25;

    /**
     * Identificador de estación de prueba.
     */
    private static final Long SAMPLE_STATION_ID = 1L;

    /**
     * Identificador de observación de prueba.
     */
    private static final Long SAMPLE_OBSERVATION_ID = 1L;

    /**
     * Segundo identificador de observación de prueba.
     */
    private static final Long SAMPLE_SECOND_OBSERVATION_ID = 2L;

    // -------------------------------------------------------------------------
    // Mocks e Inyección
    // -------------------------------------------------------------------------
    /**
     * Mock del repositorio de estaciones.
     */
    @Mock
    private StationRepository stationRepository;

    /**
     * Mock del repositorio de observaciones.
     */
    @Mock
    private ObservationRepository observationRepository;

    /**
     * Instancia del servicio bajo prueba con los mocks inyectados.
     */
    @InjectMocks
    private MeteoManager meteoManager;

    // -------------------------------------------------------------------------
    // Builder de datos de prueba
    // -------------------------------------------------------------------------
    /**
     * Construye un objeto {@link RedMeteo} válido con datos de prueba.
     *
     * @return una instancia de {@link RedMeteo} completamente poblada.
     */
    private RedMeteo buildValidRedMeteo() {
        RedMeteo redMeteo = new RedMeteo();
        redMeteo.setIdEstacion(SAMPLE_STATION_CODE);
        redMeteo.setNombre(SAMPLE_STATION_NAME);
        redMeteo.setLatitud(SAMPLE_LATITUDE);
        redMeteo.setLongitud(SAMPLE_LONGITUDE);
        redMeteo.setAltitud(SAMPLE_ALTITUDE);
        redMeteo.setIdObservacion(SAMPLE_OBSERVATION_CODE);
        redMeteo.setFechaHora(OffsetDateTime.now());
        redMeteo.setTemperatura(SAMPLE_TEMPERATURE);
        redMeteo.setHumedad(SAMPLE_HUMIDITY);
        redMeteo.setVelocidadViento(SAMPLE_WIND_SPEED);
        redMeteo.setDireccionViento(SAMPLE_WIND_DIRECTION);
        redMeteo.setPresion(SAMPLE_PRESSURE);
        redMeteo.setPresionAbsoluta(SAMPLE_ABSOLUTE_PRESSURE);
        return redMeteo;
    }

    /**
     * Construye una estación con ID asignado.
     *
     * @return una instancia de {@link Station} con ID.
     */
    private Station buildStationWithId() {
        Station station = new Station();
        station.setId(SAMPLE_STATION_ID);
        station.setCode(SAMPLE_STATION_CODE);
        return station;
    }

    // -------------------------------------------------------------------------
    // Stubs reutilizables (responsabilidad única: configurar mocks)
    // -------------------------------------------------------------------------
    /**
     * Configura el stub para simular que la estación existe.
     *
     * @param station estación que se devolverá en la búsqueda.
     */
    private void stubStationFound(Station station) {
        Mockito.when(stationRepository.findByCodeIgnoreCase(SAMPLE_STATION_CODE))
                .thenReturn(station);
    }

    /**
     * Configura el stub para simular que la estación no existe.
     */
    private void stubStationNotFound() {
        Mockito.when(stationRepository.findByCodeIgnoreCase(SAMPLE_STATION_CODE))
                .thenReturn(null);
    }

    /**
     * Configura el stub del repositorio para simular persistencia de estación:
     * asigna un ID a la entidad capturada y la retorna.
     */
    private void stubStationSaveWithIdAssignment() {
        Mockito.when(stationRepository.save(Mockito.any(Station.class)))
                .thenAnswer(invocation -> {
                    Station station = invocation.getArgument(0);
                    station.setId(SAMPLE_STATION_ID);
                    return station;
                });
    }

    /**
     * Configura el stub para simular que la observación no existe para una
     * estación dada.
     *
     * @param station estación sobre la que se busca la observación.
     */
    private void stubObservationNotFound(Station station) {
        Mockito.when(observationRepository.findByStationAndCodeIgnoreCase(station, SAMPLE_OBSERVATION_CODE))
                .thenReturn(null);
    }

    /**
     * Configura el stub para simular que la observación no existe para
     * cualquier estación.
     */
    private void stubObservationNotFoundForAny() {
        Mockito.when(observationRepository.findByStationAndCodeIgnoreCase(Mockito.any(), Mockito.eq(SAMPLE_OBSERVATION_CODE)))
                .thenReturn(null);
    }

    /**
     * Configura el stub para simular que la observación ya existe.
     *
     * @param observation observación que se devolverá en la búsqueda.
     */
    private void stubObservationFound(Observation observation) {
        Mockito.when(observationRepository.findByStationAndCodeIgnoreCase(Mockito.any(), Mockito.eq(SAMPLE_OBSERVATION_CODE)))
                .thenReturn(observation);
    }

    /**
     * Configura el stub del repositorio para simular persistencia de
     * observación: asigna un ID a la entidad capturada y la retorna.
     */
    private void stubObservationSaveWithIdAssignment() {
        Mockito.when(observationRepository.save(Mockito.any(Observation.class)))
                .thenAnswer(invocation -> {
                    Observation observation = invocation.getArgument(0);
                    observation.setId(SAMPLE_OBSERVATION_ID);
                    return observation;
                });
    }

    // -------------------------------------------------------------------------
    // Escenarios de creación de observación (saveObs)
    // -------------------------------------------------------------------------
    /**
     * Verifica que cuando no existen ni la estación ni la observación, se crean
     * ambas.
     */
    @Test
    @DisplayName("saveObs: crea nueva observación cuando estación y observación no existen")
    void shouldCreateNewObservationWhenBothStationAndObservationDoNotExist() {
        // Arrange
        RedMeteo redMeteo = buildValidRedMeteo();
        Station station = buildStationWithId();
        stubStationFound(station);
        stubObservationNotFound(station);
        stubObservationSaveWithIdAssignment();

        // Act
        boolean result = meteoManager.saveObs(redMeteo);

        // Assert
        Assertions.assertTrue(result, "saveObs debe retornar true cuando la observación se crea exitosamente");
        Mockito.verify(observationRepository).save(Mockito.any(Observation.class));
    }

    /**
     * Verifica que si la estación no existe, se crea primero y luego la
     * observación.
     */
    @Test
    @DisplayName("saveObs: crea estación si no existe y luego crea observación")
    void shouldCreateStationIfMissingAndThenCreateObservation() {
        // Arrange
        RedMeteo redMeteo = buildValidRedMeteo();
        stubStationNotFound();
        stubStationSaveWithIdAssignment();
        stubObservationNotFoundForAny();
        stubObservationSaveWithIdAssignment();

        // Act
        boolean result = meteoManager.saveObs(redMeteo);

        // Assert
        Assertions.assertTrue(result, "saveObs debe retornar true tras crear estación y observación");
        Mockito.verify(stationRepository).save(Mockito.any(Station.class));
        Mockito.verify(observationRepository).save(Mockito.any(Observation.class));
    }

    // -------------------------------------------------------------------------
    // Escenarios de validación de entrada (guardas)
    // -------------------------------------------------------------------------
    /**
     * Verifica que el método retorna false cuando el DTO es null.
     */
    @Test
    @DisplayName("saveObs: retorna false cuando RedMeteo es null")
    void shouldReturnFalseWhenRedMeteoIsNull() {
        // Arrange
        // (No requiere configuración; null es el SUT)

        // Act
        boolean result = meteoManager.saveObs(null);

        // Assert
        Assertions.assertFalse(result, "saveObs debe retornar false para entrada null");
        Mockito.verify(observationRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Verifica que el método retorna false cuando el código de estación está
     * vacío.
     */
    @Test
    @DisplayName("saveObs: retorna false cuando el código de estación es vacío")
    void shouldReturnFalseWhenStationCodeIsEmpty() {
        // Arrange
        RedMeteo redMeteo = buildValidRedMeteo();
        redMeteo.setIdEstacion("");

        // Act
        boolean result = meteoManager.saveObs(redMeteo);

        // Assert
        Assertions.assertFalse(result, "saveObs debe retornar false para código de estación vacío");
        Mockito.verify(observationRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Verifica que el método retorna false cuando el código de observación está
     * vacío.
     */
    @Test
    @DisplayName("saveObs: retorna false cuando el código de observación es vacío")
    void shouldReturnFalseWhenObservationCodeIsEmpty() {
        // Arrange
        RedMeteo redMeteo = buildValidRedMeteo();
        redMeteo.setIdObservacion("");
        Station station = buildStationWithId();
        stubStationFound(station);

        // Act
        boolean result = meteoManager.saveObs(redMeteo);

        // Assert
        Assertions.assertFalse(result, "saveObs debe retornar false para código de observación vacío");
        Mockito.verify(observationRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Verifica que el método retorna false cuando la observación ya existe
     * (deduplicación).
     */
    @Test
    @DisplayName("saveObs: retorna false cuando la observación ya existe (deduplicación)")
    void shouldReturnFalseWhenObservationAlreadyExists() {
        // Arrange
        RedMeteo redMeteo = buildValidRedMeteo();
        Station station = buildStationWithId();
        Observation existingObservation = new Observation();
        existingObservation.setId(SAMPLE_OBSERVATION_ID);

        stubStationFound(station);
        stubObservationFound(existingObservation);

        // Act
        boolean result = meteoManager.saveObs(redMeteo);

        // Assert
        Assertions.assertFalse(result, "saveObs debe retornar false para observación duplicada");
        Mockito.verify(observationRepository, Mockito.never()).save(Mockito.any());
    }

    // -------------------------------------------------------------------------
    // Escenarios de transformación de datos (lógica de negocio)
    // -------------------------------------------------------------------------
    /**
     * Verifica que el código de estación se normaliza a mayúsculas para la
     * búsqueda.
     */
    @Test
    @DisplayName("saveObs: normaliza código de estación a mayúsculas para búsqueda")
    void shouldNormalizeStationCodeToUpperCaseForLookup() {
        // Arrange
        RedMeteo redMeteo = buildValidRedMeteo();
        redMeteo.setIdEstacion("est001");
        Station station = buildStationWithId();

        Mockito.when(stationRepository.findByCodeIgnoreCase("EST001")).thenReturn(station);
        stubObservationNotFound(station);
        stubObservationSaveWithIdAssignment();

        ArgumentCaptor<Observation> observationCaptor = ArgumentCaptor.forClass(Observation.class);

        // Act
        boolean result = meteoManager.saveObs(redMeteo);

        // Assert
        Assertions.assertTrue(result, "saveObs debe completar exitosamente tras normalización");
        Mockito.verify(observationRepository).save(observationCaptor.capture());
        Observation capturedObservation = observationCaptor.getValue();
        Assertions.assertEquals(SAMPLE_OBSERVATION_CODE, capturedObservation.getCode(),
                "El código de observación debe preservarse en mayúsculas");
    }

    /**
     * Verifica que los valores numéricos de temperatura, humedad y viento se
     * persisten correctamente.
     */
    @Test
    @DisplayName("saveObs: persiste valores numéricos de temperatura, humedad y viento")
    void shouldPersistNumericValuesForTemperatureHumidityAndWind() {
        // Arrange
        RedMeteo redMeteo = buildValidRedMeteo();
        Station station = buildStationWithId();
        stubStationFound(station);
        stubObservationNotFound(station);
        stubObservationSaveWithIdAssignment();

        ArgumentCaptor<Observation> observationCaptor = ArgumentCaptor.forClass(Observation.class);

        // Act
        meteoManager.saveObs(redMeteo);

        // Assert
        Mockito.verify(observationRepository).save(observationCaptor.capture());
        Observation capturedObservation = observationCaptor.getValue();
        Assertions.assertEquals(SAMPLE_TEMPERATURE, capturedObservation.getTemperature(),
                "La temperatura debe persistirse sin transformación");
        Assertions.assertEquals(SAMPLE_HUMIDITY, capturedObservation.getHumidity(),
                "La humedad debe persistirse sin transformación");
        Assertions.assertEquals(SAMPLE_WIND_SPEED, capturedObservation.getWindSpeed(),
                "La velocidad del viento debe persistirse sin transformación");
    }

    /**
     * Verifica que el método permite valores null en campos numéricos
     * opcionales.
     */
    @Test
    @DisplayName("saveObs: permite valores null en campos numéricos opcionales")
    void shouldAllowNullValuesInOptionalNumericFields() {
        // Arrange
        RedMeteo redMeteo = buildValidRedMeteo();
        redMeteo.setTemperatura(null);
        redMeteo.setHumedad(null);
        Station station = buildStationWithId();
        stubStationFound(station);
        stubObservationNotFound(station);
        stubObservationSaveWithIdAssignment();

        // Act
        boolean result = meteoManager.saveObs(redMeteo);

        // Assert
        Assertions.assertTrue(result, "saveObs debe completar exitosamente con campos numéricos null");
        Mockito.verify(observationRepository).save(Mockito.any(Observation.class));
    }

    // -------------------------------------------------------------------------
    // Escenarios de consulta (getStation, getObservations)
    // -------------------------------------------------------------------------
    /**
     * Verifica que getStation retorna null cuando el código está vacío.
     */
    @Test
    @DisplayName("getStation: retorna null cuando el código es vacío")
    void shouldReturnNullWhenStationCodeIsEmpty() {
        // Arrange
        // (No requiere configuración; string vacío es el SUT)

        // Act
        Station result = meteoManager.getStation("");

        // Assert
        Assertions.assertNull(result, "getStation debe retornar null para código vacío");
        Mockito.verify(stationRepository, Mockito.never()).findByCodeIgnoreCase(Mockito.any());
    }

    /**
     * Verifica que getStation retorna null cuando el código es null.
     */
    @Test
    @DisplayName("getStation: retorna null cuando el código es null")
    void shouldReturnNullWhenStationCodeIsNull() {
        // Arrange
        // (No requiere configuración; null es el SUT)

        // Act
        Station result = meteoManager.getStation(null);

        // Assert
        Assertions.assertNull(result, "getStation debe retornar null para código null");
        Mockito.verify(stationRepository, Mockito.never()).findByCodeIgnoreCase(Mockito.any());
    }

    /**
     * Verifica que getStation busca y retorna la estación por código.
     */
    @Test
    @DisplayName("getStation: busca y retorna estación por código")
    void shouldFindAndReturnStationByCode() {
        // Arrange
        Station station = buildStationWithId();
        stubStationFound(station);

        // Act
        Station result = meteoManager.getStation(SAMPLE_STATION_CODE);

        // Assert
        Assertions.assertEquals(station, result, "getStation debe retornar la estación encontrada");
        Mockito.verify(stationRepository).findByCodeIgnoreCase(SAMPLE_STATION_CODE);
    }

    /**
     * Verifica que getObservations retorna lista vacía cuando la estación es
     * null.
     */
    @Test
    @DisplayName("getObservations: retorna lista vacía cuando la estación es null")
    void shouldReturnEmptyListWhenStationIsNull() {
        // Arrange
        // (No requiere configuración; null es el SUT)

        // Act
        List<Observation> result = meteoManager.getObservations(null);

        // Assert
        Assertions.assertTrue(result.isEmpty(), "getObservations debe retornar lista vacía para estación null");
        Mockito.verify(observationRepository, Mockito.never()).findByStation(Mockito.any());
    }

    /**
     * Verifica que getObservations retorna todas las observaciones de una
     * estación.
     */
    @Test
    @DisplayName("getObservations: retorna todas las observaciones de una estación")
    void shouldReturnAllObservationsForStation() {
        // Arrange
        Station station = buildStationWithId();
        Observation observation1 = new Observation();
        observation1.setId(SAMPLE_OBSERVATION_ID);
        Observation observation2 = new Observation();
        observation2.setId(SAMPLE_SECOND_OBSERVATION_ID);

        Mockito.when(observationRepository.findByStation(station))
                .thenReturn(List.of(observation1, observation2));

        // Act
        List<Observation> result = meteoManager.getObservations(station);

        // Assert
        Assertions.assertEquals(2, result.size(), "getObservations debe retornar todas las observaciones");
        Mockito.verify(observationRepository).findByStation(station);
    }
}
