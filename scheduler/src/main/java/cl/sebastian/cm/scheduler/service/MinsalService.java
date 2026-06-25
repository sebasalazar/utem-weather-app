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
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Servicio encargado de consultar el endpoint del Ministerio de Salud (MINSAL)
 * para obtener la lista de farmacias de turno vigentes.
 * <p>
 * Utiliza un {@link RestClient} configurado para realizar peticiones HTTP al
 * endpoint definido en las propiedades de la aplicación. Gestiona los errores
 * de red, timeouts y respuestas del servidor, envolviéndolos en una excepción
 * propia ({@link SchException}) para un manejo uniforme en capas superiores.
 * </p>
 * <p>
 * La configuración del cliente incluye encabezados por defecto (Accept,
 * User-Agent, Accept-Language) para garantizar una comunicación adecuada con el
 * servicio externo.
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see FarmaciaTurno
 * @see SchException
 * @see RestClient
 */
@Service

public class MinsalService {

    /**
     * Logger de la clase para registrar eventos, advertencias y errores.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MinsalService.class);

    /**
     * Mapper JSON para deserializar la respuesta del servicio. Se utiliza una
     * instancia estática para reutilización.
     */
    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    /**
     * Cliente HTTP configurado para realizar las peticiones al servicio MINSAL.
     */
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
     * @param builder builder de {@link RestClient} auto-configurado por Spring.
     * @param baseUrl URL base del endpoint de farmacias (ej.
     * {@code https://minsal.cl/api}).
     * @throws IllegalArgumentException si la URL base es nula o vacía después
     * de recortar espacios.
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
                .defaultHeader("Accept", MediaType.ALL_VALUE)
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
     * </p>
     * <ul>
     * <li>Si la respuesta es exitosa pero no contiene datos (cuerpo vacío o
     * arreglo vacío), retorna una lista vacía.</li>
     * <li>En caso de errores HTTP (4xx o 5xx), errores de conectividad o
     * timeouts, se registra el error y se retorna una lista vacía.</li>
     * <li>No se lanza {@link SchException} en esta versión; en su lugar, se
     * maneja internamente retornando lista vacía.</li>
     * </ul>
     * <p>
     * <strong>Nota:</strong> Aunque el Javadoc original menciona
     * {@link SchException}, la implementación actual no la lanza; se retorna
     * {@code List.of()} en todos los casos de error. Este comportamiento está
     * documentado para claridad.
     * </p>
     *
     * @return lista de farmacias de turno; nunca {@code null}, puede estar
     * vacía si no hay datos o si ocurre un error.
     * @see FarmaciaTurno
     */
    public List<FarmaciaTurno> obtenerFarmaciasTurno() {
        final String path = "/getLocalesTurnos.php";
        LOGGER.info("Consultando farmacias de turno en: {}", path);

        try {
            final String json = restClient.get()
                    .uri(path)
                    .retrieve()
                    .body(String.class);

            if (StringUtils.isBlank(json)) {
                LOGGER.warn("Respuesta vacía del servicio MINSAL");
                return List.of();
            }

            LOGGER.debug("Respuesta recibida (primeros 128 chars): {}",
                    StringUtils.abbreviate(json, 128));

            final FarmaciaTurno[] farmacias = MAPPER.readValue(
                    json, new TypeReference<FarmaciaTurno[]>() {
            });

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
