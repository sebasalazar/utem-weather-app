package cl.sebastian.cm.rest.domain.repository;

import cl.sebastian.cm.rest.domain.data.IdDistance;
import cl.sebastian.cm.rest.domain.model.Pharmacy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Pharmacy}.
 * <p>
 * Esta interfaz extiende {@link JpaRepository}, lo que proporciona métodos CRUD
 * estándar y funcionalidad de paginación/ordenación para la entidad
 * {@code Pharmacy}.
 * </p>
 * <p>
 * Además de los métodos heredados, se define un método de consulta
 * personalizado para buscar una farmacia por su {@code storeId} (identificador
 * alternativo).
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see Pharmacy
 * @see JpaRepository
 */
@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    /**
     * Busca una farmacia por su identificador de tienda en el sistema de
     * comercio.
     * <p>
     * Este método utiliza la convención de nomenclatura de Spring Data JPA para
     * generar automáticamente la consulta:
     * {@code select p from Pharmacy p where p.storeId = ?1}. Dado que el campo
     * {@code storeId} es único en la entidad, se espera que el resultado sea
     * como máximo una única farmacia.
     * </p>
     *
     * @param storeId el identificador único de la tienda en el sistema de
     * comercio.
     * @return la farmacia que coincide con el {@code storeId}, o {@code null}
     * si no existe.
     */
    Pharmacy findByStoreId(Integer storeId);

    /**
     * Busca farmacias dentro de un radio de distancia desde un punto geográfico
     * dado, devolviendo el ID de la farmacia y la distancia calculada en
     * metros.
     * <p>
     * La consulta utiliza la función {@code ST_Distance} de PostGIS para
     * calcular la distancia geodésica en línea recta entre la ubicación de cada
     * farmacia y el punto de referencia. Los resultados se ordenan de menor a
     * mayor distancia.
     * </p>
     * <p>
     * <strong>Dependencias técnicas:</strong>
     * </p>
     * <ul>
     * <li>La base de datos debe tener habilitado el módulo PostGIS.</li>
     * <li>Los campos {@code latitude} y {@code longitude} de la entidad
     * {@code Pharmacy} deben estar indexados para optimizar el
     * rendimiento.</li>
     * <li>La función {@code ST_MakePoint} crea un punto a partir de
     * coordenadas, y el cast {@code ::geography} permite calcular distancias en
     * metros sobre el esferoide terrestre.</li>
     * </ul>
     * <p>
     * <strong>Rendimiento:</strong>
     * Para conjuntos de datos grandes, se recomienda crear un índice espacial
     * GIST sobre la columna calculada o sobre la geometría:
     * <pre>{@code CREATE INDEX idx_pharmacy_geography ON pharmacies USING GIST (ST_MakePoint(longitude, latitude)::geography);}</pre>
     * </p>
     * <p>
     * <strong>Ejemplo de uso:</strong>
     * Buscar farmacias en un radio de 5 km desde las coordenadas (-33.456,
     * -70.654):
     * <pre>{@code
     * List<IdDistance> farmaciasCercanas = pharmacyRepository.searchByDistance(-33.456, -70.654, 5000);
     * }</pre>
     * </p>
     *
     * @param latitude latitud del punto de referencia en grados decimales (ej.
     * -33.4567).
     * @param longitude longitud del punto de referencia en grados decimales
     * (ej. -70.6543).
     * @param distance radio de búsqueda en metros (ej. 5000 para 5 km).
     * @return lista de objetos {@link IdDistance} que contienen el ID de la
     * farmacia y la distancia en metros, ordenada por distancia ascendente.
     * Puede estar vacía si no hay farmacias dentro del radio.
     * @see IdDistance
     * @see <a href="https://postgis.net/docs/ST_Distance.html">ST_Distance</a>
     */
    @Query(nativeQuery = true, value = """
        SELECT id, distance
        FROM (
            SELECT pk::bigint AS id,
                   ST_Distance(
                       ST_MakePoint(longitude, latitude)::geography,
                       ST_MakePoint(:longitude, :latitude)::geography
                   )::int AS distance
            FROM pharmacies
        ) AS sub
        WHERE distance <= :distance
        ORDER BY distance ASC
        """)
    List<IdDistance> searchByDistance(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") long distance
    );
}
