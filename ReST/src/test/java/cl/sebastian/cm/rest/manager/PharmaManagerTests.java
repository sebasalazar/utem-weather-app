package cl.sebastian.cm.rest.manager;

import cl.sebastian.cm.rest.domain.data.IdDistance;
import cl.sebastian.cm.rest.domain.model.Pharmacy;
import cl.sebastian.cm.rest.domain.model.PharmacyOnDuty;
import cl.sebastian.cm.rest.domain.repository.PharmacyOnDutyRepository;
import cl.sebastian.cm.rest.domain.repository.PharmacyRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
 * Pruebas unitarias de {@link PharmaManager}.
 *
 * <p>
 * Valida la lógica de negocio para encontrar la farmacia de turno más cercana a
 * una ubicación geográfica dada, considerando:</p>
 * <ul>
 * <li>Validación de coordenadas geográficas</li>
 * <li>Horario laboral de las farmacias</li>
 * <li>Turnos de emergencia (días festivos y fines de semana)</li>
 * <li>Distancia máxima de búsqueda configurable</li>
 * </ul>
 *
 * <h3>Glosario para lectores no técnicos</h3>
 * <ul>
 * <li><b>Farmacia de turno:</b> Farmacia que atiende fuera de su horario
 * habitual en días específicos (generalmente domingos y festivos), regulado por
 * la autoridad sanitaria.</li>
 * <li><b>Horario laboral:</b> Período diario en que una farmacia opera
 * normalmente (ej: 08:00 a 18:00). Fuera de este horario, solo atienden
 * farmacias de turno.</li>
 * <li><b>Distancia máxima:</b> Radio máximo (en metros) dentro del cual se
 * buscan farmacias. Valor por defecto: 1 metro (configurable).</li>
 * <li><b>IdDistance:</b> Estructura que contiene el identificador de una
 * farmacia y su distancia (en metros) respecto al punto de búsqueda.</li>
 * </ul>
 *
 * <p>
 * Estructura: los escenarios se agrupan por responsabilidad mediante clases
 * {@code @Nested} y se parametrizan cuando comparten la misma aserción,
 * siguiendo la convención Arrange-Act-Assert (AAA).</p>
 */
@ExtendWith(MockitoExtension.class)
class PharmaManagerTests {

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PharmacyOnDutyRepository pharmacyOnDutyRepository;

    private PharmaManager pharmaManager;

    @BeforeEach
    void setUp() {
        pharmaManager = new PharmaManager(pharmacyRepository, pharmacyOnDutyRepository, 15000);
    }

