package cl.sebastian.cm.scheduler.domain.repository;

import cl.sebastian.cm.scheduler.domain.model.Pharmacy;
import cl.sebastian.cm.scheduler.domain.model.PharmacyOnDuty;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link PharmacyOnDuty}.
 * <p>
 * Esta interfaz extiende {@link JpaRepository}, proporcionando métodos CRUD
 * estándar y funcionalidad de paginación/ordenación para los registros de
 * farmacias en turno.
 * </p>
 * <p>
 * Además de los métodos heredados, define un método de consulta personalizado
 * para buscar un turno específico por farmacia y fecha (ignorando la hora).
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see PharmacyOnDuty
 * @see Pharmacy
 * @see JpaRepository
 */
@Repository
public interface PharmacyOnDutyRepository extends JpaRepository<PharmacyOnDuty, Long> {

    /**
     * Busca un registro de turno de farmacia por la farmacia y la fecha exacta.
     * <p>
     * Este método utiliza la convención de nomenclatura de Spring Data JPA para
     * generar automáticamente la consulta:
     * <pre>{@code select p from PharmacyOnDuty p where p.pharmacy = ?1 and p.dutyDate = ?2}</pre>
     * </p>
     *
     * <p>
     * Se asume que existe una restricción de unicidad a nivel de base de datos
     * (por ejemplo, una clave única compuesta por {@code pharmacy_fk} y la
     * fecha) para garantizar que el resultado sea como máximo un único
     * registro. Si no existe dicha restricción y hay múltiples registros para
     * la misma farmacia y fecha, este método lanzará una
     * {@link org.springframework.dao.IncorrectResultSizeDataAccessException}.
     * </p>
     *
     * @param pharmacy la farmacia asociada al turno (no puede ser
     * {@code null}).
     * @param dutyDate la fecha del turno (no puede ser {@code null}).
     * @return el registro de turno que coincide con la farmacia y la fecha, o
     * {@code null} si no existe ningún registro.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException si
     * existen más de un registro para la misma farmacia y fecha.
     */
    PharmacyOnDuty findByPharmacyAndDutyDate(Pharmacy pharmacy, LocalDate dutyDate);
}
