package cl.sebastian.cm.scheduler.domain.model;

import cl.sebastian.cm.scheduler.domain.Seba;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Clase base para entidades JPA con clave primaria numerica autoincremental.
 *
 * <p>
 * Centraliza los campos de auditoria mas comunes del proyecto: identificador,
 * fecha de creacion y fecha de actualizacion. Las entidades que extienden esta
 * clase comparten el mismo criterio de igualdad y el mismo ciclo de
 * timestamps.</p>
 */
@MappedSuperclass
public class PkEntityBase extends Seba {

    /**
     * El tiempo de ejecución de serialización asocia con cada clase
     * serializable un número de versión, llamado serialVersionUID, que se usa
     * durante la deserialización para verificar que el remitente y el receptor
     * de un objeto serializado hayan cargado clases para ese objeto que sean
     * compatibles con respecto a la serialización. Si el receptor ha cargado
     * una clase para el objeto que tiene un serialVersionUID diferente al de la
     * clase del remitente correspondiente, entonces la deserialización
     * resultará en una InvalidClassException.
     *
     * Una clase serializable declara su propio serialVersionUID como un campo
     * serialVersionUID que DEBE ser static, final, y de tipo long. Sino se
     * declara la JVM lo calcula en tiempo de ejecución.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Llave primaria Autoincremental
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk", nullable = false, updatable = false)
    private Long id = null;

    @Column(name = "created", nullable = false, updatable = false)
    private OffsetDateTime created = OffsetDateTime.now();

    @Column(name = "updated", nullable = false)
    private OffsetDateTime updated = OffsetDateTime.now();

    /**
     * Callback JPA ejecutado antes de insertar una entidad por primera vez.
     *
     * <p>
     * Sincroniza {@code created} y {@code updated} con la fecha actual.</p>
     */
    @PrePersist
    protected void onCreate() {
        this.created = OffsetDateTime.now();
        this.updated = this.created;
    }

    /**
     * Callback JPA ejecutado antes de actualizar una entidad existente.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updated = OffsetDateTime.now();
    }

    /**
     * @return El identificador generado por el motor de base de datos.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Fuerza un identificador, no necesariamente se podrá persistir
     * dependerá del mecanismo interno.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Fecha de creación
     */
    public OffsetDateTime getCreated() {
        return created;
    }

    /**
     * @param created Fecha de creación
     */
    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    /**
     * @return Fecha de última actualización
     */
    public OffsetDateTime getUpdated() {
        return updated;
    }

    /**
     * @param updated Fecha de última actualización
     */
    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }

    /**
     * @return El hash de la clase, es práctico para reducir el computo
     * necesario para esta ejecución.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);  // `id` puede ser null, pero Objects.hashCode maneja bien ese caso
    }

    /**
     * Esta clase tiene un supuesto muy fuerte, y se trata de que dos objetos se
     * consideraran iguales si sus ids son iguales, esto no necesariamente es
     * cierto pero ayuda a aumentar el rendimiento de las comparaciones.
     *
     * @param obj Objeto
     * @return true si son iguales o false en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PkEntityBase other = (PkEntityBase) obj;

        // Solo se consideran iguales si ambos IDs existen y coinciden
        if (this.id == null || other.id == null) {
            return false;
        }

        return Objects.equals(this.id, other.id);
    }
}
