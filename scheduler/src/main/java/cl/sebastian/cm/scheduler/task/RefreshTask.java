package cl.sebastian.cm.scheduler.task;

import cl.sebastian.cm.scheduler.domain.data.FarmaciaTurno;
import cl.sebastian.cm.scheduler.manager.PharmaManager;
import cl.sebastian.cm.scheduler.service.MinsalService;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarea programada para refrescar la información de farmacias en turno.
 * <p>
 * Esta clase se ejecuta de forma periódica según la configuración definida en
 * la propiedad {@code scheduler.hour.task}. Obtiene la lista de farmacias de
 * turno a través de {@link MinsalService} y las persiste utilizando
 * {@link PharmaManager}.
 * </p>
 * <p>
 * Cada registro se procesa de manera individual, capturando y registrando
 * cualquier excepción para evitar que un error en un elemento afecte al
 * procesamiento del resto.
 * </p>
 *
 * @author Sebastián Salazar
 * @version 1.0
 * @see PharmaManager
 * @see MinsalService
 * @see Scheduled
 */
@Component
public class RefreshTask {

    /**
     * Logger de la clase para registrar eventos, advertencias y errores.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTask.class);

    /**
     * Gestor de farmacias encargado de la lógica de persistencia.
     */
    private final PharmaManager pharmaManager;

    /**
     * Servicio externo que proporciona los datos de farmacias en turno.
     */
    private final MinsalService minsalService;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param pharmaManager gestor de farmacias.
     * @param minsalService servicio de consulta de turnos.
     */
    @Autowired
    public RefreshTask(final PharmaManager pharmaManager,
            final MinsalService minsalService) {
        this.pharmaManager = pharmaManager;
        this.minsalService = minsalService;
    }

    /**
     * Procesa un único registro de farmacia de turno, persistiéndolo mediante
     * {@link PharmaManager#save(FarmaciaTurno)}.
     * <p>
     * Si ocurre alguna excepción durante el guardado, se registra el error pero
     * no se propaga, permitiendo que el procesamiento continúe con los
     * siguientes registros.
     * </p>
     *
     * @param ft el objeto {@link FarmaciaTurno} a procesar (puede ser
     * {@code null}, en cuyo caso la operación se omite silenciosamente).
     */
    private void process(final FarmaciaTurno ft) {
        if (ft == null) {
            return;
        }

        try {
            pharmaManager.save(ft);
        } catch (Exception e) {
            LOGGER.error("Error al procesar farmacia de turno: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * Ejecuta la tarea de refresco de farmacias de turno.
     * <p>
     * Este método está anotado con {@link Scheduled} y se invoca
     * automáticamente según el intervalo definido en la propiedad
     * {@code scheduler.hour.task}.
     * </p>
     * <p>
     * El flujo de trabajo es el siguiente:
     * </p>
     * <ol>
     * <li>Obtiene la lista de farmacias de turno desde
     * {@link MinsalService}.</li>
     * <li>Si la lista está vacía o es nula, registra un error y finaliza.</li>
     * <li>Para cada elemento de la lista, invoca
     * {@link #process(FarmaciaTurno)}.</li>
     * </ol>
     * <p>
     * El procesamiento es tolerante a fallos: un error en una farmacia no
     * detiene el procesamiento de las demás.
     * </p>
     */
    @Scheduled(fixedDelayString = "${scheduler.hour.task}")
    public void schedulerRun() {
        List<FarmaciaTurno> farmacias = minsalService.obtenerFarmaciasTurno();
        if (CollectionUtils.isEmpty(farmacias)) {
            LOGGER.error("No hay información de los turnos de la farmacia");
            return;
        }

        for (FarmaciaTurno farmacia : farmacias) {
            process(farmacia);
        }
    }
}
