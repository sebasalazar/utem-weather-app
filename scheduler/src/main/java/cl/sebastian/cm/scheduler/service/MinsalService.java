package cl.sebastian.cm.scheduler.service;

import cl.sebastian.cm.scheduler.domain.data.FarmaciaTurno;
import cl.sebastian.cm.scheduler.exception.SchException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * Servicio encargado de consultar el endpoint del Ministerio de Salud (MINSAL)
 * para obtener la lista de farmacias de turno vigentes.
 * <p>
 * Utiliza un {@link RestClient} configurado para realizar peticiones HTTP al
 * endpoint definido en las propiedades de la aplicación. Gestiona los errores
 * de red, timeouts y respuestas del servidor, envolviéndolos en una excepción
 * propia ({@link SchException}) para un manejo uniforme en capas superiores.
 * </p>
 *
 * @author Sebastián
 * @since 1.0
 * @see FarmaciaTurno
 * @see SchException
 */
@Service
public class MinsalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinsalService.class);

    private final RestClient restClient;

    /**
     * Constructor que inyecta el {@link RestClient.Builder} y la URL base del
     * servicio MINSAL desde el archivo de propiedades.
     * <p>
     * El {@code RestClient} se construye con la URL base y los encabezados por
     * defecto configurados en este mismo constructor, evitando así recrearlos
     * en cada llamada.
     * </p>
     *
     * @param builder builder de {@link RestClient} auto-configurado por Spring
     * @param baseUrl URL base del endpoint de farmacias (ej.
     * {@code https://minsal.cl/api})
     * @throws IllegalArgumentException si la URL base es nula o vacía después
     * de recortar espacios
     */
    @Autowired
    public MinsalService(RestClient.Builder builder,
            @Value("${minsal.farmacias.base.url}") String baseUrl) {

        final String url = StringUtils.trimToEmpty(baseUrl);
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("La URL base de MINSAL no puede estar vacía");
        }

        this.restClient = builder
                .baseUrl(url)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("User-Agent", "Sebastian_CL/1.0")
                .defaultHeader("Accept-Language", "es-CL,es;q=0.9")
                .build();
    }

    /**
     * Obtiene la lista de farmacias de turno para la fecha actual desde el
     * servicio MINSAL.
     * <p>
     * Realiza una petición GET al endpoint {@code /getLocalesTurnos.php} y
     * deserializa la respuesta JSON en un arreglo de objetos
     * {@link FarmaciaTurno}.
     * </p>
     * <p>
     * <strong>Comportamiento ante errores:</strong>
     * <ul>
     * <li>Si la respuesta es exitosa pero no contiene datos, retorna una lista
     * vacía.</li>
     * <li>En caso de errores HTTP (4xx o 5xx), errores de conectividad o
     * timeouts, se registra el error y se lanza una {@link SchException} que
     * envuelve la causa.</li>
     * </ul>
     * </p>
     *
     * @return lista de farmacias de turno; nunca {@code null}, puede estar
     * vacía si no hay datos.
     * @throws SchException si ocurre cualquier error durante la comunicación
     * con el servicio (incluye errores HTTP, problemas de red o errores
     * inesperados).
     * @see FarmaciaTurno
     */
    public List<FarmaciaTurno> obtenerFarmaciasTurno() {
        final String path = "/getLocalesTurnos.php";
        LOGGER.info("Consultando farmacias de turno en: {}{}", restClient, path);

        try {
            FarmaciaTurno[] farmacias = restClient.get()
                    .uri(path)
                    .retrieve()
                    .body(FarmaciaTurno[].class);

            if (ArrayUtils.isEmpty(farmacias)) {
                LOGGER.warn("No se encontraron farmacias de turno disponibles");
                return List.of();
            }

            List<FarmaciaTurno> resultado = Arrays.asList(farmacias);
            LOGGER.info("Se obtuvieron {} farmacias de turno", resultado.size());
            return resultado;
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error del cliente HTTP al consultar farmacias de turno. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            LOGGER.error("Error del servidor HTTP al consultar farmacias de turno. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            LOGGER.error("Error de conectividad o timeout al consultar farmacias de turno", e);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al consultar farmacias de turno", e);
        }
        return List.of();
    }
}
