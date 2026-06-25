package cl.sebastian.cm.scheduler.manager;

import cl.sebastian.cm.scheduler.domain.data.RedMeteo;
import cl.sebastian.cm.scheduler.domain.model.Observation;
import cl.sebastian.cm.scheduler.domain.model.Station;
import cl.sebastian.cm.scheduler.domain.repository.ObservationRepository;
import cl.sebastian.cm.scheduler.domain.repository.StationRepository;
import cl.sebastian.cm.scheduler.utils.RmUtils;
import cl.sebastian.cm.scheduler.utils.TextUtils;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio de gestión de datos meteorológicos provenientes de RedMeteo.
 * <p>
 * Esta clase orquesta la lógica de negocio para persistir estaciones y
 * observaciones meteorológicas, garantizando la integridad referencial y
 * evitando duplicados.
 * </p>
 * <p>
 * <strong>Flujo principal ({@link #saveObs(RedMeteo)}):</strong>
 * </p>
 * <ol>
 * <li><b>Get-or-create Station</b>: Busca la estación por su código
 * (case-insensitive). Si no existe, la crea a partir de los datos del
 * {@link RedMeteo} y la persiste.</li>
 * <li><b>Validación de código de observación</b>: Normaliza el código de
 * observación a mayúsculas y verifica que no esté vacío.</li>
 * <li><b>Evitar duplicados</b>: Busca una observación existente para la
 * estación y código dados. Si ya existe, no se persiste y se retorna
 * {@code false}.</li>
 * <li><b>Creación y persistencia</b>: Construye una nueva observación con todos
 * los valores del DTO, la guarda en el repositorio y retorna {@code true}.</li>
 * </ol>
 * <p>
 * Además de la persistencia, proporciona métodos de consulta para obtener
 * estaciones por código y observaciones por estación.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see RedMeteo
 * @see Station
 * @see Observation
 * @see StationRepository
 * @see ObservationRepository
 */
@Service
public class MeteoManager {

    /**
     * Logger de la clase para registrar eventos, advertencias y errores.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MeteoManager.class);

    /**
     * Repositorio para operaciones de la entidad {@link Station}.
     */
    private final StationRepository stationRepository;

    /**
     * Repositorio para operaciones de la entidad {@link Observation}.
     */
    private final ObservationRepository observationRepository;

    /**
     * Constructor que inyecta los repositorios necesarios.
     *
     * @param stationRepository repositorio de estaciones.
     * @param observationRepository repositorio de observaciones.
     */
    @Autowired
    public MeteoManager(final StationRepository stationRepository,
            final ObservationRepository observationRepository) {
        this.stationRepository = stationRepository;
        this.observationRepository = observationRepository;
    }

    /**
     * Persiste una observación individual a partir de los datos de RedMeteo.
     * <p>
     * El método realiza las siguientes operaciones:
     * </p>
     * <ul>
     * <li><strong>Validación de entrada:</strong> Si el DTO es {@code null} o
     * el código de estación está vacío, se omite la operación y se retorna
     * {@code false}.</li>
     * <li><strong>Get-or-create Station:</strong> Obtiene la estación existente
     * o crea una nueva con los datos del DTO.</li>
     * <li><strong>Validación de código de observación:</strong> Normaliza el
     * código a mayúsculas. Si está vacío, se registra un error y se retorna
     * {@code false}.</li>
     * <li><strong>Detección de duplicados:</strong> Consulta si ya existe una
     * observación para la misma estación y código (case-insensitive). Si
     * existe, se registra en modo debug y se retorna {@code false}.</li>
     * <li><strong>Persistencia:</strong> Crea una nueva observación con todos
     * los valores del DTO, la guarda en la base de datos y retorna
     * {@code true}.</li>
     * </ul>
     *
     * @param rm DTO de RedMeteo con los datos de la observación (puede ser
     * {@code null}).
     * @return {@code true} si se creó una nueva observación (persistencia
     * exitosa), {@code false} si ya existía, el DTO era inválido o ocurrió
     * algún error de validación.
     * @see #getOrCreateStation(String, RedMeteo)
     * @see #crearObservacion(Station, String, RedMeteo)
     */
    public boolean saveObs(final RedMeteo rm) {
        if (rm == null) {
            return false;
        }

        final String stationCode = TextUtils.upper(rm.getIdEstacion());
        if (StringUtils.isBlank(stationCode)) {
            LOGGER.warn("Código de estación vacío en observación: {}", rm);
            return false;
        }

        final Station station = getOrCreateStation(stationCode, rm);

        final String code = TextUtils.upper(rm.getIdObservacion());
        if (StringUtils.isBlank(code)) {
            LOGGER.error("No hay código de observación. Estación: {}, Payload: {}",
                    stationCode, rm);
            return false;
        }

        final Observation obs = observationRepository.findByStationAndCodeIgnoreCase(station, code);
        if (obs != null) {
            LOGGER.debug("Observación ya existe: station={}, code={}", stationCode, code);
            return false;
        }

        final Observation nuevaObs = crearObservacion(station, code, rm);
        final Observation savedObs = observationRepository.save(nuevaObs);
        LOGGER.info("Observación creada: id={}, station={}, code={}, date={}",
                savedObs.getId(), station.getCode(), code, savedObs.getDate());
        return true;
    }

    /**
     * Obtiene una estación por su código o la crea si no existe.
     * <p>
     * Busca en el repositorio una estación con el código especificado
     * (case-insensitive). Si no existe, crea una nueva estación a partir de los
     * datos del DTO de RedMeteo y la persiste.
     * </p>
     * <p>
     * <strong>Campos de la nueva estación:</strong>
     * </p>
     * <ul>
     * <li>{@code active}: {@code true} (por defecto)</li>
     * <li>{@code altitude}: {@link RedMeteo#getAltitud()}</li>
     * <li>{@code code}: código normalizado</li>
     * <li>{@code latitude}: {@link RedMeteo#getLatitud()}</li>
     * <li>{@code longitude}: {@link RedMeteo#getLongitud()}</li>
     * <li>{@code name}: {@link RedMeteo#getNombre()}</li>
     * </ul>
     *
     * @param stationCode código único de la estación (normalizado a
     * mayúsculas).
     * @param rm datos de RedMeteo con la información de la estación.
     * @return la estación existente o recién creada (nunca {@code null}).
     */
    private Station getOrCreateStation(final String stationCode, final RedMeteo rm) {
        Station station = stationRepository.findByCodeIgnoreCase(stationCode);
        if (station == null) {
            LOGGER.info("Creando nueva estación con código={}", stationCode);
            station = new Station();
            station.setActive(true);
            station.setAltitude(rm.getAltitud());
            station.setCode(stationCode);
            station.setLatitude(rm.getLatitud());
            station.setLongitude(rm.getLongitud());
            station.setName(rm.getNombre());
            station = stationRepository.save(station);
        }
        return station;
    }

    /**
     * Crea un objeto {@link Observation} a partir de los datos de RedMeteo.
     * <p>
     * Todos los valores numéricos se procesan mediante
     * {@link RmUtils#getValue(Double)} para manejar valores nulos de forma
     * segura.
     * </p>
     *
     * @param station estación asociada a la observación.
     * @param code código de la observación (ya normalizado a mayúsculas).
     * @param rm datos de RedMeteo con los valores de la observación.
     * @return nueva instancia de {@link Observation} (no persistida aún).
     */
    private Observation crearObservacion(final Station station,
            final String code,
            final RedMeteo rm) {
        final Observation obs = new Observation();
        obs.setCode(code);
        obs.setAbsolutePressure(RmUtils.getValue(rm.getPresionAbsoluta()));
        obs.setDailyRainfall(RmUtils.getValue(rm.getLluviaDiaria()));
        obs.setDate(rm.getFechaHora());
        obs.setDewPoint(RmUtils.getValue(rm.getPuntoRocio()));
        obs.setHumidity(RmUtils.getValue(rm.getHumedad()));
        obs.setPrecipitation(RmUtils.getValue(rm.getPrecipitacion()));
        obs.setPressure(RmUtils.getValue(rm.getPresion()));
        obs.setRainRate(RmUtils.getValue(rm.getTasaLluvia()));
        obs.setSolarRadiation(RmUtils.getValue(rm.getRadiacionSolar()));
        obs.setStation(station);
        obs.setTemperature(RmUtils.getValue(rm.getTemperatura()));
        obs.setUltraviolet(RmUtils.getValue(rm.getUltravioleta()));
        obs.setWindDirection(RmUtils.getValue(rm.getDireccionViento()));
        obs.setWindGust(RmUtils.getValue(rm.getRachaViento()));
        obs.setWindSpeed(RmUtils.getValue(rm.getVelocidadViento()));
        return obs;
    }

    /**
     * Obtiene una estación meteorológica por su código.
     * <p>
     * La búsqueda es insensible a mayúsculas/minúsculas.
     * </p>
     *
     * @param code código de la estación (puede ser {@code null} o vacío).
     * @return la estación encontrada, o {@code null} si no existe o el código
     * es inválido.
     */
    public Station getStation(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        return stationRepository.findByCodeIgnoreCase(code);
    }

    /**
     * Obtiene todas las observaciones asociadas a una estación.
     * <p>
     * Si la estación es {@code null}, se registra una advertencia y se retorna
     * una lista vacía.
     * </p>
     *
     * @param station estación a consultar (puede ser {@code null}).
     * @return lista de observaciones; nunca {@code null}, puede estar vacía.
     */
    public List<Observation> getObservations(final Station station) {
        if (station == null) {
            LOGGER.warn("Estación es null, retornando lista vacía");
            return List.of();
        }

        return observationRepository.findByStation(station);
    }
}
