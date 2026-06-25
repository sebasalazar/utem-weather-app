package cl.sebastian.cm.rest.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entidad que representa una observación meteorológica puntual capturada por
 * una estación.
 * <p>
 * Incluye referencia a la {@link Station} que reporta el dato, un código del
 * tipo de observación/variable, la fecha-hora de la medición y distintos campos
 * físicos (temperatura, humedad, viento, radiación, presión, precipitación,
 * etc.).
 * </p>
 *
 * <h2>Convenciones</h2>
 * <ul>
 * <li>Los campos numéricos usan <b>-1</b> como valor “sentinel” para indicar
 * dato no disponible.</li>
 * <li>Las unidades dependen de tu proveedor de datos. Convenciones habituales:
 * temperatura (°C), humedad relativa (%), velocidad de viento (m/s o km/h),
 * dirección de viento (grados meteorológicos 0–360), radiación solar (W/m²),
 * presión (hPa), precipitación (mm), tasa de lluvia (mm/h), punto de rocío
 * (°C), racha de viento (igual unidad que velocidad).</li>
 * <li>{@code date} se inicializa con la hora actual del sistema.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
@Entity
@Table(name = "observations")
public class Observation extends PkEntityBase {

    /**
     * Estación que emite la observación.
     * <p>
     * FK: {@code observations.station_fk} → {@code stations.pk}.</p>
     */
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Station.class)
    @JoinColumn(name = "station_fk", nullable = false)
    private Station station = null;

    /**
     * Código de la observación.
     */
    @Column(name = "code", nullable = false)
    private String code = null;

    /**
     * Fecha-hora de la observación.
     */
    @Column(name = "date_time", nullable = false)
    private OffsetDateTime date = OffsetDateTime.now();

    /**
     * Temperatura (convención típica: °C). {@code -1.0} indica dato no
     * disponible.
     */
    @Column(name = "temperature", nullable = false)
    private Double temperature = -1.0;

    /**
     * Humedad relativa (convención típica: %). {@code -1.0} indica dato no
     * disponible.
     */
    @Column(name = "humidity", nullable = false)
    private Double humidity = -1.0;

    /**
     * Velocidad del viento (m/s o km/h, según tu convención). {@code -1.0} =
     * N/D.
     */
    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed = -1.0;

    /**
     * Dirección del viento (grados 0–360). {@code -1L} = N/D.
     */
    @Column(name = "wind_direction", nullable = false)
    private Long windDirection = -1L;

    /**
     * Radiación solar (W/m² habitual). {@code -1.0} = N/D.
     */
    @Column(name = "solar_radiation", nullable = false)
    private Double solarRadiation = -1.0;

    /**
     * Presión absoluta (hPa habitual). {@code -1.0} = N/D.
     */
    @Column(name = "absolute_pressure", nullable = false)
    private Double absolutePressure = -1.0;

    /**
     * Precipitación acumulada del evento/intervalo (mm habitual). {@code -1.0}
     * = N/D.
     */
    @Column(name = "precipitation", nullable = false)
    private Double precipitation = -1.0;

    /**
     * Punto de rocío (°C habitual). {@code -1.0} = N/D.
     */
    @Column(name = "dew_point", nullable = false)
    private Double dewPoint = -1.0;

    /**
     * Racha de viento (misma unidad que velocidad). {@code -1.0} = N/D.
     */
    @Column(name = "wind_gust", nullable = false)
    private Double windGust = -1.0;

    /**
     * Presión (redundante u otra referencia; aclara en tu dominio).
     * {@code -1.0} = N/D.
     */
    @Column(name = "pressure", nullable = false)
    private Double pressure = -1.0;

    /**
     * Tasa de lluvia (mm/h típico). {@code -1.0} = N/D.
     */
    @Column(name = "rain_rate", nullable = false)
    private Double rainRate = -1.0;

    /**
     * Índice UV (entero). {@code -1L} = N/D.
     */
    @Column(name = "ultraviolet", nullable = false)
    private Long ultraviolet = -1L;

    /**
     * Lluvia diaria acumulada a la hora de la observación (mm típico).
     * {@code -1.0} = N/D.
     */
    @Column(name = "daily_rainfall", nullable = false)
    private Double dailyRainfall = -1.0;

    /**
     * @return estación que reporta la observación.
     */
    public Station getStation() {
        return station;
    }

    /**
     * Define la estación asociada.
     *
     * @param station estación emisora (no nula en persistencia)
     */
    public void setStation(Station station) {
        this.station = station;
    }

    /**
     * @return código de la variable/tipo de observación.
     */
    public String getCode() {
        return code;
    }

    /**
     * Define el código de la observación.
     *
     * @param code identificador
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return fecha-hora de la observación.
     */
    public OffsetDateTime getDate() {
        return date;
    }

    /**
     * Define la fecha-hora de la observación.
     *
     * @param date fecha-hora
     */
    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    /**
     * @return temperatura (°C típico) o -1 si N/D.
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * @param temperature temperatura (°C típico); usa -1 para N/D si mantienes
     * el sentinel.
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    /**
     * @return humedad relativa (%) o -1 si N/D.
     */
    public Double getHumidity() {
        return humidity;
    }

    /**
     * @param humidity humedad relativa (%) ; usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    /**
     * @return velocidad del viento (m/s o km/h) o -1 si N/D.
     */
    public Double getWindSpeed() {
        return windSpeed;
    }

    /**
     * @param windSpeed velocidad del viento; usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * @return dirección del viento (0–360) o -1 si N/D.
     */
    public Long getWindDirection() {
        return windDirection;
    }

    /**
     * @param windDirection dirección (0–360); usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setWindDirection(Long windDirection) {
        this.windDirection = windDirection;
    }

    /**
     * @return radiación solar (W/m²) o -1 si N/D.
     */
    public Double getSolarRadiation() {
        return solarRadiation;
    }

    /**
     * @param solarRadiation radiación solar; usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setSolarRadiation(Double solarRadiation) {
        this.solarRadiation = solarRadiation;
    }

    /**
     * @return presión absoluta (hPa) o -1 si N/D.
     */
    public Double getAbsolutePressure() {
        return absolutePressure;
    }

    /**
     * @param absolutePressure presión absoluta; usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setAbsolutePressure(Double absolutePressure) {
        this.absolutePressure = absolutePressure;
    }

    /**
     * @return precipitación (mm) o -1 si N/D.
     */
    public Double getPrecipitation() {
        return precipitation;
    }

    /**
     * @param precipitation precipitación (mm); usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setPrecipitation(Double precipitation) {
        this.precipitation = precipitation;
    }

    /**
     * @return punto de rocío (°C) o -1 si N/D.
     */
    public Double getDewPoint() {
        return dewPoint;
    }

    /**
     * @param dewPoint punto de rocío (°C); usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setDewPoint(Double dewPoint) {
        this.dewPoint = dewPoint;
    }

    /**
     * @return racha de viento (unidad de velocidad) o -1 si N/D.
     */
    public Double getWindGust() {
        return windGust;
    }

    /**
     * @param windGust racha de viento; usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setWindGust(Double windGust) {
        this.windGust = windGust;
    }

    /**
     * @return presión (hPa u otra referencia) o -1 si N/D.
     */
    public Double getPressure() {
        return pressure;
    }

    /**
     * @param pressure presión; usa -1 para N/D si mantienes el sentinel.
     */
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    /**
     * @return tasa de lluvia (mm/h) o -1 si N/D.
     */
    public Double getRainRate() {
        return rainRate;
    }

    /**
     * @param rainRate tasa de lluvia (mm/h); usa -1 para N/D si mantienes el
     * sentinel.
     */
    public void setRainRate(Double rainRate) {
        this.rainRate = rainRate;
    }

    /**
     * @return índice UV (entero) o -1 si N/D.
     */
    public Long getUltraviolet() {
        return ultraviolet;
    }

    /**
     * @param ultraviolet índice UV; usa -1 para N/D si mantienes el sentinel.
     */
    public void setUltraviolet(Long ultraviolet) {
        this.ultraviolet = ultraviolet;
    }

    /**
     * @return lluvia diaria acumulada (mm) o -1 si N/D.
     */
    public Double getDailyRainfall() {
        return dailyRainfall;
    }

    /**
     * @param dailyRainfall lluvia diaria acumulada (mm); usa -1 para N/D si
     * mantienes el sentinel.
     */
    public void setDailyRainfall(Double dailyRainfall) {
        this.dailyRainfall = dailyRainfall;
    }

    /**
     * Igualdad por clave natural compuesta: (station.id, code).
     * <p>
     * Ventajas:
     * <ul>
     * <li>Evita comparaciones profundas/proxies de JPA (usa el id de
     * Station).</li>
     * <li>Coherente con estructuras hash si (station, code) son inmutables en
     * práctica.</li>
     * </ul>
     *
     * @return valor hash consistente con {@code equals}
     */
    @Override
    public int hashCode() {
        final Long stationId = (station != null ? station.getId() : null);
        int result = 17;
        result = 31 * result + Objects.hashCode(stationId);
        result = 31 * result + Objects.hashCode(code);
        return result;
    }

    /**
     * Dos observaciones son iguales si pertenecen a la misma estación (por id)
     * y comparten el mismo {@code code}.
     *
     * @param obj otro objeto
     * @return {@code true} si (station.id, code) coinciden; {@code false} en
     * caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Observation other = (Observation) obj;

        final Long thisStationId = (this.station != null ? this.station.getId() : null);
        final Long otherStationId = (other.station != null ? other.station.getId() : null);

        return Objects.equals(thisStationId, otherStationId)
                && Objects.equals(this.code, other.code);
    }
}
