package cl.sebastian.cm.rest.domain.data;

import cl.sebastian.cm.rest.domain.Seba;
import cl.sebastian.cm.rest.domain.model.Pharmacy;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

/**
 * DTO que representa una farmacia para la API REST.
 * <p>
 * Esta clase extiende {@link Seba}, que proporciona el identificador único
 * ({@code id}) y las marcas de tiempo de creación y actualización.
 * </p>
 * <p>
 * Se utiliza para transferir información de farmacias entre el cliente y el
 * servidor, con nombres de campos en español para la serialización JSON
 * (mediante {@link JsonProperty}).
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see Seba
 */
@Schema(description = "Representa una farmacia con sus datos comerciales, ubicación y horarios")
public class Pharma extends Seba {

    /**
     * Cadena o tipo de comercio al que pertenece la farmacia (ej. "Ahumada",
     * "Cruz Verde").
     */
    @Schema(description = "Cadena o tipo de comercio de la farmacia", example = "Ahumada")
    @JsonProperty("cadena")
    private String commerce;

    /**
     * Identificador único de la tienda dentro del sistema de comercio. Es un
     * valor numérico que no se repite en la tabla.
     */
    @Schema(description = "Identificador único de la tienda en el sistema de comercio", example = "12345")
    @JsonProperty("tienda")
    private Integer storeId;

    /**
     * Nombre comercial de la farmacia.
     */
    @Schema(description = "Nombre comercial de la farmacia", example = "Farmacia Ahumada Providencia")
    @JsonProperty("nombre")
    private String name;

    /**
     * Dirección física de la farmacia.
     */
    @Schema(description = "Dirección física de la farmacia", example = "Av. Providencia 1000")
    @JsonProperty("direccion")
    private String address;

    /**
     * Número de teléfono de contacto de la farmacia.
     */
    @Schema(description = "Número de teléfono de la farmacia",
            example = "56912345678")
    @JsonProperty("telefono")
    private Long phone;

    /**
     * Hora de inicio de la jornada laboral (apertura).
     */
    @Schema(description = "Hora de apertura de la farmacia (formato HH:mm)", example = "08:30")
    @JsonProperty("apertura_normal")
    private LocalTime startTime;

    /**
     * Hora de fin de la jornada laboral (cierre).
     */
    @Schema(description = "Hora de cierre de la farmacia (formato HH:mm)", example = "22:00")
    @JsonProperty("cierre_normal")
    private LocalTime endTime;

    public Pharma() {
    }

    public Pharma(Pharmacy pharmacy) {
        this.commerce = pharmacy.getCommerce().getLabel();
        this.storeId = pharmacy.getStoreId();
        this.name = pharmacy.getName();
        this.address = pharmacy.getAddress();
        this.phone = pharmacy.getPhone();
        this.startTime = pharmacy.getStartTime();
        this.endTime = pharmacy.getEndTime();
    }

    /**
     * Obtiene la cadena o tipo de comercio.
     *
     * @return el comercio (ej. "Ahumada").
     */
    public String getCommerce() {
        return commerce;
    }

    /**
     * Establece la cadena o tipo de comercio.
     *
     * @param commerce el nuevo comercio.
     */
    public void setCommerce(String commerce) {
        this.commerce = commerce;
    }

    /**
     * Obtiene el identificador único de la tienda.
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
}
