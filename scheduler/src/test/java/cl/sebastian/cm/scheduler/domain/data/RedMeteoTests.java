package cl.sebastian.cm.scheduler.domain.data;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para el DTO {@link RedMeteo}.
 * <p>
 * Verifica el correcto funcionamiento de los getters y setters del DTO que
 * representa los datos meteorológicos obtenidos desde RedMeteo. Se asegura de
 * que las propiedades se inicialicen correctamente con el constructor por
 * defecto, que los setters almacenen y recuperen los valores sin interferencias
 * entre campos, y que se pueda resetear a {@code null} cualquier propiedad.
 * </p>
 * <p>
 * Las pruebas cubren:
 * </p>
 * <ul>
 * <li>Estado inicial de todas las propiedades (deben ser {@code null}).</li>
 * <li>Ciclo completo de asignación y recuperación de todas las propiedades,
 * verificando que no haya interferencia entre campos.</li>
 * <li>Capacidad de resetear propiedades a {@code null} después de haber
 * asignado valores.</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see RedMeteo
 */
@DisplayName("RedMeteo - DTO para datos meteorológicos")
class RedMeteoTests {

    // -------------------------------------------------------------------------
    // Constantes de prueba (evita números mágicos y strings dispersos)
    // -------------------------------------------------------------------------
    /**
     * Identificador de estación de prueba.
     */
    private static final String SAMPLE_ID_ESTACION = "EST001";

    /**
     * Nombre de estación de prueba.
     */
    private static final String SAMPLE_NOMBRE = "Estación Metropolitana";

    /**
     * Latitud de prueba.
     */
    private static final Double SAMPLE_LATITUD = -33.456;

    /**
     * Longitud de prueba.
     */
    private static final Double SAMPLE_LONGITUD = -70.654;

    /**
     * Altitud de prueba (en metros).
     */
    private static final Integer SAMPLE_ALTITUD = 700;

    /**
     * Identificador de observación de prueba.
     */
    private static final String SAMPLE_ID_OBSERVACION = "OBS001";

    /**
     * Temperatura de prueba (en °C).
     */
    private static final Double SAMPLE_TEMPERATURA = 25.5;

    /**
     * Humedad de prueba (en %).
     */
    private static final Double SAMPLE_HUMEDAD = 65.0;

    /**
     * Velocidad del viento de prueba (en km/h).
     */
    private static final Double SAMPLE_VELOCIDAD_VIENTO = 10.0;

    /**
     * Dirección del viento de prueba (en grados).
     */
    private static final Long SAMPLE_DIRECCION_VIENTO = 180L;

    /**
     * Radiación solar de prueba (en W/m²).
     */
    private static final Double SAMPLE_RADIACION_SOLAR = 500.0;

    /**
     * Presión atmosférica de prueba (en hPa).
     */
    private static final Double SAMPLE_PRESION = 1013.25;

    /**
     * Presión absoluta de prueba (en hPa).
     */
    private static final Double SAMPLE_PRESION_ABSOLUTA = 1013.25;

    // -------------------------------------------------------------------------
    // Estado inicial del DTO
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica que el constructor por defecto inicializa todas las
     * propiedades del DTO a {@code null}.
     * <p>
     * Esta prueba asegura que no haya valores predeterminados no deseados que
     * puedan interferir con la lógica de negocio.
     * </p>
     */
    @Test
    @DisplayName("Constructor por defecto: inicializa todas las propiedades en null")
    void shouldInitializeAllPropertiesAsNullWhenCreatedWithDefaultConstructor() {
        // Arrange
        // (No requiere configuración; el constructor por defecto es el SUT)

        // Act
        RedMeteo redMeteo = new RedMeteo();

        // Assert
        Assertions.assertNull(redMeteo.getIdEstacion(), "idEstacion debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getNombre(), "nombre debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getLatitud(), "latitud debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getLongitud(), "longitud debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getAltitud(), "altitud debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getIdObservacion(), "idObservacion debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getFechaHora(), "fechaHora debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getTemperatura(), "temperatura debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getHumedad(), "humedad debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getVelocidadViento(), "velocidadViento debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getDireccionViento(), "direccionViento debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getRadiacionSolar(), "radiacionSolar debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getPresion(), "presion debe ser null inicialmente");
        Assertions.assertNull(redMeteo.getPresionAbsoluta(), "presionAbsoluta debe ser null inicialmente");
    }

