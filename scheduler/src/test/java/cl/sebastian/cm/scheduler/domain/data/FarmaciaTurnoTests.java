package cl.sebastian.cm.scheduler.domain.data;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para el DTO {@link FarmaciaTurno}.
 * <p>
 * Verifica el correcto funcionamiento de los getters y setters del DTO que
 * representa la información de una farmacia de turno. Se asegura de que las
 * propiedades se inicialicen correctamente con el constructor por defecto, que
 * los setters almacenen y recuperen los valores sin interferencias entre
 * campos, y que el comportamiento de las propiedades de tiempo sea coherente.
 * </p>
 * <p>
 * Las pruebas cubren:
 * </p>
 * <ul>
 * <li>Estado inicial de todas las propiedades (deben ser {@code null}).</li>
 * <li>Ciclo completo de asignación y recuperación de todas las propiedades,
 * verificando que no haya interferencia entre campos.</li>
 * <li>Comportamiento específico de las propiedades de hora de inicio y fin,
 * verificando su independencia y coherencia temporal.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see FarmaciaTurno
 */
@DisplayName("FarmaciaTurno - DTO para datos de farmacias de turno")
class FarmaciaTurnoTests {

    // -------------------------------------------------------------------------
    // Constantes de prueba (valores semánticos, sin números mágicos)
    // -------------------------------------------------------------------------
    /**
     * Fecha de prueba para el turno.
     */
    private static final LocalDate SAMPLE_DATE = LocalDate.of(2026, 6, 20);

    /**
     * Identificador de tienda de prueba.
     */
    private static final Integer SAMPLE_STORE_ID = 12345;

    /**
     * Nombre de la farmacia de prueba.
     */
    private static final String SAMPLE_PHARMACY_NAME = "Farmacia AHUMADA";

    /**
     * Dirección de la farmacia de prueba.
     */
    private static final String SAMPLE_PHARMACY_ADDRESS = "Av. Providencia 1000";

    /**
     * Teléfono de la farmacia de prueba.
     */
    private static final String SAMPLE_PHARMACY_PHONE = "912345678";

    /**
     * Latitud de la ubicación de prueba.
     */
    private static final Double SAMPLE_LATITUDE = -33.456789;

    /**
     * Longitud de la ubicación de prueba.
     */
    private static final Double SAMPLE_LONGITUDE = -70.654321;

    /**
     * Hora de inicio del turno de prueba.
     */
    private static final LocalTime SAMPLE_START_TIME = LocalTime.of(8, 30);

    /**
     * Hora de fin del turno de prueba.
     */
    private static final LocalTime SAMPLE_END_TIME = LocalTime.of(22, 0);

    /**
     * Nombre de la comuna de prueba.
     */
    private static final String SAMPLE_COMMUNE_NAME = "Santiago";

    /**
     * Nombre de la localidad de prueba.
     */
    private static final String SAMPLE_LOCALITY_NAME = "Providencia";

    /**
     * Identificador de región de prueba.
     */
    private static final Long SAMPLE_REGION_ID = 8L;

    /**
     * Identificador de comuna de prueba.
     */
    private static final Long SAMPLE_COMMUNE_ID = 101L;

    /**
     * Identificador de localidad de prueba.
     */
    private static final Long SAMPLE_LOCALITY_ID = 5L;

    /**
     * Día de operación de prueba.
     */
    private static final String SAMPLE_OPERATION_DAY = "Jueves";

