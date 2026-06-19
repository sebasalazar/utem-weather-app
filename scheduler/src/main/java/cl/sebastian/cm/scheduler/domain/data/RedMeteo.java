package cl.sebastian.cm.scheduler.domain.data;

import cl.sebastian.cm.scheduler.domain.Seba;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * Objeto json de https://redmeteo.cl
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedMeteo extends Seba {

    /**
     * Identificador de la estación que emite la observación.
     */
    @JsonProperty("id_estacion")
    private String idEstacion;

    /**
     * Nombre legible de la estación.
     */
    @JsonProperty("nombre")
    private String nombre;

    /**
     * Latitud en grados decimales [-90, 90].
     */
    @JsonProperty("latitud")
    private Double latitud;

    /**
     * Longitud en grados decimales [-180, 180].
     */
    @JsonProperty("longitud")
    private Double longitud;

    /**
     * Altitud en metros sobre el nivel del mar.
     */
    @JsonProperty("altitud")
    private Integer altitud;

    /**
     * Identificador propio de la observación (si lo entrega el origen).
     */
    @JsonProperty("id_observacion")
    private String idObservacion;

    /**
     * Fecha y hora de la medición.
     */
    @JsonProperty("fecha_hora")
    private OffsetDateTime fechaHora;

    /**
     * Temperatura (°C).
     */
    @JsonProperty("temperatura")
    private Double temperatura;

    /**
     * Humedad relativa (%).
     */
    @JsonProperty("humedad")
    private Double humedad;

    /**
     * Velocidad del viento (m/s o km/h).
     */
    @JsonProperty("velocidad_viento")
    private Double velocidadViento;

    /**
     * Dirección del viento (grados 0–360).
     */
    @JsonProperty("direccion_viento")
    private Long direccionViento;

    /**
     * Radiación solar (W/m²).
     */
    @JsonProperty("radiacion_solar")
    private Double radiacionSolar;

    /**
     * Presión absoluta (hPa).
     */
    @JsonProperty("presion_absoluta")
    private Double presionAbsoluta;

    /**
     * Precipitación del período/intervalo (mm).
     */
    @JsonProperty("precipitacion")
    private Double precipitacion;

    /**
     * Punto de rocío (°C).
     */
    @JsonProperty("punto_rocio")
    private Double puntoRocio;

    /**
     * Racha de viento (misma unidad que velocidad).
     */
    @JsonProperty("racha_viento")
    private Double rachaViento;

    /**
     * Presión (hPa) – según definición de la fuente.
     */
    @JsonProperty("presion")
    private Double presion;

    /**
     * Tasa de lluvia (mm/h). Clave principal 'tasalluvia'; acepta alias
     * 'tasa_lluvia'.
     */
    @JsonProperty("tasalluvia")
    @JsonAlias("tasa_lluvia")
    private Double tasaLluvia;

    /**
     * Índice UV (entero).
     */
    @JsonProperty("ultravioleta")
    private Long ultravioleta;

    /**
     * Lluvia diaria acumulada (mm). Clave principal 'lluviadiaria'; acepta
     * alias 'lluvia_diaria'.
     */
    @JsonProperty("lluviadiaria")
    @JsonAlias("lluvia_diaria")
    private Double lluviaDiaria;

    public String getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(String idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Integer getAltitud() {
        return altitud;
    }

    public void setAltitud(Integer altitud) {
        this.altitud = altitud;
    }

    public String getIdObservacion() {
        return idObservacion;
    }

    public void setIdObservacion(String idObservacion) {
        this.idObservacion = idObservacion;
    }

    public OffsetDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(OffsetDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getHumedad() {
        return humedad;
    }

    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }

    public Double getVelocidadViento() {
        return velocidadViento;
    }

    public void setVelocidadViento(Double velocidadViento) {
        this.velocidadViento = velocidadViento;
    }

    public Long getDireccionViento() {
        return direccionViento;
    }

    public void setDireccionViento(Long direccionViento) {
        this.direccionViento = direccionViento;
    }

    public Double getRadiacionSolar() {
        return radiacionSolar;
    }

    public void setRadiacionSolar(Double radiacionSolar) {
        this.radiacionSolar = radiacionSolar;
    }

    public Double getPresionAbsoluta() {
        return presionAbsoluta;
    }

    public void setPresionAbsoluta(Double presionAbsoluta) {
        this.presionAbsoluta = presionAbsoluta;
    }

    public Double getPrecipitacion() {
        return precipitacion;
    }

    public void setPrecipitacion(Double precipitacion) {
        this.precipitacion = precipitacion;
    }

    public Double getPuntoRocio() {
        return puntoRocio;
    }

    public void setPuntoRocio(Double puntoRocio) {
        this.puntoRocio = puntoRocio;
    }

    public Double getRachaViento() {
        return rachaViento;
    }

    public void setRachaViento(Double rachaViento) {
        this.rachaViento = rachaViento;
    }

    public Double getPresion() {
        return presion;
    }

    public void setPresion(Double presion) {
        this.presion = presion;
    }

    public Double getTasaLluvia() {
        return tasaLluvia;
    }

    public void setTasaLluvia(Double tasaLluvia) {
        this.tasaLluvia = tasaLluvia;
    }

    public Long getUltravioleta() {
        return ultravioleta;
    }

    public void setUltravioleta(Long ultravioleta) {
        this.ultravioleta = ultravioleta;
    }

    public Double getLluviaDiaria() {
        return lluviaDiaria;
    }

    public void setLluviaDiaria(Double lluviaDiaria) {
        this.lluviaDiaria = lluviaDiaria;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("idEstacion", idEstacion)
                .append("nombre", nombre)
                .append("latitud", latitud)
                .append("longitud", longitud)
                .append("altitud", altitud)
                .append("idObservacion", idObservacion)
                .append("fechaHora", fechaHora)
                .append("temperatura", temperatura)
                .append("humedad", humedad)
                .append("velocidadViento", velocidadViento)
                .append("direccionViento", direccionViento)
                .append("radiacionSolar", radiacionSolar)
                .append("presionAbsoluta", presionAbsoluta)
                .append("precipitacion", precipitacion)
                .append("puntoRocio", puntoRocio)
                .append("rachaViento", rachaViento)
                .append("presion", presion)
                .append("tasaLluvia", tasaLluvia)
                .append("ultravioleta", ultravioleta)
                .append("lluviaDiaria", lluviaDiaria)
                .toString();
    }
}
