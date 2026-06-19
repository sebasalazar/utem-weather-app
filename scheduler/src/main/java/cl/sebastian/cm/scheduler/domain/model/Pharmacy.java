package cl.sebastian.cm.scheduler.domain.model;

import cl.sebastian.cm.scheduler.domain.enums.Commerce;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa una farmacia en el sistema, con sus datos comerciales, ubicación,
 * horario de atención y coordenadas geográficas.
 * <p>
 * Esta entidad JPA mapea a la tabla {@code pharmacies} y extiende de
 * {@link PkEntityBase}, que proporciona el identificador único (id) de la
 * entidad.
 * </p>
 * <p>
 * El campo {@code storeId} es único y se utiliza como identificador alternativo
 * para la tienda en el sistema de comercio asociado.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see PkEntityBase
 * @see Commerce
 */
@Entity
@Table(name = "pharmacies")
public class Pharmacy extends PkEntityBase {

    /**
     * Tipo de comercio al que pertenece la farmacia (por ejemplo, cadena o
     * franquicia). Se almacena como un valor numérico (smallint) en la base de
     * datos.
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "commerce", columnDefinition = "smallint", nullable = false)
    private Commerce commerce;

    /**
     * Identificador único de la tienda dentro del sistema de comercio. Es un
     * valor numérico y no puede repetirse en la tabla.
     */
    @PositiveOrZero
    @Column(name = "store_id", nullable = false, unique = true)
    private Integer storeId;

    /**
     * Nombre comercial de la farmacia.
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Dirección física de la farmacia.
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * Número de teléfono de contacto de la farmacia.
     */
    @PositiveOrZero
    @Column(name = "phone", nullable = false)
    private Long phone;

    /**
     * Hora de inicio de la jornada laboral (apertura).
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * Hora de fin de la jornada laboral (cierre).
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Latitud geográfica de la ubicación de la farmacia.
     */
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Column(name = "latitude", nullable = false)
    private double latitude;

    /**
     * Longitud geográfica de la ubicación de la farmacia.
     */
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Column(name = "longitude", nullable = false)
    private double longitude;

    /**
     * Obtiene el tipo de comercio al que pertenece la farmacia.
     *
     * @return el {@link Commerce} asociado.
     */
    public Commerce getCommerce() {
        return commerce;
    }

    /**
     * Establece el tipo de comercio al que pertenece la farmacia.
     *
     * @param commerce el nuevo tipo de comercio.
     */
    public void setCommerce(Commerce commerce) {
        this.commerce = commerce;
    }

    /**
     * Obtiene el identificador único de la tienda en el sistema de comercio.
     *
     * @return el storeId.
     */
    public Integer getStoreId() {
        return storeId;
    }

    /**
     * Establece el identificador único de la tienda.
     *
     * @param storeId el nuevo storeId.
     */
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    /**
     * Obtiene el nombre comercial de la farmacia.
     *
     * @return el nombre.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre comercial de la farmacia.
     *
     * @param name el nuevo nombre.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la dirección física de la farmacia.
     *
     * @return la dirección.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Establece la dirección física de la farmacia.
     *
     * @param address la nueva dirección.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Obtiene el número de teléfono de la farmacia.
     *
     * @return el teléfono.
     */
    public Long getPhone() {
        return phone;
    }

    /**
     * Establece el número de teléfono de la farmacia.
     *
     * @param phone el nuevo teléfono.
     */
    public void setPhone(Long phone) {
        this.phone = phone;
    }

    /**
     * Obtiene la hora de apertura de la farmacia.
     *
     * @return la hora de inicio.
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Establece la hora de apertura de la farmacia.
     *
     * @param startTime la nueva hora de inicio.
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Obtiene la hora de cierre de la farmacia.
     *
     * @return la hora de fin.
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Establece la hora de cierre de la farmacia.
     *
     * @param endTime la nueva hora de fin.
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Obtiene la latitud de la ubicación de la farmacia.
     *
     * @return la latitud.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Establece la latitud de la ubicación de la farmacia.
     *
     * @param latitude la nueva latitud.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Obtiene la longitud de la ubicación de la farmacia.
     *
     * @return la longitud.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Establece la longitud de la ubicación de la farmacia.
     *
     * @param longitude la nueva longitud.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Calcula el código hash basado exclusivamente en el {@code storeId}.
     *
     * @return el valor hash.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.storeId);
        return hash;
    }

    /**
     * Compara esta farmacia con otro objeto para determinar igualdad. Dos
     * farmacias se consideran iguales si tienen el mismo {@code storeId}.
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
        final Pharmacy other = (Pharmacy) obj;
        return Objects.equals(this.storeId, other.storeId);
    }
}