    // -------------------------------------------------------------------------
    // Estado inicial del DTO
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica que el constructor por defecto inicializa todas las
     * propiedades del DTO a {@code null}.
     * <p>
     * Esta prueba asegura que no haya valores predeterminados no deseados que
     * puedan interferir con la lógica de negocio.
     * </p>
     */
    @Test
    @DisplayName("Constructor por defecto: inicializa todas las propiedades en null")
    void shouldInitializeAllPropertiesAsNullWhenCreatedWithDefaultConstructor() {
        // Arrange
        // (No requiere configuración; el constructor por defecto es el SUT)

        // Act
        FarmaciaTurno farmaciaTurno = new FarmaciaTurno();

        // Assert
        Assertions.assertNull(farmaciaTurno.getDate(), "date debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getStoreId(), "storeId debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getPharmacyName(), "pharmacyName debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getPharmacyAddress(), "pharmacyAddress debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getPharmacyPhone(), "pharmacyPhone debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getLatitude(), "latitude debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getLongitude(), "longitude debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getStartTime(), "startTime debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getEndTime(), "endTime debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getCommuneName(), "communeName debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getLocalityName(), "localityName debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getRegionId(), "regionId debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getCommuneId(), "communeId debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getLocalityId(), "localityId debe ser null inicialmente");
        Assertions.assertNull(farmaciaTurno.getOperationDay(), "operationDay debe ser null inicialmente");
    }

    // -------------------------------------------------------------------------
    // Ciclo de vida completo del DTO
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica el ciclo completo de asignación y recuperación de
     * todas las propiedades del DTO.
     * <p>
     * Asigna valores a todas las propiedades y luego verifica que cada getter
     * retorne el valor correcto, asegurando que no haya interferencia entre
     * campos (por ejemplo, que un setter no sobrescriba accidentalmente otro).
     * </p>
     */
    @Test
    @DisplayName("Propiedades: almacena y recupera todos los valores sin interferencia entre campos")
    void shouldStoreAndRetrieveAllPropertiesWithoutInterference() {
        // Arrange
        FarmaciaTurno farmaciaTurno = new FarmaciaTurno();

        // Act - Poblado completo del DTO
        farmaciaTurno.setDate(SAMPLE_DATE);
        farmaciaTurno.setStoreId(SAMPLE_STORE_ID);
        farmaciaTurno.setPharmacyName(SAMPLE_PHARMACY_NAME);
        farmaciaTurno.setPharmacyAddress(SAMPLE_PHARMACY_ADDRESS);
        farmaciaTurno.setPharmacyPhone(SAMPLE_PHARMACY_PHONE);
        farmaciaTurno.setLatitude(SAMPLE_LATITUDE);
        farmaciaTurno.setLongitude(SAMPLE_LONGITUDE);
        farmaciaTurno.setStartTime(SAMPLE_START_TIME);
        farmaciaTurno.setEndTime(SAMPLE_END_TIME);
        farmaciaTurno.setCommuneName(SAMPLE_COMMUNE_NAME);
        farmaciaTurno.setLocalityName(SAMPLE_LOCALITY_NAME);
        farmaciaTurno.setRegionId(SAMPLE_REGION_ID);
        farmaciaTurno.setCommuneId(SAMPLE_COMMUNE_ID);
        farmaciaTurno.setLocalityId(SAMPLE_LOCALITY_ID);
        farmaciaTurno.setOperationDay(SAMPLE_OPERATION_DAY);

        // Assert - Verifica que ningún setter sobrescribió accidentalmente otro campo
        Assertions.assertEquals(SAMPLE_DATE, farmaciaTurno.getDate(), "date incorrecto");
        Assertions.assertEquals(SAMPLE_STORE_ID, farmaciaTurno.getStoreId(), "storeId incorrecto");
        Assertions.assertEquals(SAMPLE_PHARMACY_NAME, farmaciaTurno.getPharmacyName(), "pharmacyName incorrecto");
        Assertions.assertEquals(SAMPLE_PHARMACY_ADDRESS, farmaciaTurno.getPharmacyAddress(), "pharmacyAddress incorrecta");
        Assertions.assertEquals(SAMPLE_PHARMACY_PHONE, farmaciaTurno.getPharmacyPhone(), "pharmacyPhone incorrecto");
        Assertions.assertEquals(SAMPLE_LATITUDE, farmaciaTurno.getLatitude(), "latitude incorrecta");
        Assertions.assertEquals(SAMPLE_LONGITUDE, farmaciaTurno.getLongitude(), "longitude incorrecta");
        Assertions.assertEquals(SAMPLE_START_TIME, farmaciaTurno.getStartTime(), "startTime incorrecto");
        Assertions.assertEquals(SAMPLE_END_TIME, farmaciaTurno.getEndTime(), "endTime incorrecto");
        Assertions.assertEquals(SAMPLE_COMMUNE_NAME, farmaciaTurno.getCommuneName(), "communeName incorrecto");
        Assertions.assertEquals(SAMPLE_LOCALITY_NAME, farmaciaTurno.getLocalityName(), "localityName incorrecto");
        Assertions.assertEquals(SAMPLE_REGION_ID, farmaciaTurno.getRegionId(), "regionId incorrecto");
        Assertions.assertEquals(SAMPLE_COMMUNE_ID, farmaciaTurno.getCommuneId(), "communeId incorrecto");
        Assertions.assertEquals(SAMPLE_LOCALITY_ID, farmaciaTurno.getLocalityId(), "localityId incorrecto");
        Assertions.assertEquals(SAMPLE_OPERATION_DAY, farmaciaTurno.getOperationDay(), "operationDay incorrecto");
    }

    // -------------------------------------------------------------------------
    // Comportamiento específico: coherencia temporal del turno
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica que las propiedades de hora de inicio y fin del turno
     * son independientes y se mantienen correctamente.
     * <p>
     * Además, comprueba que en un turno válido, la hora de inicio es
     * cronológicamente anterior a la hora de fin.
     * </p>
     */
    @Test
    @DisplayName("Horario de turno: startTime y endTime son independientes y preservan su valor")
    void shouldKeepStartAndEndTimeIndependent() {
        // Arrange
        FarmaciaTurno farmaciaTurno = new FarmaciaTurno();

        // Act
        farmaciaTurno.setStartTime(SAMPLE_START_TIME);
        farmaciaTurno.setEndTime(SAMPLE_END_TIME);

        // Assert
        Assertions.assertEquals(SAMPLE_START_TIME, farmaciaTurno.getStartTime(),
                "startTime debe conservarse tras asignar endTime");
        Assertions.assertEquals(SAMPLE_END_TIME, farmaciaTurno.getEndTime(),
                "endTime debe conservarse tras asignación");
        Assertions.assertTrue(farmaciaTurno.getStartTime().isBefore(farmaciaTurno.getEndTime()),
                "startTime debe ser cronológicamente anterior a endTime en un turno válido");
    }
}
