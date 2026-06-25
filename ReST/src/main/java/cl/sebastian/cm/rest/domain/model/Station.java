package cl.sebastian.cm.rest.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Entidad de estación meteorológica.
 * <p>
 * Modela una estación con código único, nombre, coordenadas geográficas,
 * altitud y estado (activa/inactiva). Hereda la PK y trazabilidad básica desde
 * {@link PkEntityBase}.
 * </p>
 *
 * <h2>Igualdad y hash</h2>
 * <p>
 * Esta clase redefine {@link #equals(Object)} y {@link #hashCode()} para usar
 * <b>el código</b> ({@link #code}) como clave natural. Esto implica:
 * </p>
 * <ul>
 * <li>Si {@code code} es nulo, la igualdad puede no comportarse como se
 * espera.</li>
 * <li>{@code code} se define como {@code unique} y {@code updatable=false}, por
 * lo que es inmutable a nivel de base de datos una vez persistido.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
@Entity
@Table(name = "stations")
public class Station extends PkEntityBase {

    /**
     * Código único de la estación.
     * <ul>
     * <li>No puede ser blanco ni superar 255 caracteres.</li>
     * <li>Índice único en la tabla y no actualizable tras persistencia.</li>
     * </ul>
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "code", nullable = false, unique = true, length = 255, updatable = false)
    private String code;

    /**
     * Nombre legible de la estación.
     * <ul>
     * <li>No puede ser blanco ni superar 255 caracteres.</li>
     * </ul>
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Latitud geográfica en grados decimales.
     * <ul>
     * <li>Rango permitido: [-90.0, 90.0].</li>
     * <li>No nula.</li>
     * </ul>
     */
    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    /**
     * Longitud geográfica en grados decimales.
     * <ul>
     * <li>Rango permitido: [-180.0, 180.0].</li>
     * <li>No nula.</li>
     * </ul>
     */
    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    /**
     * Altitud en metros sobre el nivel del mar en metros.
     * <ul>
     * <li>Rango sugerido: [-500, 9000].</li>
     * <li>No nula.</li>
     * </ul>
     */
    @NotNull
    @Min(-500)
    @Max(9000)
    @Column(name = "altitude", nullable = false)
    private Integer altitude;

    /**
     * Indicador de actividad de la estación.
     * <ul>
     * <li>{@code true} si está operativa, {@code false} en caso contrario.</li>
     * <li>No nulo a nivel de base de datos.</li>
     * </ul>
     */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * Obtiene el código único de la estación.
     *
     * @return código único (no blanco cuando es válido)
     */
    public String getCode() {
        return code;
    }

    /**
     * Define el código único de la estación.
     * <p>
     * Advertencia: la columna está marcada como {@code updatable=false};
     * intenta establecerlo antes de persistir. Tras persistir, el proveedor JPA
     * puede ignorar cambios.
     * </p>
     *
     * @param code código único (no blanco, máx. 255)
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Obtiene el nombre legible de la estación.
     *
     * @return nombre (no blanco cuando es válido)
     */
    public String getName() {
        return name;
    }

    /**
     * Define el nombre legible de la estación.
     *
     * @param name nombre (no blanco, máx. 255)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la latitud geográfica en grados decimales.
     *
     * @return latitud en el rango [-90.0, 90.0]
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Define la latitud geográfica en grados decimales.
     *
     * @param latitude latitud en el rango [-90.0, 90.0]
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Obtiene la longitud geográfica en grados decimales.
     *
     * @return longitud en el rango [-180.0, 180.0]
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Define la longitud geográfica en grados decimales.
     *
     * @param longitude longitud en el rango [-180.0, 180.0]
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Obtiene la altitud en metros sobre el nivel del mar.
     *
     * @return altitud en el rango [-500, 9000]
     */
    public Integer getAltitude() {
        return altitude;
    }

    /**
     * Define la altitud en metros sobre el nivel del mar.
     *
     * @param altitude altitud en el rango [-500, 9000]
     */
    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    /**
     * Indica si la estación está activa.
     *
     * @return {@code true} si operativa; {@code false} en caso contrario
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Define el estado de actividad de la estación.
     *
     * @param active {@code true} si operativa; {@code false} en caso contrario
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Calcula el hash basado en el código ({@link #code}).
     * <p>
     * Coherente con {@link #equals(Object)}. Si el código es nulo, el valor del
     * hash puede degradar la dispersión en estructuras hash.
     * </p>
     *
     * @return valor hash consistente con {@code equals}
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.code);
        return hash;
    }

    /**
     * Determina la igualdad por clave natural ({@link #code}).
     * <p>
     * Dos estaciones se consideran iguales si y solo si sus códigos son
     * iguales.
     * </p>
     *
     * @param obj objeto a comparar
     * @return {@code true} si ambos objetos son {@code Station} y tienen el
     * mismo código; {@code false} en caso contrario
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
        final Station other = (Station) obj;
        return Objects.equals(this.code, other.code);
    }
}
