package cl.sebastian.cm.rest.domain.data;

import cl.sebastian.cm.rest.domain.Seba;
import cl.sebastian.cm.rest.domain.model.Observation;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.OffsetDateTime;

/**
 * DTO de observación meteorológica proveniente de
 * <a href="https://redmeteo.cl">redmeteo.cl</a>.
 *
 * <p>
 * Mapea directamente las claves del JSON de origen mediante Jackson. Los campos
 * desconocidos se ignoran y los atributos con valor {@code null} no se
 * serializan.
 *
 * <h2>Convenciones y unidades</h2>
 * <ul>
 * <li><b>Temperatura</b>: °C</li>
 * <li><b>Humedad relativa</b>: % (0–100)</li>
 * <li><b>Velocidad del viento</b>: m/s o km/h (según fuente)</li>
 * <li><b>Dirección del viento</b>: grados (0–360)</li>
 * <li><b>Radiación solar</b>: W/m²</li>
 * <li><b>Presión / presión absoluta</b>: hPa</li>
 * <li><b>Precipitación</b>: mm (del intervalo)</li>
 * <li><b>Tasa de lluvia</b>: mm/h</li>
 * <li><b>Lluvia diaria</b>: mm acumulados del día</li>
 * <li><b>Fecha/hora</b>: {@link OffsetDateTime} (ISO-8601 con offset)</li>
 * </ul>
 *
 * <p>
 * <b>Nulabilidad:</b> los campos numéricos pueden ser {@code null} si la fuente
 * no entrega el dato. Se omiten en la serialización (por
 * {@link JsonInclude#NON_NULL}).</p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "MeteoObs",
        description = "Observación meteorológica mapeada desde redmeteo.cl (unidades en la descripción de cada campo)."
)
public class MeteoObs extends Seba {

    /**
     * Identificador propio de la observación (si lo entrega el origen).
     */
    @Schema(
            description = "Identificador propio de la observación (si lo entrega el origen).",
            example = "obs-123",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("id_observacion")
    private String observationId;

    /**
     * Fecha y hora de la medición (ISO-8601 con zona/offset).
     */
    @Schema(
            description = "Fecha y hora de la medición (ISO-8601 con zona/offset).",
            example = "2025-11-10T14:31:00-03:00",
            format = "date-time",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("fecha_hora")
    private OffsetDateTime measurementDateTime;

    /**
     * Temperatura en °C.
     */
    @Schema(
            description = "Temperatura en °C.",
            example = "18.7",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("temperatura")
    private Double temperature;

    /**
     * Humedad relativa en % (0–100).
     */
    @Schema(
            description = "Humedad relativa en % (0–100).",
            example = "63",
            minimum = "0",
            maximum = "100",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("humedad")
    private Double humidity;

    /**
     * Velocidad del viento (m/s o km/h según fuente).
     */
    @Schema(
            description = "Velocidad del viento (m/s o km/h según fuente).",
            example = "3.2",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("velocidad_viento")
    private Double windSpeed;

    /**
     * Dirección del viento en grados (0–360).
     */
    @Schema(
            description = "Dirección del viento en grados (0–360).",
            example = "255",
            minimum = "0",
            maximum = "360",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("direccion_viento")
    private Long windDirection;

    /**
     * Radiación solar en W/m².
     */
    @Schema(
            description = "Radiación solar en W/m².",
            example = "420.0",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("radiacion_solar")
    private Double solarRadiation;

    /**
     * Presión absoluta en hPa.
     */
    @Schema(
            description = "Presión absoluta en hPa.",
            example = "1012.3",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("presion_absoluta")
    private Double absolutePressure;

    /**
     * Precipitación del período/intervalo en mm.
     */
    @Schema(
            description = "Precipitación del período/intervalo en mm.",
            example = "0.0",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("precipitacion")
    private Double precipitation;

    /**
     * Punto de rocío en °C.
     */
    @Schema(
            description = "Punto de rocío en °C.",
            example = "11.5",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("punto_rocio")
    private Double dewPoint;

    /**
     * Racha máxima de viento (misma unidad que velocidad del viento).
     */
    @Schema(
            description = "Racha máxima de viento (misma unidad que velocidad del viento).",
            example = "5.8",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("racha_viento")
    private Double windGust;

    /**
     * Presión en hPa (según definición de la fuente).
     */
    @Schema(
            description = "Presión en hPa (según definición de la fuente).",
            example = "1011.6",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("presion")
    private Double pressure;

    /**
     * Tasa de lluvia en mm/h. Clave principal {@code tasalluvia}; acepta alias
     * {@code tasa_lluvia}.
     */
    @Schema(
            description = "Tasa de lluvia en mm/h.",
            example = "0.0",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("tasalluvia")
    @JsonAlias("tasa_lluvia")
    private Double rainRate;

    /**
     * Índice UV (entero).
     */
    @Schema(
            description = "Índice UV (entero).",
            example = "5",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("ultravioleta")
    private Long ultraviolet;

    /**
     * Lluvia diaria acumulada en mm. Clave principal {@code lluviadiaria};
     * alias {@code lluvia_diaria}.
     */
    @Schema(
            description = "Lluvia diaria acumulada en mm.",
            example = "0.0",
            minimum = "0",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("lluviadiaria")
    @JsonAlias("lluvia_diaria")
    private Double dailyRainfall;

    /**
     * Constructor por defecto requerido por Jackson.
     */
    public MeteoObs() {
    }

    /**
     * Crea una instancia a partir de un {@link Observation} de dominio.
     *
     * <p>
     * Copia directa de valores sin convertir unidades. Verifica que las
     * unidades de {@code Observation} correspondan a las declaradas en esta
     * clase.</p>
     *
     * @param obs fuente de datos de observación; si es {@code null}, los campos
     * permanecen en {@code null}.
     */
    public MeteoObs(Observation obs) {
        this.observationId = obs.getCode();
        this.measurementDateTime = obs.getDate();
        this.temperature = obs.getTemperature();
        this.humidity = obs.getHumidity();
        this.windSpeed = obs.getWindSpeed();
        this.windDirection = obs.getWindDirection();
        this.solarRadiation = obs.getSolarRadiation();
        this.absolutePressure = obs.getAbsolutePressure();
        this.precipitation = obs.getPrecipitation();
        this.dewPoint = obs.getDewPoint();
        this.windGust = obs.getWindGust();
        this.pressure = obs.getPressure();
        this.rainRate = obs.getRainRate();
        this.ultraviolet = obs.getUltraviolet();
        this.dailyRainfall = obs.getDailyRainfall();
    }

    /**
     * @return identificador propio de la observación.
     */
    public String getObservationId() {
        return observationId;
    }

    /**
     * @param observationId identificador propio de la observación.
     */
    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    /**
     * @return fecha y hora de la medición.
     */
    public OffsetDateTime getMeasurementDateTime() {
        return measurementDateTime;
    }

    /**
     * @param measurementDateTime fecha y hora de la medición.
     */
    public void setMeasurementDateTime(OffsetDateTime measurementDateTime) {
        this.measurementDateTime = measurementDateTime;
    }

    /**
     * @return temperatura en °C.
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * @param temperature temperatura en °C.
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    /**
     * @return humedad relativa en % (0–100).
     */
    public Double getHumidity() {
        return humidity;
    }

    /**
     * @param humidity humedad relativa en % (0–100).
     */
    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    /**
     * @return velocidad del viento.
     */
    public Double getWindSpeed() {
        return windSpeed;
    }

    /**
     * @param windSpeed velocidad del viento.
     */
    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * @return dirección del viento en grados (0–360).
     */
    public Long getWindDirection() {
        return windDirection;
    }

    /**
     * @param windDirection dirección del viento en grados (0–360).
     */
    public void setWindDirection(Long windDirection) {
        this.windDirection = windDirection;
    }

    /**
     * @return radiación solar en W/m².
     */
    public Double getSolarRadiation() {
        return solarRadiation;
    }

    /**
     * @param solarRadiation radiación solar en W/m².
     */
    public void setSolarRadiation(Double solarRadiation) {
        this.solarRadiation = solarRadiation;
    }

    /**
     * @return presión absoluta en hPa.
     */
    public Double getAbsolutePressure() {
        return absolutePressure;
    }

    /**
     * @param absolutePressure presión absoluta en hPa.
     */
    public void setAbsolutePressure(Double absolutePressure) {
        this.absolutePressure = absolutePressure;
    }

    /**
     * @return precipitación en mm (del intervalo).
     */
    public Double getPrecipitation() {
        return precipitation;
    }

    /**
     * @param precipitation precipitación en mm (del intervalo).
     */
    public void setPrecipitation(Double precipitation) {
        this.precipitation = precipitation;
    }

    /**
     * @return punto de rocío en °C.
     */
    public Double getDewPoint() {
        return dewPoint;
    }

    /**
     * @param dewPoint punto de rocío en °C.
     */
    public void setDewPoint(Double dewPoint) {
        this.dewPoint = dewPoint;
    }

    /**
     * @return racha máxima de viento.
     */
    public Double getWindGust() {
        return windGust;
    }

    /**
     * @param windGust racha máxima de viento.
     */
    public void setWindGust(Double windGust) {
        this.windGust = windGust;
    }

    /**
     * @return presión en hPa.
     */
    public Double getPressure() {
        return pressure;
    }

    /**
     * @param pressure presión en hPa.
     */
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    /**
     * @return tasa de lluvia en mm/h.
     */
    public Double getRainRate() {
        return rainRate;
    }

    /**
     * @param rainRate tasa de lluvia en mm/h.
     */
    public void setRainRate(Double rainRate) {
        this.rainRate = rainRate;
    }

    /**
     * @return índice UV (entero).
     */
    public Long getUltraviolet() {
        return ultraviolet;
    }

    /**
     * @param ultraviolet índice UV (entero).
     */
    public void setUltraviolet(Long ultraviolet) {
        this.ultraviolet = ultraviolet;
    }

    /**
     * @return lluvia diaria acumulada en mm.
     */
    public Double getDailyRainfall() {
        return dailyRainfall;
    }

    /**
     * @param dailyRainfall lluvia diaria acumulada en mm.
     */
    public void setDailyRainfall(Double dailyRainfall) {
        this.dailyRainfall = dailyRainfall;
    }
}