    // -------------------------------------------------------------------------
    // Factory methods para creación de objetos de prueba
    // -------------------------------------------------------------------------
    private Pharmacy crearFarmacia(long id, LocalTime horaInicio, LocalTime horaFin) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(id);
        pharmacy.setName("Farmacia " + id);
        pharmacy.setStartTime(horaInicio);
        pharmacy.setEndTime(horaFin);
        return pharmacy;
    }

    private LocalDateTime crearDateTime(int anio, int mes, int dia, int hora, int minuto) {
        return LocalDateTime.of(anio, mes, dia, hora, minuto);
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
            PharmaManager manager = new PharmaManager(
                    pharmacyRepository, pharmacyOnDutyRepository, null);

            // Assert
            org.junit.jupiter.api.Assertions.assertEquals(
                    1,
                    manager.getMaxDistance(),
                    "La distancia máxima por defecto debe ser 1 metro"
            );
        }

        @Test
        @DisplayName("Debe usar la distancia máxima especificada cuando es válida")
        void constructorConDistanciaValidaDebeUsarValorEspecifico() {
            // Arrange & Act
            PharmaManager manager = new PharmaManager(
                    pharmacyRepository, pharmacyOnDutyRepository, 5000);

            // Assert
            org.junit.jupiter.api.Assertions.assertEquals(
                    5000,
                    manager.getMaxDistance(),
                    "La distancia máxima debe ser 5000 metros"
            );
        }
    }

    // -------------------------------------------------------------------------
    // Validación de entrada: coordenadas y fecha/hora
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Validación de entrada: coordenadas y fecha/hora")
    class ValidacionEntradaTests {

        @Test
        @DisplayName("Debe retornar vacío cuando la fecha/hora es nula")
        void getNearbyOnDutyConDateTimeNuloDebeRetornarVacio() {
            // Arrange
            double latitud = -33.4;
            double longitud = -70.6;
            LocalDateTime dateTimeNulo = null;

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, dateTimeNulo);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando la fecha/hora es nula"
            );
            org.mockito.Mockito.verify(
                    pharmacyRepository,
                    org.mockito.Mockito.never()
            ).searchByDistance(
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyDouble(),
                    org.mockito.ArgumentMatchers.anyLong()
            );
        }

        @Test
        @DisplayName("Debe retornar vacío cuando las coordenadas son inválidas")
        void getNearbyOnDutyConCoordenadasInvalidasDebeRetornarVacio() {
            // Arrange
            double latitudInvalida = 91.0;
            double longitud = 0.0;
            LocalDateTime dateTime = LocalDateTime.now();

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitudInvalida, longitud, dateTime);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando las coordenadas son inválidas"
            );
            org.mockito.Mockito.verify(
                    pharmacyRepository,
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
    @DisplayName("Búsqueda sin farmacias encontradas")
    class BusquedaSinResultadosTests {

        @Test
        @DisplayName("Debe retornar vacío cuando no hay farmacias en el radio de búsqueda")
        void getNearbyOnDutySinFarmaciasCercanasDebeRetornarVacio() {
            // Arrange
            double latitud = -33.4;
            double longitud = -70.6;
            LocalDateTime dateTime = LocalDateTime.now();
            long distanciaMaxima = 15000L;

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(Collections.emptyList());

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, dateTime);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando no hay farmacias cercanas"
            );
            org.mockito.Mockito.verify(pharmacyRepository).searchByDistance(
                    latitud, longitud, distanciaMaxima);
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la farmacia encontrada no existe en BD")
        void getNearbyOnDutyConFarmaciaNoExistenteDebeRetornarVacio() {
            // Arrange
            double latitud = -33.4;
            double longitud = -70.6;
            LocalDateTime dateTime = LocalDateTime.now();
            long distanciaMaxima = 15000L;
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.empty());

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, dateTime);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "El resultado debe estar vacío cuando la farmacia no existe"
            );
            org.mockito.Mockito.verify(pharmacyRepository).searchByDistance(
                    latitud, longitud, distanciaMaxima);
            org.mockito.Mockito.verify(pharmacyRepository).findById(1L);
        }
    }

    // -------------------------------------------------------------------------
    // Farmacia en horario laboral
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Farmacia disponible en horario laboral")
    class FarmaciaEnHorarioLaboralTests {

        @Test
        @DisplayName("Debe retornar farmacia cuando está dentro de su horario laboral")
        void getNearbyOnDutyEnHorarioLaboralDebeRetornarFarmacia() {
            // Arrange
            LocalDateTime lunes10am = crearDateTime(2024, 1, 8, 10, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunes10am);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    "Debe retornar la farmacia en horario laboral"
            );
            org.junit.jupiter.api.Assertions.assertEquals(
                    1L,
                    resultado.get().getId(),
                    "El ID de la farmacia debe ser 1"
            );
            org.mockito.Mockito.verify(pharmacyRepository).searchByDistance(
                    latitud, longitud, distanciaMaxima);
            org.mockito.Mockito.verify(pharmacyRepository).findById(1L);
        }

        @ParameterizedTest(name = "hora={0}:{1} debe estar dentro del horario laboral")
        @CsvSource({
            // Límite inferior (hora de apertura)
            "8,  0",
            // Hora intermedia
            "12, 30",
            // Una hora antes del cierre
            "17, 0"
        })
        @DisplayName("Debe retornar farmacia en diferentes horarios dentro del rango laboral")
        void getNearbyOnDutyEnDiferentesHorariosLaboralesDebeRetornarFarmacia(
                int hora, int minuto) {
            // Arrange
            LocalDateTime lunes = crearDateTime(2024, 1, 8, hora, minuto);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunes);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    String.format("Debe retornar farmacia a las %02d:%02d", hora, minuto)
            );
        }
    }

    // -------------------------------------------------------------------------
    // Límites del horario laboral
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Límites del horario laboral (casos borde)")
    class LimitesHorarioLaboralTests {

        @Test
        @DisplayName("Debe retornar farmacia exactamente en la hora de apertura")
        void getNearbyOnDutyEnHoraExactaDeAperturaDebeRetornarFarmacia() {
            // Arrange
            LocalDateTime lunes8am = crearDateTime(2024, 1, 8, 8, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunes8am);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    "Debe retornar farmacia exactamente en la hora de apertura"
            );
        }

        @Test
        @DisplayName("Debe retornar vacío exactamente en la hora de cierre")
        void getNearbyOnDutyEnHoraExactaDeCierreDebeRetornarVacio() {
            // Arrange
            LocalDateTime lunes6pm = crearDateTime(2024, 1, 8, 18, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunes6pm);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "Debe retornar vacío exactamente en la hora de cierre"
            );
        }

        @Test
        @DisplayName("Debe retornar vacío una hora antes de la apertura")
        void getNearbyOnDutyAntesDeAperturaDebeRetornarVacio() {
            // Arrange
            LocalDateTime lunes7am = crearDateTime(2024, 1, 8, 7, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));
            org.mockito.Mockito.when(
                    pharmacyOnDutyRepository.findByPharmacyAndDutyDate(
                            farmacia, lunes7am.toLocalDate())
            ).thenReturn(null);

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunes7am);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "Debe retornar vacío antes de la hora de apertura"
            );
            org.mockito.Mockito.verify(pharmacyOnDutyRepository).findByPharmacyAndDutyDate(
                    farmacia, lunes7am.toLocalDate());
        }
    }

    // -------------------------------------------------------------------------
    // Farmacia fuera de horario laboral con turno de emergencia
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Farmacia con turno de emergencia (fuera de horario laboral)")
    class FarmaciaConTurnoEmergenciaTests {

        @Test
        @DisplayName("Debe retornar farmacia de turno en domingo fuera de horario laboral")
        void getNearbyOnDutyEnDomingoConTurnoDebeRetornarFarmacia() {
            // Arrange
            LocalDateTime domingo = crearDateTime(2024, 1, 7, 10, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            PharmacyOnDuty turno = new PharmacyOnDuty();
            turno.setPharmacy(farmacia);
            turno.setDutyDate(domingo.toLocalDate());
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));
            org.mockito.Mockito.when(
                    pharmacyOnDutyRepository.findByPharmacyAndDutyDate(
                            farmacia, domingo.toLocalDate())
            ).thenReturn(turno);

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, domingo);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    "Debe retornar farmacia de turno en domingo"
            );
            org.junit.jupiter.api.Assertions.assertEquals(
                    1L,
                    resultado.get().getId(),
                    "El ID de la farmacia debe ser 1"
            );
            org.mockito.Mockito.verify(pharmacyOnDutyRepository).findByPharmacyAndDutyDate(
                    farmacia, domingo.toLocalDate());
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la farmacia está fuera de horario y no tiene turno")
        void getNearbyOnDutyEnDomingoSinTurnoDebeRetornarVacio() {
            // Arrange
            LocalDateTime domingo = crearDateTime(2024, 1, 7, 10, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));
            org.mockito.Mockito.when(
                    pharmacyOnDutyRepository.findByPharmacyAndDutyDate(
                            farmacia, domingo.toLocalDate())
            ).thenReturn(null);

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, domingo);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "Debe retornar vacío cuando no hay turno de emergencia"
            );
            org.mockito.Mockito.verify(pharmacyOnDutyRepository).findByPharmacyAndDutyDate(
                    farmacia, domingo.toLocalDate());
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la farmacia está cerrada en sábado sin turno")
        void getNearbyOnDutyEnSabadoSinTurnoDebeRetornarVacio() {
            // Arrange
            LocalDateTime sabado = crearDateTime(2024, 1, 6, 10, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));
            org.mockito.Mockito.when(
                    pharmacyOnDutyRepository.findByPharmacyAndDutyDate(
                            farmacia, sabado.toLocalDate())
            ).thenReturn(null);

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, sabado);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "Debe retornar vacío en sábado sin turno"
            );
        }
    }

    // -------------------------------------------------------------------------
    // Selección de farmacia cuando hay múltiples candidatas
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Selección de farmacia entre múltiples candidatas")
    class SeleccionFarmaciaTests {

        @Test
        @DisplayName("Debe retornar la primera farmacia que cumple condiciones (no necesariamente la más cercana)")
        void getNearbyOnDutyConMultiplesFarmaciasDebeRetornarPrimeraValida() {
            // Arrange
            LocalDateTime lunes10am = crearDateTime(2024, 1, 8, 10, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia1 = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            Pharmacy farmacia2 = crearFarmacia(2L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            IdDistance id1 = crearIdDistance(1L, 10000);
            IdDistance id2 = crearIdDistance(2L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(id1, id2));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia1));

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunes10am);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    "Debe retornar una farmacia"
            );
            org.junit.jupiter.api.Assertions.assertEquals(
                    1L,
                    resultado.get().getId(),
                    "Debe retornar la primera farmacia válida (ID 1), no la más cercana"
            );
            org.mockito.Mockito.verify(pharmacyRepository).findById(1L);
            org.mockito.Mockito.verify(
                    pharmacyRepository,
                    org.mockito.Mockito.never()
            ).findById(2L);
        }

        @Test
        @DisplayName("Debe retornar farmacia válida cuando encuentra una que cumple condiciones")
        void getNearbyOnDutyConFarmaciaValidaDebeRetornarLaPrimera() {
            // Arrange: lunes a las 22:00 (fuera de horario laboral 08:00-18:00)
            LocalDateTime lunesAfueraDeHorario = crearDateTime(2024, 1, 8, 22, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), LocalTime.of(18, 0));
            PharmacyOnDuty turno = new PharmacyOnDuty();
            turno.setPharmacy(farmacia);
            turno.setDutyDate(lunesAfueraDeHorario.toLocalDate());
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));
            org.mockito.Mockito.when(
                    pharmacyOnDutyRepository.findByPharmacyAndDutyDate(
                            farmacia, lunesAfueraDeHorario.toLocalDate())
            ).thenReturn(turno);

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunesAfueraDeHorario);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isPresent(),
                    "Debe retornar una farmacia cuando tiene turno"
            );
            org.junit.jupiter.api.Assertions.assertEquals(
                    1L,
                    resultado.get().getId(),
                    "Debe retornar la farmacia con turno"
            );
            org.mockito.Mockito.verify(pharmacyRepository).findById(1L);
            org.mockito.Mockito.verify(pharmacyOnDutyRepository).findByPharmacyAndDutyDate(
                    farmacia, lunesAfueraDeHorario.toLocalDate());
        }
    }

    // -------------------------------------------------------------------------
    // Casos borde: configuración de horarios nulos
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Casos borde: configuración de horarios nulos")
    class CasosBordeHorariosNulosTests {

        @Test
        @DisplayName("Debe retornar vacío cuando la farmacia tiene hora de inicio nula")
        void getNearbyOnDutyConHoraInicioNulaDebeRetornarVacio() {
            // Arrange
            LocalDateTime lunes10am = crearDateTime(2024, 1, 8, 10, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, null, LocalTime.of(18, 0));
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));
            org.mockito.Mockito.when(
                    pharmacyOnDutyRepository.findByPharmacyAndDutyDate(
                            farmacia, lunes10am.toLocalDate())
            ).thenReturn(null);

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunes10am);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "Debe retornar vacío cuando la hora de inicio es nula"
            );
        }

        @Test
        @DisplayName("Debe retornar vacío cuando la farmacia tiene hora de fin nula")
        void getNearbyOnDutyConHoraFinNulaDebeRetornarVacio() {
            // Arrange
            LocalDateTime lunes10am = crearDateTime(2024, 1, 8, 10, 0);
            double latitud = -33.4;
            double longitud = -70.6;
            long distanciaMaxima = 15000L;
            Pharmacy farmacia = crearFarmacia(1L, LocalTime.of(8, 0), null);
            IdDistance idDistance = crearIdDistance(1L, 500);

            org.mockito.Mockito.when(
                    pharmacyRepository.searchByDistance(latitud, longitud, distanciaMaxima)
            ).thenReturn(List.of(idDistance));
            org.mockito.Mockito.when(pharmacyRepository.findById(1L))
                    .thenReturn(Optional.of(farmacia));

            // Act
            Optional<Pharmacy> resultado = pharmaManager.getNearbyOnDuty(
                    latitud, longitud, lunes10am);

            // Assert
            org.junit.jupiter.api.Assertions.assertTrue(
                    resultado.isEmpty(),
                    "Debe retornar vacío cuando la hora de fin es nula"
            );
        }
    }
}
