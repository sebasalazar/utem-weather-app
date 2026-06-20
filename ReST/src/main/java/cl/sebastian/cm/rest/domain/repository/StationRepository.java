package cl.sebastian.cm.rest.domain.repository;

import cl.sebastian.cm.rest.domain.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para {@link Station}.
 * <p>
 * Expone operaciones CRUD estándar y consultas derivadas por nombre de método.
 * </p>
 *
 * <h2>Notas</h2>
 * <ul>
 * <li>La PK del agregado es {@code Long} (heredada desde
 * {@code PkEntityBase}).</li>
 * <li>Existe una restricción única sobre {@code code} en la entidad/tabla.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    /**
     * Busca una estación por su código, ignorando mayúsculas/minúsculas.
     * <p>
     * Este método usa la convención de consulta derivada de Spring Data
     * ({@code findBy...IgnoreCase}). Si no existe coincidencia, Spring Data
     * devolverá {@code null}.
     * </p>
     *
     * @param code código único de la estación (no nulo/ni en blanco cuando es
     * válido)
     * @return la estación encontrada o {@code null} si no existe
     */
    Station findByCodeIgnoreCase(String code);
}