    // -------------------------------------------------------------------------
    // Ciclo de vida completo del DTO (reemplaza los 12 tests individuales)
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica el ciclo completo de asignación y recuperación de
     * todas las propiedades del DTO.
     * <p>
     * Asigna valores a todas las propiedades (incluyendo
     * {@link OffsetDateTime}) y luego verifica que cada getter retorne el valor
     * correcto, asegurando que no haya interferencia entre campos (por ejemplo,
     * que un setter no sobrescriba accidentalmente otro).
     * </p>
     * <p>
     * Esta prueba consolida múltiples verificaciones individuales en un solo
     * método, reduciendo la redundancia y manteniendo la claridad.
     * </p>
     */
    @Test
    @DisplayName("Propiedades: almacena y recupera todos los valores sin interferencia entre campos")
    void shouldStoreAndRetrieveAllPropertiesWithoutInterference() {
        // Arrange
        RedMeteo redMeteo = new RedMeteo();
        OffsetDateTime fechaHoraMuestra = OffsetDateTime.now();

        // Act - Poblado completo del DTO
        redMeteo.setIdEstacion(SAMPLE_ID_ESTACION);
        redMeteo.setNombre(SAMPLE_NOMBRE);
        redMeteo.setLatitud(SAMPLE_LATITUD);
        redMeteo.setLongitud(SAMPLE_LONGITUD);
        redMeteo.setAltitud(SAMPLE_ALTITUD);
        redMeteo.setIdObservacion(SAMPLE_ID_OBSERVACION);
        redMeteo.setFechaHora(fechaHoraMuestra);
        redMeteo.setTemperatura(SAMPLE_TEMPERATURA);
        redMeteo.setHumedad(SAMPLE_HUMEDAD);
        redMeteo.setVelocidadViento(SAMPLE_VELOCIDAD_VIENTO);
        redMeteo.setDireccionViento(SAMPLE_DIRECCION_VIENTO);
        redMeteo.setRadiacionSolar(SAMPLE_RADIACION_SOLAR);
        redMeteo.setPresion(SAMPLE_PRESION);
        redMeteo.setPresionAbsoluta(SAMPLE_PRESION_ABSOLUTA);

        // Assert - Verifica que ningún setter sobrescribió accidentalmente otro campo
        Assertions.assertEquals(SAMPLE_ID_ESTACION, redMeteo.getIdEstacion(), "idEstacion incorrecto");
        Assertions.assertEquals(SAMPLE_NOMBRE, redMeteo.getNombre(), "nombre incorrecto");
        Assertions.assertEquals(SAMPLE_LATITUD, redMeteo.getLatitud(), "latitud incorrecta");
        Assertions.assertEquals(SAMPLE_LONGITUD, redMeteo.getLongitud(), "longitud incorrecta");
        Assertions.assertEquals(SAMPLE_ALTITUD, redMeteo.getAltitud(), "altitud incorrecta");
        Assertions.assertEquals(SAMPLE_ID_OBSERVACION, redMeteo.getIdObservacion(), "idObservacion incorrecto");
        Assertions.assertEquals(fechaHoraMuestra, redMeteo.getFechaHora(), "fechaHora incorrecta");
        Assertions.assertEquals(SAMPLE_TEMPERATURA, redMeteo.getTemperatura(), "temperatura incorrecta");
        Assertions.assertEquals(SAMPLE_HUMEDAD, redMeteo.getHumedad(), "humedad incorrecta");
        Assertions.assertEquals(SAMPLE_VELOCIDAD_VIENTO, redMeteo.getVelocidadViento(), "velocidadViento incorrecta");
        Assertions.assertEquals(SAMPLE_DIRECCION_VIENTO, redMeteo.getDireccionViento(), "direccionViento incorrecta");
        Assertions.assertEquals(SAMPLE_RADIACION_SOLAR, redMeteo.getRadiacionSolar(), "radiacionSolar incorrecta");
        Assertions.assertEquals(SAMPLE_PRESION, redMeteo.getPresion(), "presion incorrecta");
        Assertions.assertEquals(SAMPLE_PRESION_ABSOLUTA, redMeteo.getPresionAbsoluta(), "presionAbsoluta incorrecta");
    }

    // -------------------------------------------------------------------------
    // Manejo de valores nulos (comportamiento específico)
    // -------------------------------------------------------------------------
    /**
     * Prueba que verifica que los setters permiten resetear propiedades a
     * {@code null} después de haberles asignado un valor.
     * <p>
     * Esto es importante para garantizar que el DTO pueda representar datos
     * parciales o para limpiar valores antes de una nueva asignación.
     * </p>
     */
    @Test
    @DisplayName("Setters: permiten resetear propiedades a null tras haberles asignado un valor")
    void shouldAllowResettingPropertiesToNullAfterAssignment() {
        // Arrange
        RedMeteo redMeteo = new RedMeteo();
        redMeteo.setTemperatura(SAMPLE_TEMPERATURA);
        redMeteo.setHumedad(SAMPLE_HUMEDAD);
        redMeteo.setLatitud(SAMPLE_LATITUD);

        // Act
        redMeteo.setTemperatura(null);
        redMeteo.setHumedad(null);
        redMeteo.setLatitud(null);

        // Assert
        Assertions.assertNull(redMeteo.getTemperatura(), "temperatura debe aceptar null tras asignación");
        Assertions.assertNull(redMeteo.getHumedad(), "humedad debe aceptar null tras asignación");
        Assertions.assertNull(redMeteo.getLatitud(), "latitud debe aceptar null tras asignación");
    }
}
