package cl.sebastian.cm.rest.domain.data;

import cl.sebastian.cm.rest.domain.Seba;
import cl.sebastian.cm.rest.domain.model.Pharmacy;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

/**
 * DTO (Data Transfer Object) que representa una farmacia para la API REST.
 * <p>
 * Esta clase extiende {@link Seba}, heredando el identificador único
 * ({@code id}) y las marcas de tiempo de creación y actualización
 * ({@code createdAt}, {@code updatedAt}), lo que permite mantener la
 * trazabilidad de los registros expuestos.
 * </p>
 * <p>
 * Se utiliza para transferir información de farmacias entre el cliente y el
 * servidor en las operaciones CRUD de la API. Los nombres de los campos en la
 * serialización JSON se definen en español mediante {@link JsonProperty} para
 * facilitar el consumo desde aplicaciones frontend en el contexto chileno.
 * </p>
 * <p>
 * La conversión desde la entidad de dominio {@link Pharmacy} a este DTO se
 * realiza mediante el constructor {@link #Pharma(Pharmacy)}, que mapea los
 * atributos relevantes.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see Seba
 * @see Pharmacy
 */
@Schema(description = "DTO que representa una farmacia con sus datos comerciales, ubicación geográfica y horarios de atención")
public class Pharma extends Seba {

    /**
     * Cadena o tipo de comercio al que pertenece la farmacia (ej. "Ahumada",
     * "Cruz Verde", "Salcobrand"). Este valor se obtiene del enumerado
     * {@code Commerce} de la entidad {@link Pharmacy}.
     */
    @Schema(description = "Cadena o tipo de comercio de la farmacia (ej. Ahumada, Cruz Verde, Salcobrand)",
            example = "Ahumada")
    @JsonProperty("cadena")
    private String commerce;

    /**
     * Identificador único de la tienda dentro del sistema de comercio. Es un
     * valor numérico entero que no se repite en la tabla de farmacias.
     */
    @Schema(description = "Identificador único de la tienda en el sistema del comercio (clave foránea)",
            example = "12345")
    @JsonProperty("tienda")
    private Integer storeId;

    /**
     * Nombre comercial de la farmacia, tal como se muestra al público.
     */
    @Schema(description = "Nombre comercial completo de la farmacia",
            example = "Farmacia Ahumada Providencia")
    @JsonProperty("nombre")
    private String name;

    /**
     * Dirección física de la farmacia, incluyendo calle, número y comuna si
     * corresponde.
     */
    @Schema(description = "Dirección física de la farmacia (calle, número, comuna)",
            example = "Av. Providencia 1000, Providencia")
    @JsonProperty("direccion")
    private String address;

    /**
     * Coordenada de latitud (WGS84) que indica la ubicación geográfica de la
     * farmacia.
     */
    @Schema(description = "Latitud de la farmacia en grados decimales (WGS84)",
            example = "-33.476614")
    private Double latitude;

    /**
     * Coordenada de longitud (WGS84) que indica la ubicación geográfica de la
     * farmacia.
     */
    @Schema(description = "Longitud de la farmacia en grados decimales (WGS84)",
            example = "-70.634325")
    private Double longitude;

    /**
     * Número de teléfono de contacto de la farmacia, incluyendo código de área
     * si es necesario. Se almacena como {@code Long} para evitar pérdida de
     * precisión en números largos.
     */
    @Schema(description = "Número de teléfono de la farmacia (incluye código de país o área)",
            example = "56912345678")
    @JsonProperty("telefono")
    private Long phone;

    /**
     * Hora de inicio de la jornada laboral normal (apertura). Se representa con
     * {@link LocalTime} y se serializa en formato HH:mm.
     */
    @Schema(description = "Hora de apertura en horario normal (formato HH:mm)",
            example = "08:30")
    @JsonProperty("apertura_normal")
    private LocalTime startTime;

    /**
     * Hora de fin de la jornada laboral normal (cierre). Se representa con
     * {@link LocalTime} y se serializa en formato HH:mm.
     */
    @Schema(description = "Hora de cierre en horario normal (formato HH:mm)",
            example = "22:00")
    @JsonProperty("cierre_normal")
    private LocalTime endTime;

    /**
     * Constructor por defecto requerido para frameworks de serialización
     * (Jackson, etc.).
     */
    public Pharma() {
        // Constructor vacío
    }

    /**
     * Construye un DTO {@code Pharma} a partir de una entidad {@link Pharmacy}.
     * <p>
     * Este constructor mapea los atributos de la entidad de dominio al DTO,
     * extrayendo el valor textual del enumerado {@code Commerce} para el campo
     * {@link #commerce}. Los campos heredados de {@link Seba} (id, createdAt,
     * updatedAt) se mantienen con los valores de la entidad.
     * </p>
     *
     * @param pharmacy entidad de dominio {@link Pharmacy} con los datos de la
     * farmacia. No debe ser {@code null}.
     * @throws NullPointerException si {@code pharmacy} es {@code null}.
     */
    public Pharma(Pharmacy pharmacy) {
        // Nota: los campos heredados (id, createdAt, updatedAt) permanecen sin asignar
        // explícitamente; pueden asignarse mediante setters o en la capa de servicio.
        this.commerce = pharmacy.getCommerce().getLabel();
        this.storeId = pharmacy.getStoreId();
        this.name = pharmacy.getName();
        this.address = pharmacy.getAddress();
        this.latitude = pharmacy.getLatitude();
        this.longitude = pharmacy.getLongitude();
        this.phone = pharmacy.getPhone();
        this.startTime = pharmacy.getStartTime();
        this.endTime = pharmacy.getEndTime();
    }

    /**
     * Obtiene la cadena o tipo de comercio de la farmacia.
     *
     * @return el nombre del comercio (ej. "Ahumada").
     */
    public String getCommerce() {
        return commerce;
    }

    /**
     * Establece la cadena o tipo de comercio de la farmacia.
     *
     * @param commerce el nuevo comercio (debe ser un valor válido según el
     * enumerado {@code Commerce}).
     */
    public void setCommerce(String commerce) {
        this.commerce = commerce;
    }

    /**
     * Obtiene el identificador único de la tienda dentro del sistema del
     * comercio.
     *
     * @return el storeId (identificador numérico).
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
     * @return el nombre comercial.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre comercial de la farmacia.
     *
     * @param name el nuevo nombre comercial.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la dirección física de la farmacia.
     *
     * @return la dirección (calle, número, comuna).
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
     * Obtiene la latitud de la farmacia.
     *
     * @return la latitud en grados decimales.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Establece la latitud de la farmacia.
     *
     * @param latitude la nueva latitud.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Obtiene la longitud de la farmacia.
     *
     * @return la longitud en grados decimales.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Establece la longitud de la farmacia.
     *
     * @param longitude la nueva longitud.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Obtiene el número de teléfono de la farmacia.
     *
     * @return el teléfono (como {@code Long}).
     */
    public Long getPhone() {
        return phone;
    }

    /**
     * Establece el número de teléfono de la farmacia.
     *
     * @param phone el nuevo número de teléfono.
     */
    public void setPhone(Long phone) {
        this.phone = phone;
    }

    /**
     * Obtiene la hora de apertura en horario normal.
     *
     * @return la hora de inicio (objeto {@link LocalTime}).
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Establece la hora de apertura en horario normal.
     *
     * @param startTime la nueva hora de inicio.
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Obtiene la hora de cierre en horario normal.
     *
     * @return la hora de fin (objeto {@link LocalTime}).
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Establece la hora de cierre en horario normal.
     *
     * @param endTime la nueva hora de fin.
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
