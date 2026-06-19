package cl.sebastian.cm.scheduler.manager;

import cl.sebastian.cm.scheduler.domain.data.FarmaciaTurno;
import cl.sebastian.cm.scheduler.domain.enums.Commerce;
import cl.sebastian.cm.scheduler.domain.model.Pharmacy;
import cl.sebastian.cm.scheduler.domain.model.PharmacyOnDuty;
import cl.sebastian.cm.scheduler.domain.repository.PharmacyOnDutyRepository;
import cl.sebastian.cm.scheduler.domain.repository.PharmacyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Pruebas unitarias para el servicio {@link PharmaManager}.
 * <p>
 * Verifica la lógica de negocio para la gestión de farmacias y turnos,
 * incluyendo la creación y reutilización de entidades, deduplicación,
 * validaciones de entrada y transformaciones de datos (normalización de
 * nombres, detección de comercio, formato telefónico, construcción de
 * direcciones, etc.).
 * </p>
 * <p>
 * Las pruebas utilizan {@link Mockito} para aislar las dependencias
 * ({@link PharmacyRepository} y {@link PharmacyOnDutyRepository}) y se centran
 * en el comportamiento del método {@link PharmaManager#save(FarmaciaTurno)}.
 * </p>
 * <p>
 * <strong>Cobertura de escenarios:</strong>
 * </p>
 * <ul>
 * <li>Creación de farmacia y turno cuando no existen.</li>
 * <li>Reutilización de farmacia existente y creación solo de turno.</li>
 * <li>Omisión de creación cuando el turno ya existe.</li>
 * <li>Validaciones de entrada (storeId nulo, fecha nula, objeto nulo).</li>
 * <li>Transformaciones de datos: normalización a mayúsculas, detección de
 * comercio, normalización telefónica, construcción de dirección, preservación
 * de coordenadas y horarios.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see PharmaManager
 * @see FarmaciaTurno
 */
@DisplayName("PharmaManager - Gestión de farmacias y turnos")
@ExtendWith(MockitoExtension.class)
class PharmaManagerTests {

    // -------------------------------------------------------------------------
    // Constantes de prueba (elimina números mágicos del flujo de tests)
    // -------------------------------------------------------------------------
    /**
     * Identificador de tienda de prueba.
     */
    private static final Integer SAMPLE_STORE_ID = 12345;

    /**
     * Identificador de farmacia de prueba.
     */
    private static final Long SAMPLE_PHARMACY_ID = 1L;

    /**
     * Identificador de turno de prueba.
     */
    private static final Long SAMPLE_DUTY_ID = 100L;

    /**
     * Fecha de turno de prueba.
     */
    private static final LocalDate SAMPLE_DATE = LocalDate.of(2026, 6, 20);

    /**
     * Nombre de la farmacia de prueba.
     */
    private static final String SAMPLE_PHARMACY_NAME = "Farmacia AHUMADA";

    /**
     * Dirección de la farmacia de prueba.
     */
    private static final String SAMPLE_PHARMACY_ADDRESS = "Av. Providencia 1000";

    /**
     * Localidad de prueba.
     */
    private static final String SAMPLE_LOCALITY = "Providencia";

    /**
     * Comuna de prueba.
     */
    private static final String SAMPLE_COMMUNE = "Santiago";

    /**
     * Teléfono de prueba.
     */
    private static final String SAMPLE_PHONE = "912345678";

    /**
     * Latitud de prueba.
     */
    private static final Double SAMPLE_LATITUDE = -33.456789;

    /**
     * Longitud de prueba.
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
     * Teléfono normalizado esperado (formato internacional).
     */
    private static final Long SAMPLE_NORMALIZED_PHONE = 56912345678L;

    /**
     * Teléfono inválido esperado (0L).
     */
    private static final Long INVALID_PHONE_AS_ZERO = 0L;

    // -------------------------------------------------------------------------
    // Mocks e Inyección
    // -------------------------------------------------------------------------
    /**
     * Mock del repositorio de farmacias.
     */
    @Mock
    private PharmacyRepository pharmacyRepository;

    /**
     * Mock del repositorio de turnos.
     */
    @Mock
    private PharmacyOnDutyRepository pharmacyOnDutyRepository;

    /**
     * Instancia del servicio bajo prueba con los mocks inyectados.
     */
    @InjectMocks
    private PharmaManager pharmaManager;

    // -------------------------------------------------------------------------
    // Builder de datos de prueba
    // -------------------------------------------------------------------------
    /**
     * Construye un objeto {@link FarmaciaTurno} válido con datos de prueba.
     *
     * @return una instancia de {@link FarmaciaTurno} completamente poblada.
     */
    private FarmaciaTurno buildValidFarmaciaTurno() {
        FarmaciaTurno farmaciaTurno = new FarmaciaTurno();
        farmaciaTurno.setStoreId(SAMPLE_STORE_ID);
        farmaciaTurno.setDate(SAMPLE_DATE);
        farmaciaTurno.setPharmacyName(SAMPLE_PHARMACY_NAME);
        farmaciaTurno.setPharmacyAddress(SAMPLE_PHARMACY_ADDRESS);
        farmaciaTurno.setLocalityName(SAMPLE_LOCALITY);
        farmaciaTurno.setCommuneName(SAMPLE_COMMUNE);
        farmaciaTurno.setPharmacyPhone(SAMPLE_PHONE);
        farmaciaTurno.setLatitude(SAMPLE_LATITUDE);
        farmaciaTurno.setLongitude(SAMPLE_LONGITUDE);
        farmaciaTurno.setStartTime(SAMPLE_START_TIME);
        farmaciaTurno.setEndTime(SAMPLE_END_TIME);
        return farmaciaTurno;
    }

    /**
     * Configura el stub del repositorio para simular persistencia: asigna un ID
     * a la entidad capturada y la retorna. Este stub NO contiene aserciones
     * (responsabilidad única).
     */
    private void stubPharmacyRepositorySaveWithIdAssignment() {
        Mockito.when(pharmacyRepository.save(Mockito.any(Pharmacy.class)))
                .thenAnswer(invocation -> {
                    Pharmacy pharmacy = invocation.getArgument(0);
                    pharmacy.setId(SAMPLE_PHARMACY_ID);
                    return pharmacy;
                });
    }

    /**
     * Stub para simular que la farmacia no existe.
     */
    private void stubPharmacyNotFound() {
        Mockito.when(pharmacyRepository.findByStoreId(SAMPLE_STORE_ID)).thenReturn(null);
    }

    /**
     * Stub para simular que el turno no existe para una farmacia y fecha dadas.
     *
     * @param pharmacy farmacia sobre la que se busca el turno.
     */
    private void stubDutyNotFound(Pharmacy pharmacy) {
        Mockito.when(pharmacyOnDutyRepository.findByPharmacyAndDutyDate(pharmacy, SAMPLE_DATE))
                .thenReturn(null);
    }

    /**
     * Stub para simular que el turno no existe para cualquier farmacia y fecha.
     */
    private void stubDutyNotFoundForAny() {
        Mockito.when(pharmacyOnDutyRepository.findByPharmacyAndDutyDate(Mockito.any(), Mockito.any()))
                .thenReturn(null);
    }

    // -------------------------------------------------------------------------
    // Escenarios de creación y deduplicación
    // -------------------------------------------------------------------------
    /**
     * Verifica que cuando no existe ni la farmacia ni el turno, se crean ambos.
     */
    @Test
    @DisplayName("save: crea nueva farmacia y turno cuando no existen previamente")
    void shouldCreatePharmacyAndDutyWhenNeitherExists() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        stubPharmacyNotFound();
        stubPharmacyRepositorySaveWithIdAssignment();
        stubDutyNotFoundForAny();

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository).save(Mockito.any(Pharmacy.class));
        Mockito.verify(pharmacyOnDutyRepository).save(Mockito.any(PharmacyOnDuty.class));
    }

    /**
     * Verifica que cuando la farmacia ya existe, se reutiliza y solo se crea el
     * turno.
     */
    @Test
    @DisplayName("save: reutiliza farmacia existente y solo crea turno nuevo")
    void shouldReuseExistingPharmacyAndCreateOnlyDuty() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        Pharmacy existingPharmacy = new Pharmacy();
        existingPharmacy.setId(SAMPLE_PHARMACY_ID);
        existingPharmacy.setStoreId(SAMPLE_STORE_ID);

        Mockito.when(pharmacyRepository.findByStoreId(SAMPLE_STORE_ID)).thenReturn(existingPharmacy);
        stubDutyNotFound(existingPharmacy);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository, Mockito.never()).save(Mockito.any(Pharmacy.class));
        Mockito.verify(pharmacyOnDutyRepository).save(Mockito.any(PharmacyOnDuty.class));
    }

    /**
     * Verifica que cuando el turno ya existe para la fecha, no se crea uno
     * nuevo.
     */
    @Test
    @DisplayName("save: omite creación cuando el turno ya existe para esa fecha")
    void shouldSkipDutyCreationWhenAlreadyExistsForDate() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        Pharmacy existingPharmacy = new Pharmacy();
        existingPharmacy.setId(SAMPLE_PHARMACY_ID);
        existingPharmacy.setStoreId(SAMPLE_STORE_ID);

        PharmacyOnDuty existingDuty = new PharmacyOnDuty();
        existingDuty.setId(SAMPLE_DUTY_ID);

        Mockito.when(pharmacyRepository.findByStoreId(SAMPLE_STORE_ID)).thenReturn(existingPharmacy);
        Mockito.when(pharmacyOnDutyRepository.findByPharmacyAndDutyDate(existingPharmacy, SAMPLE_DATE))
                .thenReturn(existingDuty);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyOnDutyRepository, Mockito.never()).save(Mockito.any(PharmacyOnDuty.class));
    }

    // -------------------------------------------------------------------------
    // Escenarios de validación de entrada (guardas)
    // -------------------------------------------------------------------------
    /**
     * Verifica que se lanza {@link IllegalArgumentException} cuando el
     * {@code storeId} es null.
     */
    @Test
    @DisplayName("save: lanza IllegalArgumentException cuando storeId es null")
    void shouldThrowIllegalArgumentExceptionWhenStoreIdIsNull() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        farmaciaTurno.setStoreId(null);

        // Act & Assert
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> pharmaManager.save(farmaciaTurno),
                "storeId null debe provocar IllegalArgumentException"
        );
    }

    /**
     * Verifica que el método ignora silenciosamente cuando el DTO es null.
     */
    @Test
    @DisplayName("save: ignora silenciosamente cuando FarmaciaTurno es null")
    void shouldSilentlyIgnoreWhenFarmaciaTurnoIsNull() {
        // Arrange
        // (No requiere configuración; null es el SUT)

        // Act
        pharmaManager.save(null);

        // Assert
        Mockito.verify(pharmacyRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(pharmacyOnDutyRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Verifica que el método ignora silenciosamente cuando la fecha es null.
     */
    @Test
    @DisplayName("save: ignora silenciosamente cuando la fecha es null")
    void shouldSilentlyIgnoreWhenDateIsNull() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        farmaciaTurno.setDate(null);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(pharmacyOnDutyRepository, Mockito.never()).save(Mockito.any());
    }

    // -------------------------------------------------------------------------
    // Escenarios de transformación de datos (lógica de negocio)
    // -------------------------------------------------------------------------
    /**
     * Verifica que el nombre de la farmacia se normaliza a mayúsculas.
     */
    @Test
    @DisplayName("save: normaliza el nombre de farmacia a mayúsculas")
    void shouldNormalizePharmacyNameToUpperCase() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        farmaciaTurno.setPharmacyName("farmacia ahumada");
        stubPharmacyNotFound();
        stubPharmacyRepositorySaveWithIdAssignment();
        stubDutyNotFoundForAny();

        ArgumentCaptor<Pharmacy> pharmacyCaptor = ArgumentCaptor.forClass(Pharmacy.class);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository).save(pharmacyCaptor.capture());
        Pharmacy capturedPharmacy = pharmacyCaptor.getValue();
        Assertions.assertEquals("FARMACIA AHUMADA", capturedPharmacy.getName(),
                "El nombre debe normalizarse a mayúsculas");
    }

    /**
     * Verifica que se detecta correctamente el comercio AHUMADA a partir del
     * nombre.
     */
    @Test
    @DisplayName("save: detecta cadena AHUMADA a partir del nombre de farmacia")
    void shouldDetectAhumadaCommerceFromPharmacyName() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        farmaciaTurno.setPharmacyName("Farmacia AHUMADA Santiago");
        stubPharmacyNotFound();
        stubPharmacyRepositorySaveWithIdAssignment();
        stubDutyNotFoundForAny();

        ArgumentCaptor<Pharmacy> pharmacyCaptor = ArgumentCaptor.forClass(Pharmacy.class);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository).save(pharmacyCaptor.capture());
        Pharmacy capturedPharmacy = pharmacyCaptor.getValue();
        Assertions.assertEquals(Commerce.AHUMADA, capturedPharmacy.getCommerce(),
                "El commerce debe detectarse como AHUMADA desde el nombre");
    }

    /**
     * Verifica que el teléfono se normaliza al formato internacional chileno.
     */
    @Test
    @DisplayName("save: normaliza teléfono al formato internacional chileno")
    void shouldNormalizePhoneToChileanInternationalFormat() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        farmaciaTurno.setPharmacyPhone("9-1234-5678");
        stubPharmacyNotFound();
        stubPharmacyRepositorySaveWithIdAssignment();
        stubDutyNotFoundForAny();

        ArgumentCaptor<Pharmacy> pharmacyCaptor = ArgumentCaptor.forClass(Pharmacy.class);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository).save(pharmacyCaptor.capture());
        Pharmacy capturedPharmacy = pharmacyCaptor.getValue();
        Assertions.assertEquals(SAMPLE_NORMALIZED_PHONE, capturedPharmacy.getPhone(),
                "El teléfono debe normalizarse al formato 569XXXXXXXX");
    }

    /**
     * Verifica que se asigna 0L cuando el teléfono no es válido.
     */
    @Test
    @DisplayName("save: asigna 0L cuando el teléfono no es válido")
    void shouldAssignZeroWhenPhoneIsInvalid() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        farmaciaTurno.setPharmacyPhone("227777777");
        stubPharmacyNotFound();
        stubPharmacyRepositorySaveWithIdAssignment();
        stubDutyNotFoundForAny();

        ArgumentCaptor<Pharmacy> pharmacyCaptor = ArgumentCaptor.forClass(Pharmacy.class);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository).save(pharmacyCaptor.capture());
        Pharmacy capturedPharmacy = pharmacyCaptor.getValue();
        Assertions.assertEquals(INVALID_PHONE_AS_ZERO, capturedPharmacy.getPhone(),
                "Teléfono inválido debe resultar en 0L");
    }

    /**
     * Verifica que la dirección se compone de calle, localidad y comuna.
     */
    @Test
    @DisplayName("save: construye dirección compuesta con calle, localidad y comuna")
    void shouldBuildCompositeAddressWithStreetLocalityAndCommune() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        stubPharmacyNotFound();
        stubPharmacyRepositorySaveWithIdAssignment();
        stubDutyNotFoundForAny();

        ArgumentCaptor<Pharmacy> pharmacyCaptor = ArgumentCaptor.forClass(Pharmacy.class);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository).save(pharmacyCaptor.capture());
        Pharmacy capturedPharmacy = pharmacyCaptor.getValue();
        String address = capturedPharmacy.getAddress();

        Assertions.assertNotNull(address, "La dirección no debe ser null");
        Assertions.assertTrue(address.contains("AV. PROVIDENCIA"),
                "La dirección debe contener la calle normalizada");
        Assertions.assertTrue(address.contains("PROVIDENCIA"),
                "La dirección debe contener la localidad");
        Assertions.assertTrue(address.contains("SANTIAGO"),
                "La dirección debe contener la comuna");
    }

    /**
     * Verifica que las coordenadas geográficas se preservan sin transformación.
     */
    @Test
    @DisplayName("save: preserva coordenadas geográficas sin transformación")
    void shouldPreserveGeographicCoordinatesWithoutTransformation() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        stubPharmacyNotFound();
        stubPharmacyRepositorySaveWithIdAssignment();
        stubDutyNotFoundForAny();

        ArgumentCaptor<Pharmacy> pharmacyCaptor = ArgumentCaptor.forClass(Pharmacy.class);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository).save(pharmacyCaptor.capture());
        Pharmacy capturedPharmacy = pharmacyCaptor.getValue();
        Assertions.assertEquals(SAMPLE_LATITUDE, capturedPharmacy.getLatitude(),
                "La latitud debe preservarse sin transformación");
        Assertions.assertEquals(SAMPLE_LONGITUDE, capturedPharmacy.getLongitude(),
                "La longitud debe preservarse sin transformación");
    }

    /**
     * Verifica que los horarios de apertura y cierre se preservan.
     */
    @Test
    @DisplayName("save: preserva horarios de apertura y cierre del turno")
    void shouldPreserveTurnOperatingHours() {
        // Arrange
        FarmaciaTurno farmaciaTurno = buildValidFarmaciaTurno();
        stubPharmacyNotFound();
        stubPharmacyRepositorySaveWithIdAssignment();
        stubDutyNotFoundForAny();

        ArgumentCaptor<Pharmacy> pharmacyCaptor = ArgumentCaptor.forClass(Pharmacy.class);

        // Act
        pharmaManager.save(farmaciaTurno);

        // Assert
        Mockito.verify(pharmacyRepository).save(pharmacyCaptor.capture());
        Pharmacy capturedPharmacy = pharmacyCaptor.getValue();
        Assertions.assertEquals(SAMPLE_START_TIME, capturedPharmacy.getStartTime(),
                "El horario de apertura debe preservarse");
        Assertions.assertEquals(SAMPLE_END_TIME, capturedPharmacy.getEndTime(),
                "El horario de cierre debe preservarse");
    }
}
