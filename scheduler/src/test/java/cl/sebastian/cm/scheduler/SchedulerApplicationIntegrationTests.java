package cl.sebastian.cm.scheduler;

import cl.sebastian.cm.scheduler.manager.MeteoManager;
import cl.sebastian.cm.scheduler.manager.PharmaManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

/**
 * Pruebas de integración (smoke tests) para la aplicación
 * {@link SchedulerApplication}.
 * <p>
 * Verifica que el contexto de Spring se carga correctamente con todos los beans
 * requeridos y que los beans críticos ({@link PharmaManager} y
 * {@link MeteoManager}) están disponibles para inyección. Además, valida que la
 * configuración de pruebas deshabilita la ejecución de tareas programadas para
 * evitar efectos secundarios durante la ejecución de los tests.
 * </p>
 * <p>
 * Estas pruebas son de tipo "smoke" porque no ejercitan la lógica de negocio,
 * sino que confirman que el entorno de ejecución está correctamente
 * configurado.
 * </p>
 * <p>
 * <strong>Configuración de pruebas:</strong>
 * Se utiliza {@code @TestPropertySource} para establecer la propiedad
 * {@code spring.task.scheduling.enabled=false}, lo que previene que las tareas
 * programadas se ejecuten automáticamente durante la suite de pruebas.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see SchedulerApplication
 * @see PharmaManager
 * @see MeteoManager
 */
@DisplayName("SchedulerApplication - Pruebas de integración (smoke tests)")
@SpringBootTest
@TestPropertySource(properties = {"spring.task.scheduling.enabled=false"})
class SchedulerApplicationIntegrationTests {

    // -------------------------------------------------------------------------
    // Constantes de prueba (elimina strings mágicos dispersos)
    // -------------------------------------------------------------------------
    /**
     * Nombre esperado del bean {@link PharmaManager} en el contexto de Spring.
     */
    private static final String BEAN_NAME_PHARMA_MANAGER = "pharmaManager";

    /**
     * Nombre esperado del bean {@link MeteoManager} en el contexto de Spring.
     */
    private static final String BEAN_NAME_METEO_MANAGER = "meteoManager";

    /**
     * Propiedad que controla la habilitación del scheduling.
     */
    private static final String SCHEDULING_ENABLED_PROPERTY = "spring.task.scheduling.enabled";

    /**
     * Número mínimo de beans esperados en el contexto para considerar que la
     * aplicación arrancó correctamente.
     */
    private static final int MINIMUM_EXPECTED_BEAN_COUNT = 10;

    // -------------------------------------------------------------------------
    // Inyección de dependencias
    // -------------------------------------------------------------------------
    /**
     * Contexto de la aplicación, usado para verificar la presencia de beans.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Bean crítico para la gestión de farmacias.
     */
    @Autowired
    private PharmaManager pharmaManager;

    /**
     * Bean crítico para la gestión de datos meteorológicos.
     */
    @Autowired
    private MeteoManager meteoManager;

    /**
     * Entorno de Spring, usado para leer propiedades de configuración.
     */
    @Autowired
    private Environment environment;

    // -------------------------------------------------------------------------
    // Smoke Tests: Validación de arranque de la aplicación
    // -------------------------------------------------------------------------
    /**
     * Verifica que el contexto de Spring se carga exitosamente y que contiene
     * al menos un número mínimo de beans (10), lo que indica que la aplicación
     * arrancó correctamente.
     */
    @Test
    @DisplayName("Contexto Spring: carga exitosamente con todos los beans requeridos")
    void shouldLoadApplicationContextWithAllRequiredBeans() {
        // Arrange
        // (No requiere configuración; el contexto ya está cargado por @SpringBootTest)

        // Act
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        int totalBeanCount = allBeanNames.length;

        // Assert
        Assertions.assertNotNull(applicationContext,
                "El ApplicationContext no debe ser null tras el arranque de Spring");
        Assertions.assertTrue(totalBeanCount >= MINIMUM_EXPECTED_BEAN_COUNT,
                String.format("Se esperaban al menos %d beans registrados, pero se encontraron %d",
                        MINIMUM_EXPECTED_BEAN_COUNT, totalBeanCount));
    }

    /**
     * Verifica que los beans críticos {@link PharmaManager} y
     * {@link MeteoManager} pueden ser inyectados correctamente, lo que confirma
     * que están registrados y disponibles en el contexto.
     */
    @Test
    @DisplayName("Beans críticos: PharmaManager y MeteoManager están disponibles para inyección")
    void shouldInjectCriticalManagerBeansSuccessfully() {
        // Arrange
        // (No requiere configuración; los beans se inyectan automáticamente)

        // Act & Assert
        Assertions.assertNotNull(pharmaManager,
                "PharmaManager debe estar disponible como bean inyectable");
        Assertions.assertNotNull(meteoManager,
                "MeteoManager debe estar disponible como bean inyectable");
    }

    /**
     * Verifica que los beans críticos están registrados con los nombres
     * esperados en el contexto de Spring, utilizando
     * {@link ApplicationContext#containsBean(String)}.
     */
    @Test
    @DisplayName("Beans críticos: están registrados con los nombres esperados en el contexto")
    void shouldRegisterCriticalManagerBeansWithExpectedNames() {
        // Arrange
        // (No requiere configuración; el contexto ya está cargado)

        // Act & Assert
        Assertions.assertTrue(applicationContext.containsBean(BEAN_NAME_PHARMA_MANAGER),
                String.format("El bean '%s' debe estar registrado en el contexto", BEAN_NAME_PHARMA_MANAGER));
        Assertions.assertTrue(applicationContext.containsBean(BEAN_NAME_METEO_MANAGER),
                String.format("El bean '%s' debe estar registrado en el contexto", BEAN_NAME_METEO_MANAGER));
    }

    // -------------------------------------------------------------------------
    // Validación de configuración de testing
    // -------------------------------------------------------------------------
    /**
     * Verifica que la propiedad {@code spring.task.scheduling.enabled} está
     * configurada como {@code "false"} en el perfil de pruebas, evitando así la
     * ejecución automática de tareas programadas que podrían interferir con los
     * tests.
     */
    @Test
    @DisplayName("Configuración de tests: scheduling está deshabilitado para evitar ejecución de tareas programadas")
    void shouldDisableSchedulingInTestProfile() {
        // Arrange
        // (No requiere configuración; la propiedad se establece vía @TestPropertySource)

        // Act
        String schedulingEnabledValue = environment.getProperty(SCHEDULING_ENABLED_PROPERTY);

        // Assert
        Assertions.assertEquals("false", schedulingEnabledValue,
                "La propiedad 'spring.task.scheduling.enabled' debe ser 'false' en el perfil de tests "
                + "para evitar que las tareas programadas se ejecuten durante la ejecución de tests");
    }
}
