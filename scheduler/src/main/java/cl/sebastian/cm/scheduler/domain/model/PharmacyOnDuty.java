package cl.sebastian.cm.scheduler.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa el registro de una farmacia que se encuentra de turno en una fecha
 * específica.
 * <p>
 * Esta entidad JPA mapea a la tabla {@code pharmacies_on_duty} y extiende de
 * {@link PkEntityBase}, que proporciona el identificador único (id) de la
 * entidad.
 * </p>
 * <p>
 * Una instancia de esta clase asocia una farmacia ({@link Pharmacy}) con una
 * fecha y hora determinadas, indicando que dicha farmacia está activa o de
 * guardia en ese momento.
 * </p>
 * <p>
 * La unicidad de la relación suele estar garantizada a nivel de base de datos
 * mediante restricciones (por ejemplo, una clave única compuesta por
 * {@code pharmacy_fk} y {@code duty_date}), aunque en esta clase no se reflejan
 * explícitamente.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see Pharmacy
 * @see PkEntityBase
 */
@Entity
@Table(name = "pharmacies_on_duty")
public class PharmacyOnDuty extends PkEntityBase {

    /**
     * Farmacia asociada a este registro de turno.
     * <p>
     * La relación es ManyToOne, por lo que una misma farmacia puede aparecer en
     * múltiples registros de turno en diferentes fechas.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "pharmacy_fk", nullable = false)
    private Pharmacy pharmacy;

    /**
     * Fecha y hora en que la farmacia está de turno.
     * <p>
     * Este campo almacena el momento exacto (incluyendo fecha y hora) para el
     * cual se asigna el turno a la farmacia.
     * </p>
     */
    @Column(name = "duty_date", nullable = false)
    private LocalDate dutyDate;

    /**
     * Obtiene la farmacia asociada a este registro de turno.
     *
     * @return la farmacia.
     */
    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    /**
     * Establece la farmacia asociada a este registro de turno.
     *
     * @param pharmacy la nueva farmacia.
     */
    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    /**
     * Obtiene la fecha y hora del turno.
     *
     * @return la fecha y hora.
     */
    public LocalDate getDutyDate() {
        return dutyDate;
    }

    /**
     * Establece la fecha y hora del turno.
     *
     * @param dutyDate la nueva fecha y hora.
     */
    public void setDutyDate(LocalDate dutyDate) {
        this.dutyDate = dutyDate;
    }

    /**
     * Calcula el código hash basado en la farmacia y la fecha del turno.
     *
     * @return el valor hash.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.pharmacy);
        hash = 79 * hash + Objects.hashCode(this.dutyDate);
        return hash;
    }

    /**
     * Compara este registro de turno con otro objeto para determinar igualdad.
     * <p>
     * Dos registros se consideran iguales si tienen la misma farmacia y la
     * misma fecha de turno.
     * </p>
     *
     * @param obj el objeto a comparar.
     * @return {@code true} si son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PharmacyOnDuty other = (PharmacyOnDuty) obj;
        if (!Objects.equals(this.pharmacy, other.pharmacy)) {
            return false;
        }
        return Objects.equals(this.dutyDate, other.dutyDate);
    }
}
