package cl.sebastian.cm.rest.manager;

import cl.sebastian.cm.rest.domain.data.IdDistance;
import cl.sebastian.cm.rest.domain.model.Observation;
import cl.sebastian.cm.rest.domain.repository.ObservationRepository;
import cl.sebastian.cm.rest.utils.CoordinatesUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Servicio de dominio para consultas meteorológicas con criterio de proximidad
 * geográfica.
 * <p>
 * Encapsula la lógica de búsqueda de observaciones dentro de un radio
 * determinado alrededor de un punto geográfico, delegando el filtrado espacial
 * a PostGIS mediante {@code ST_DWithin}. La observación devuelta es la más
 * reciente (por fecha/hora) dentro del radio configurado.
 * </p>
 * <p>
 * <strong>Reglas de negocio:</strong>
 * </p>
 * <ul>
 * <li>El radio máximo de búsqueda se configura externamente mediante la
 * propiedad {@code max.distance} (en metros). Se garantiza un valor mínimo de 1
 * metro para evitar consultas degeneradas.</li>
 * <li>Las coordenadas deben estar en el rango válido (latitud
 * {@code [-90, 90]}, longitud {@code [-180, 180]}).</li>
 * <li>Se devuelve la observación más reciente dentro del radio (ordenada por
 * fecha descendente). Si no existe ninguna, se retorna
 * {@link Optional#empty()}.</li>
 * <li>Si las coordenadas son inválidas, se registra una advertencia y se
 * retorna {@code Optional.empty()}.</li>
 * </ul>
 *
 * <h2>Dependencias técnicas</h2>
 * <ul>
 * <li>La base de datos debe tener habilitado el módulo PostGIS.</li>
 * <li>El repositorio {@link ObservationRepository} debe tener implementado el
 * método {@code findLatestNearby} que ejecuta la consulta espacial.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see ObservationRepository
 * @see Observation
 */
@Service
public class MeteoManager {

    /**
     * Logger de la clase.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MeteoManager.class);

    /**
     * Repositorio de observaciones meteorológicas.
     */
    private final ObservationRepository observationRepository;

    /**
     * Distancia máxima de búsqueda en metros. Se garantiza que sea al menos 1.
     */
    private final int maxDistance;

    /**
     * Construye el manager con el repositorio y la distancia máxima
     * configurada.
     * <p>
     * La distancia máxima se lee de la propiedad {@code max.distance} del
     * archivo de configuración. Si no está definida o es menor que 1, se usa 1
     * metro como valor por defecto.
     * </p>
     *
     * @param observationRepository repositorio de observaciones (no puede ser
     * {@code null}).
     * @param maxDistance distancia máxima de búsqueda en metros; si es
     * {@code null} o menor que 1, se usa 1.
     */
    public MeteoManager(final ObservationRepository observationRepository,
            @Value("${max.distance:1}") final Integer maxDistance) {
        this.observationRepository = observationRepository;
        this.maxDistance = Math.max(1, maxDistance == null ? 1 : maxDistance);
        LOGGER.info("MeteoManager inicializado con distancia máxima: {} metros", this.maxDistance);
    }

    /**
     * Busca la observación más reciente dentro del radio configurado alrededor
     * del punto geográfico indicado.
     * <p>
     * La búsqueda se realiza mediante una consulta espacial que calcula la
     * distancia geodésica en metros usando {@code ST_Distance}. Solo se
     * consideran observaciones cuya distancia sea menor o igual al radio máximo
     * configurado ({@link #maxDistance}). Entre todas las observaciones
     * encontradas, se selecciona la más reciente (según su campo de
     * fecha/hora).
     * </p>
     * <p>
     * <strong>Comportamiento ante coordenadas inválidas:</strong>
     * Si la latitud o longitud están fuera de los rangos permitidos, se
     * registra una advertencia y se retorna {@link Optional#empty()}.
     * </p>
     *
     * @param latitude latitud del punto de referencia, en grados decimales
     * ({@code [-90, 90]}).
     * @param longitude longitud del punto de referencia, en grados decimales
     * ({@code [-180, 180]}).
     * @return la observación más reciente dentro del radio, o
     * {@link Optional#empty()} si no existe ninguna o las coordenadas son
     * inválidas.     * 
     */
    public Optional<Observation> getLastNearby(final double latitude, final double longitude) {
        if (!CoordinatesUtils.areValid(latitude, longitude)) {
            LOGGER.warn("Coordenadas inválidas recibidas: lat={}, lon={}", latitude, longitude);
            return Optional.empty();
        }

        List<IdDistance> list = observationRepository.searchByDistance(latitude, longitude, maxDistance);
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }

        return observationRepository.findById(list.getFirst().getId());
    }

}
