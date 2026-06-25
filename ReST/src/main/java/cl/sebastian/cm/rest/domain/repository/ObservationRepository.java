package cl.sebastian.cm.rest.domain.repository;

import cl.sebastian.cm.rest.domain.data.IdDistance;
import cl.sebastian.cm.rest.domain.model.Observation;
import cl.sebastian.cm.rest.domain.model.Station;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Busca observaciones meteorológicas dentro de un radio de distancia desde
     * un punto geográfico dado, devolviendo el ID de la observación y la
     * distancia calculada en metros.
     * <p>
     * La consulta utiliza la función {@code ST_Distance} de PostGIS para
     * calcular la distancia en línea recta (geodésica) entre la ubicación de
     * cada observación y el punto de referencia. Los resultados se ordenan de
     * menor a mayor distancia.
     * </p>
     * <p>
     * <strong>Dependencias técnicas:</strong>
     * </p>
     * <ul>
     * <li>La base de datos debe tener habilitado el módulo PostGIS.</li>
     * <li>Los campos {@code latitude} y {@code longitude} de la entidad
     * {@code Observation} deben estar indexados para optimizar el
     * rendimiento.</li>
     * <li>La función {@code ST_MakePoint} crea un punto a partir de
     * coordenadas, y el cast {@code ::geography} permite calcular distancias en
     * metros sobre el esferoide terrestre.</li>
     * </ul>
     * <p>
     * <strong>Rendimiento:</strong>
     * Para conjuntos de datos grandes, se recomienda crear un índice espacial
     * GIST sobre la columna calculada o sobre la geometría:
     * {@code CREATE INDEX idx_obs_geography ON observations USING GIST (ST_MakePoint(longitude, latitude)::geography);}
     * </p>
     *
     * @param latitude latitud del punto de referencia en grados decimales (ej.
     * -33.4567).
     * @param longitude longitud del punto de referencia en grados decimales
     * (ej. -70.6543).
     * @param distance radio de búsqueda en metros (ej. 5000 para 5 km).
     * @return lista de objetos {@link IdDistance} que contienen el ID de la
     * observación y la distancia en metros, ordenada por distancia ascendente.
     * Puede estar vacía si no hay observaciones dentro del radio.
     * @see IdDistance
     * @see <a href="https://postgis.net/docs/ST_Distance.html">ST_Distance</a>
     */
    @Query(nativeQuery = true, value = """
    SELECT DISTINCT ON (s.pk) 
           o.pk::bigint AS id,
           ST_Distance(
               ST_MakePoint(s.longitude, s.latitude)::geography,
               ST_MakePoint(:longitude, :latitude)::geography
           )::int AS distance
    FROM observations o
    INNER JOIN stations s ON o.station_fk = s.pk
    WHERE ST_DWithin(
        ST_MakePoint(s.longitude, s.latitude)::geography,
        ST_MakePoint(:longitude, :latitude)::geography,
        :distance
    )
    ORDER BY s.pk, o.date_time DESC
    """)
    List<IdDistance> searchByDistance(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") long distance);
}
