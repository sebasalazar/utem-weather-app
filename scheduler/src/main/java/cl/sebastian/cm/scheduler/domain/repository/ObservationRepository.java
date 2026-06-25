package cl.sebastian.cm.scheduler.domain.repository;

import cl.sebastian.cm.scheduler.domain.model.Observation;
import cl.sebastian.cm.scheduler.domain.model.Station;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Observation}.
 * <p>
 * Proporciona operaciones CRUD estándar (heredadas de {@link JpaRepository}) y
 * consultas derivadas por convención de nombres para acceder a observaciones
 * meteorológicas.
 * </p>
 * <p>
 * <strong>Clave primaria:</strong> {@code Long} (heredada de
 * {@code PkEntityBase}).
 * </p>
 * <p>
 * <strong>Recomendaciones de rendimiento:</strong>
 * Para búsquedas frecuentes, se recomienda indexar las columnas:
 * </p>
 * <ul>
 * <li>{@code station_fk} (clave foránea a la estación)</li>
 * <li>{@code code} (código de la observación)</li>
 * <li>{@code date_time} (fecha/hora de la observación) - opcional</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see Observation
 * @see Station
 * @see JpaRepository
 */
@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {

    /**
     * Busca una observación por su estación asociada y su código, ignorando
     * mayúsculas/minúsculas en el código.
     * <p>
     * Esta consulta se genera automáticamente a partir del nombre del método
     * ({@code findByStationAndCodeIgnoreCase}). La comparación del código se
     * realiza de forma insensible a mayúsculas/minúsculas, lo que permite
     * flexibilidad en el formato del código de observación.
     * </p>
     * <p>
     * <strong>Nota:</strong> Se asume que la combinación de estación y código
     * es única. Si existen múltiples observaciones con la misma estación y
     * código (lo cual no debería ocurrir por diseño), el método retornará la
     * primera encontrada según el orden de la base de datos, pero se recomienda
     * garantizar la unicidad a nivel de base de datos mediante una restricción
     * única compuesta.
     * </p>
     *
     * @param station la estación asociada a la observación (no puede ser
     * {@code null}).
     * @param code el código de la variable/observación (no puede ser
     * {@code null} ni vacío).
     * @return la observación que coincide con la estación y el código dados, o
     * {@code null} si no existe ninguna.
     * @throws IllegalArgumentException si {@code station} o {@code code} son
     * {@code null}.
     */
    Observation findByStationAndCodeIgnoreCase(Station station, String code);

    /**
     * Obtiene todas las observaciones asociadas a una estación determinada.
     * <p>
     * La consulta devuelve una lista de observaciones pertenecientes a la
     * estación especificada. Si la estación no tiene observaciones, retorna una
     * lista vacía (nunca {@code null}).
     * </p>
     * <p>
     * El orden de los elementos no está garantizado a menos que se defina
     * explícitamente en la consulta (por ejemplo, mediante {@code @OrderBy}).
     * </p>
     *
     * @param station la estación cuyas observaciones se desean obtener (no
     * puede ser {@code null}).
     * @return lista de observaciones de la estación; nunca {@code null}, puede
     * estar vacía si no hay observaciones.
     * @throws IllegalArgumentException si {@code station} es {@code null}.
     */
    List<Observation> findByStation(Station station);
}
