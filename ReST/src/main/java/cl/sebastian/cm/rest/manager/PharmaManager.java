package cl.sebastian.cm.rest.manager;

import cl.sebastian.cm.rest.domain.data.IdDistance;
import cl.sebastian.cm.rest.domain.model.Pharmacy;
import cl.sebastian.cm.rest.domain.model.PharmacyOnDuty;
import cl.sebastian.cm.rest.domain.repository.PharmacyOnDutyRepository;
import cl.sebastian.cm.rest.domain.repository.PharmacyRepository;
import cl.sebastian.cm.rest.utils.CoordinatesUtils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio para la búsqueda de farmacias de turno o en horario
 * laboral, más cercanas a un punto geográfico en una fecha y hora determinadas.
 * <p>
 * Este servicio encapsula la lógica de negocio que combina tres criterios:
 * </p>
 * <ol>
 * <li><strong>Proximidad geográfica:</strong> utiliza consultas espaciales con
 * PostGIS para encontrar farmacias dentro de un radio configurable alrededor de
 * las coordenadas proporcionadas.</li>
 * <li><strong>Horario laboral:</strong> verifica si la farmacia está en horario
 * de atención normal para el día y hora consultados (días de semana y rango
 * horario definido).</li>
 * <li><strong>Disponibilidad de turno:</strong> si la farmacia no está en
 * horario laboral, busca si tiene un turno registrado para la fecha
 * consultada.</li>
 * </ol>
 * <p>
 * La búsqueda se realiza en orden de distancia ascendente: se itera sobre las
 * farmacias más cercanas primero y se retorna la primera que cumpla con las
 * condiciones (en horario laboral o con turno en la fecha).
 * </p>
 *
 * <h2>Reglas de negocio</h2>
 * <ul>
 * <li>El radio máximo de búsqueda se configura externamente mediante la
 * propiedad {@code max.distance} (en metros), con un mínimo de 1 metro.</li>
 * <li>Las coordenadas deben ser válidas según el sistema WGS84 (latitud
 * {@code [-90, 90]}, longitud {@code [-180, 180]}).</li>
 * <li>La fecha y hora de consulta no puede ser nula; si lo es, se retorna
 * {@link Optional#empty()}.</li>
 * <li>El horario laboral se define por:
 * <ul>
 * <li>Días de semana (lunes a viernes).</li>
 * <li>Hora dentro del rango {@code [startTime, endTime]} de la farmacia.</li>
 * </ul>
 * </li>
 * <li>Si la farmacia no está en horario laboral, se verifica si tiene un turno
 * registrado para la fecha (ignorando la hora).</li>
 * <li>Se retorna la primera farmacia que cumpla cualquiera de las dos
 * condiciones, en orden de cercanía.</li>
 * </ul>
 *
 * <h2>Dependencias técnicas</h2>
 * <ul>
 * <li>Base de datos con soporte PostGIS para consultas espaciales.</li>
 * <li>Repositorios {@link PharmacyRepository} y
 * {@link PharmacyOnDutyRepository}.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see PharmacyRepository
 * @see PharmacyOnDutyRepository
 * @see CoordinatesUtils
 */
@Service
public class PharmaManager {

    /**
     * Logger de la clase.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PharmaManager.class);

    /**
     * Repositorio de farmacias.
     */
    private final PharmacyRepository pharmacyRepository;

    /**
     * Repositorio de turnos de farmacias.
     */
    private final PharmacyOnDutyRepository pharmacyOnDutyRepository;

    /**
     * Distancia máxima de búsqueda en metros. Se garantiza que sea al menos 1.
     */
    private final int maxDistance;

    /**
     * Construye el manager con los repositorios y la distancia máxima
     * configurada.
     * <p>
     * La distancia máxima se lee de la propiedad {@code max.distance} del
     * archivo de configuración. Si no está definida o es menor que 1, se usa 1
     * metro como valor por defecto.
     * </p>
     *
     * @param pharmacyRepository repositorio de farmacias (no puede ser
     * {@code null}).
     * @param pharmacyOnDutyRepository repositorio de turnos de farmacias (no
     * puede ser {@code null}).
     * @param maxDistance distancia máxima de búsqueda en metros; si es
     * {@code null} o menor que 1, se usa 1.
     */
    public PharmaManager(final PharmacyRepository pharmacyRepository,
            final PharmacyOnDutyRepository pharmacyOnDutyRepository,
            @Value("${max.distance:1}") final Integer maxDistance) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyOnDutyRepository = pharmacyOnDutyRepository;
        this.maxDistance = Math.max(1, maxDistance == null ? 1 : maxDistance);
        LOGGER.info("PharmaManager inicializado con distancia máxima: {} metros", this.maxDistance);
    }

    /**
     * Obtiene la distancia máxima de búsqueda configurada.
     *
     * @return la distancia máxima en metros.
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Verifica si una farmacia está en horario laboral para una fecha y hora
     * determinadas.
     * <p>
     * Una farmacia está en horario laboral si se cumplen ambas condiciones:
     * </p>
     * <ul>
     * <li>El día de la semana es de lunes a viernes (no sábado ni
     * domingo).</li>
     * <li>La hora se encuentra dentro del rango definido por {@code startTime}
     * (inclusive) y {@code endTime} (exclusivo).</li>
     * </ul>
     * <p>
     * Si la farmacia o la fecha/hora son {@code null}, retorna {@code false}.
     * </p>
     *
     * @param pharmacy la farmacia a evaluar (puede ser {@code null}).
     * @param ldt la fecha y hora de consulta (puede ser {@code null}).
     * @return {@code true} si la farmacia está en horario laboral,
     * {@code false} en caso contrario.
     */
    private boolean isLaboralTime(final Pharmacy pharmacy, final LocalDateTime ldt) {
        if (pharmacy == null || ldt == null) {
            return false;
        }

        final LocalTime time = ldt.toLocalTime();
        final DayOfWeek dow = ldt.getDayOfWeek();

        // Fines de semana no son horario laboral
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return false;
        }

        // Validar que startTime y endTime no sean null
        if (pharmacy.getStartTime() == null || pharmacy.getEndTime() == null) {
            LOGGER.warn("Farmacia id={} tiene startTime o endTime null", pharmacy.getId());
            return false;
        }

        // time >= startTime && time < endTime
        final boolean isAfterStart = !time.isBefore(pharmacy.getStartTime());
        final boolean isBeforeEnd = time.isBefore(pharmacy.getEndTime());

        return isAfterStart && isBeforeEnd;
    }

    /**
     * Obtiene una farmacia por su ID, verificando si está en horario laboral o
     * si tiene un turno registrado para la fecha de consulta.
     * <p>
     * El método realiza los siguientes pasos:
     * </p>
     * <ol>
     * <li>Busca la farmacia por ID en el repositorio.</li>
     * <li>Si existe, verifica si está en horario laboral mediante
     * {@link #isLaboralTime(Pharmacy, LocalDateTime)}.</li>
     * <li>Si está en horario laboral, retorna la farmacia.</li>
     * <li>Si no está en horario laboral, busca un turno para esa farmacia y la
     * fecha (ignorando la hora).</li>
     * <li>Si existe turno, retorna la farmacia; en caso contrario, retorna
     * {@code null}.</li>
     * </ol>
     *
     * @param pharmacyId el ID de la farmacia a consultar.
     * @param ldt la fecha y hora de consulta (no puede ser {@code null}).
     * @return la farmacia si cumple con las condiciones, o {@code null} si no
     * existe, no tiene horario laboral ni turno.
     */
    private Pharmacy getPharmacy(final long pharmacyId, final LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }

        Optional<Pharmacy> opt = pharmacyRepository.findById(pharmacyId);
        if (opt.isEmpty()) {
            return null;
        }

        Pharmacy pharmacy = opt.get();

        // Si está en horario laboral, retorna la farmacia
        if (isLaboralTime(pharmacy, ldt)) {
            return pharmacy;
        }

        // Si no está en horario laboral, busca turno para la fecha (sin hora)
        final PharmacyOnDuty pod = pharmacyOnDutyRepository.findByPharmacyAndDutyDate(pharmacy, ldt.toLocalDate());
        if (pod != null) {
            return pod.getPharmacy();
        }

        return null;
    }

    /**
     * Busca la farmacia más cercana que esté en horario laboral o tenga turno
     * para una fecha y hora determinadas.
     * <p>
     * El método realiza los siguientes pasos:
     * </p>
     * <ol>
     * <li>Valida que la fecha/hora no sea nula y que las coordenadas sean
     * válidas mediante {@link CoordinatesUtils#areValid(double, double)}.</li>
     * <li>Obtiene todas las farmacias dentro del radio configurado, ordenadas
     * por distancia ascendente (las más cercanas primero).</li>
     * <li>Para cada farmacia, invoca {@link #getPharmacy(long, LocalDateTime)}
     * para verificar si cumple con horario laboral o turno.</li>
     * <li>Retorna la primera farmacia que cumpla la condición.</li>
     * </ol>
     * <p>
     * Si no hay farmacias en el radio o ninguna cumple las condiciones, se
     * retorna {@link Optional#empty()}.
     * </p>
     *
     * @param latitude latitud del punto de referencia en grados decimales.
     * @param longitude longitud del punto de referencia en grados decimales.
     * @param ldt fecha y hora para la cual se consulta el turno y horario
     * laboral (no puede ser {@code null}).
     * @return la farmacia más cercana que cumpla las condiciones (si existe), o
     * {@link Optional#empty()} en caso contrario.
     * @see PharmacyRepository#searchByDistance(double, double, long)
     * @see PharmacyOnDutyRepository#findByPharmacyAndDutyDate(Pharmacy,
     * LocalDate)
     */
    public Optional<Pharmacy> getNearbyOnDuty(
            final double latitude,
            final double longitude,
            final LocalDateTime ldt) {

        // Validación de fecha/hora
        if (ldt == null) {
            LOGGER.warn("Se requiere una fecha de consulta");
            return Optional.empty();
        }

        // Validación de coordenadas
        if (!CoordinatesUtils.areValid(latitude, longitude)) {
            LOGGER.warn("Coordenadas inválidas recibidas: lat={}, lon={}", latitude, longitude);
            return Optional.empty();
        }

        // Búsqueda de farmacias dentro del radio
        List<IdDistance> list = pharmacyRepository.searchByDistance(latitude, longitude, maxDistance);
        if (CollectionUtils.isEmpty(list)) {
            LOGGER.warn("No hay farmacias cercanas en un rango de {} metros", maxDistance);
            return Optional.empty();
        }

        // Iteración para encontrar la primera que cumpla condiciones
        for (IdDistance current : list) {
            /**
             * Esta aproximación tiene el problema N+1 consultas. Existen varias
             * formas de hacerlo mejor, pero opto por esta porque me parece que
             * es más fácil de entender para un estudiante sin muchos
             * conocimientos en el lenguaje. Además que para la cantidad de
             * datos, no tiene un impacto fuerte de rendimiento.
             */
            Pharmacy pharmacy = getPharmacy(current.getId(), ldt);
            if (pharmacy != null) {
                LOGGER.debug("Farmacia encontrada: id={}, distancia={}m", current.getId(), current.getDistance());
                return Optional.of(pharmacy);
            }
        }

        LOGGER.info("No se encontraron farmacias en horario laboral o turno para {} en el radio de {} metros",
                ldt, maxDistance);
        return Optional.empty();
    }
}
