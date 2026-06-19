package cl.sebastian.cm.scheduler.domain.data;

import cl.sebastian.cm.scheduler.domain.Seba;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema; // Import añadido para Swagger
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa un turno de farmacia con información de ubicación y horario de
 * funcionamiento. Esta clase se utiliza para deserializar datos de APIs
 * externas que proporcionan información sobre farmacias de turno en Chile.
 * <p>
 * Los datos se reciben en formato JSON y se mapean mediante anotaciones
 * {@link JsonProperty}. Todos los campos son opcionales y se ignoran
 * propiedades desconocidas.
 * </p>
 *
 * @author Sebastián C.
 * @version 1.0
 * @see JsonIgnoreProperties
 * @see JsonProperty
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Información de una farmacia de turno, incluyendo ubicación y horarios")
public class FarmaciaTurno extends Seba {

    @Schema(description = "Fecha del turno (formato ISO, ej: 2026-06-18)", example = "2026-06-18")
    @JsonProperty("fecha")
    private LocalDate date;

    @Schema(description = "Identificador único del local de farmacia", example = "12345")
    @JsonProperty("local_id")
    private Long storeId;

    @Schema(description = "Clave foránea de la región (según codificación externa)", example = "8")
    @JsonProperty("fk_region")
    private Long regionId;

    @Schema(description = "Clave foránea de la comuna", example = "101")
    @JsonProperty("fk_comuna")
    private Long communeId;

    @Schema(description = "Clave foránea de la localidad", example = "5")
    @JsonProperty("fk_localidad")
    private Long localityId;

    @Schema(description = "Nombre del local de la farmacia", example = "Farmacia Cruz Verde")
    @JsonProperty("local_nombre")
    private String pharmacyName;

    @Schema(description = "Nombre de la comuna", example = "Santiago")
    @JsonProperty("comuna_nombre")
    private String communeName;

    @Schema(description = "Nombre de la localidad", example = "Providencia")
    @JsonProperty("localidad_nombre")
    private String localityName;

    @Schema(description = "Dirección completa del local", example = "Av. Providencia 1234")
    @JsonProperty("local_direccion")
    private String pharmacyAddress;

    @Schema(description = "Hora de apertura (formato HH:mm:ss)", example = "08:30:00")
    @JsonProperty("funcionamiento_hora_apertura")
    private LocalTime startTime;

    @Schema(description = "Hora de cierre (formato HH:mm:ss)", example = "22:00:00")
    @JsonProperty("funcionamiento_hora_cierre")
    private LocalTime endTime;

    @Schema(description = "Número de teléfono del local (sin formato)", example = "227777777")
    @JsonProperty("local_telefono")
    private String pharmacyPhone;

    @Schema(description = "Latitud de la ubicación (coordenada WGS84)", example = "-33.456789")
    @JsonProperty("local_lat")
    private Double latitude;

    @Schema(description = "Longitud de la ubicación (coordenada WGS84)", example = "-70.654321")
    @JsonProperty("local_lng")
    private Double longitude;

    @Schema(description = "Día de la semana en que opera (ej: 'Lunes', 'Martes')", example = "Jueves")
    @JsonProperty("funcionamiento_dia")
    private String operationDay;

    // -------------------- Getters y Setters --------------------
    /**
     * Obtiene la fecha del turno.
     *
     * @return fecha del turno como {@link LocalDate}
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Establece la fecha del turno.
     *
     * @param date fecha del turno
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Obtiene el identificador único del local.
     *
     * @return ID de la farmacia
     */
    public Long getStoreId() {
        return storeId;
    }

    /**
     * Establece el identificador único del local.
     *
     * @param storeId ID de la farmacia
     */
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    /**
     * Obtiene la clave foránea de la región.
     *
     * @return código de región
     */
    public Long getRegionId() {
        return regionId;
    }

    /**
     * Establece la clave foránea de la región.
     *
     * @param regionId código de región
     */
    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    /**
     * Obtiene la clave foránea de la comuna.
     *
     * @return código de comuna
     */
    public Long getCommuneId() {
        return communeId;
    }

    /**
     * Establece la clave foránea de la comuna.
     *
     * @param communeId código de comuna
     */
    public void setCommuneId(Long communeId) {
        this.communeId = communeId;
    }

    /**
     * Obtiene la clave foránea de la localidad.
     *
     * @return código de localidad
     */
    public Long getLocalityId() {
        return localityId;
    }

    /**
     * Establece la clave foránea de la localidad.
     *
     * @param localityId código de localidad
     */
    public void setLocalityId(Long localityId) {
        this.localityId = localityId;
    }

    /**
     * Obtiene el nombre del local de farmacia.
     *
     * @return nombre de la farmacia
     */
    public String getPharmacyName() {
        return pharmacyName;
    }

    /**
     * Establece el nombre del local de farmacia.
     *
     * @param pharmacyName nombre de la farmacia
     */
    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    /**
     * Obtiene el nombre de la comuna.
     *
     * @return nombre de comuna
     */
    public String getCommuneName() {
        return communeName;
    }

    /**
     * Establece el nombre de la comuna.
     *
     * @param communeName nombre de comuna
     */
    public void setCommuneName(String communeName) {
        this.communeName = communeName;
    }

    /**
     * Obtiene el nombre de la localidad.
     *
     * @return nombre de localidad
     */
    public String getLocalityName() {
        return localityName;
    }

    /**
     * Establece el nombre de la localidad.
     *
     * @param localityName nombre de localidad
     */
    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    /**
     * Obtiene la dirección del local.
     *
     * @return dirección de la farmacia
     */
    public String getPharmacyAddress() {
        return pharmacyAddress;
    }

    /**
     * Establece la dirección del local.
     *
     * @param pharmacyAddress dirección de la farmacia
     */
    public void setPharmacyAddress(String pharmacyAddress) {
        this.pharmacyAddress = pharmacyAddress;
    }

    /**
     * Obtiene la hora de apertura.
     *
     * @return hora de inicio de operación
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Establece la hora de apertura.
     *
     * @param startTime hora de inicio de operación
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Obtiene la hora de cierre.
     *
     * @return hora de fin de operación
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Establece la hora de cierre.
     *
     * @param endTime hora de fin de operación
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Obtiene el número de teléfono del local.
     *
     * @return teléfono de la farmacia
     */
    public String getPharmacyPhone() {
        return pharmacyPhone;
    }

    /**
     * Establece el número de teléfono del local.
     *
     * @param pharmacyPhone teléfono de la farmacia
     */
    public void setPharmacyPhone(String pharmacyPhone) {
        this.pharmacyPhone = pharmacyPhone;
    }

    /**
     * Obtiene la latitud de la ubicación.
     *
     * @return latitud en grados decimales
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Establece la latitud de la ubicación.
     *
     * @param latitude latitud en grados decimales
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Obtiene la longitud de la ubicación.
     *
     * @return longitud en grados decimales
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Establece la longitud de la ubicación.
     *
     * @param longitude longitud en grados decimales
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Obtiene el día de la semana en que opera la farmacia.
     *
     * @return día de operación (ej. "Lunes")
     */
    public String getOperationDay() {
        return operationDay;
    }

    /**
     * Establece el día de la semana en que opera la farmacia.
     *
     * @param operationDay día de operación
     */
    public void setOperationDay(String operationDay) {
        this.operationDay = operationDay;
    }
}
