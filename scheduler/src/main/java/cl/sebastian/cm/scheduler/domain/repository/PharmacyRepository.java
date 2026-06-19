package cl.sebastian.cm.scheduler.domain.repository;

import cl.sebastian.cm.scheduler.domain.model.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
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
 * @author Sebastián Salazar
 * @version 1.0
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
}
