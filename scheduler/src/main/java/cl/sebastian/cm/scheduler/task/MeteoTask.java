package cl.sebastian.cm.scheduler.task;

import cl.sebastian.cm.scheduler.domain.data.RedMeteo;
import cl.sebastian.cm.scheduler.manager.MeteoManager;
import cl.sebastian.cm.scheduler.service.RedMeteoService;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarea programada para refrescar la información meteorológica desde RedMeteo.
 * <p>
 * Esta clase se ejecuta de forma periódica según la configuración definida en
 * la propiedad {@code scheduler.meteo.task}. Obtiene la lista de observaciones
 * meteorológicas a través de {@link RedMeteoService} y las persiste utilizando
 * {@link MeteoManager}.
 * </p>
 * <p>
 * Cada registro se procesa de manera individual, capturando y registrando
 * cualquier excepción para evitar que un error en un elemento afecte al
 * procesamiento del resto.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see MeteoManager
 * @see RedMeteoService
 * @see Scheduled
 */
@Component
public class MeteoTask {

    /**
     * Logger de la clase para registrar eventos, advertencias y errores.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MeteoTask.class);

    /**
     * Gestor de datos meteorológicos encargado de la lógica de persistencia de
     * estaciones y observaciones.
     */
    private final MeteoManager meteoManager;

    /**
     * Servicio externo que proporciona los datos meteorológicos desde RedMeteo.
     */
    private final RedMeteoService redMeteoService;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param meteoManager gestor de meteorología.
     * @param redMeteoService servicio de consulta de datos RedMeteo.
     */
    @Autowired
    public MeteoTask(MeteoManager meteoManager, RedMeteoService redMeteoService) {
        this.meteoManager = meteoManager;
        this.redMeteoService = redMeteoService;
    }

    /**
     * Procesa un único registro de observación meteorológica, persistiéndolo
     * mediante {@link MeteoManager#saveObs(RedMeteo)}.
     * <p>
     * Si ocurre alguna excepción durante el guardado, se registra el error pero
     * no se propaga, permitiendo que el procesamiento continúe con los
     * siguientes registros.
     * </p>
     *
     * @param meteo el objeto {@link RedMeteo} a procesar (puede ser
     * {@code null}, en cuyo caso la operación se omite silenciosamente).
     */
    private void process(final RedMeteo meteo) {
        if (meteo == null) {
            return;
        }

        try {
            meteoManager.saveObs(meteo);
        } catch (Exception e) {
            LOGGER.error("Error al procesar observación meteorológica: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * Ejecuta la tarea de refresco de datos meteorológicos.
     * <p>
     * Este método está anotado con {@link Scheduled} y se invoca
     * automáticamente según el intervalo definido en la propiedad
     * {@code scheduler.meteo.task}.
     * </p>
     * <p>
     * El flujo de trabajo es el siguiente:
     * </p>
     * <ol>
     * <li>Obtiene la lista de observaciones desde {@link RedMeteoService}.</li>
     * <li>Si la lista está vacía o es nula, registra un error y finaliza.</li>
     * <li>Para cada elemento de la lista, invoca
     * {@link #process(RedMeteo)}.</li>
     * </ol>
     * <p>
     * El procesamiento es tolerante a fallos: un error en una observación no
     * detiene el procesamiento de las demás.
     * </p>
     */
    @Scheduled(fixedDelayString = "${scheduler.meteo.task}")
    public void schedulerRun() {
        List<RedMeteo> datos = redMeteoService.obtenerDatosRedMeteo();
        if (CollectionUtils.isEmpty(datos)) {
            LOGGER.error("No hay información meteorológica");
            return;
        }

        for (RedMeteo meteo : datos) {
            process(meteo);
        }
    }
}
