package cl.sebastian.cm.scheduler.service;

import cl.sebastian.cm.scheduler.domain.data.RedMeteo;
import cl.sebastian.cm.scheduler.domain.model.Observation;
import cl.sebastian.cm.scheduler.domain.model.Station;
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
 * Servicio de integración con RedMeteo para consumir datos meteorológicos.
 * <p>
 * Esta clase se encarga de consultar el endpoint de RedMeteo que proporciona
 * las últimas observaciones de estaciones meteorológicas en formato JSON. Los
 * datos obtenidos se transforman en objetos {@link RedMeteo} para su posterior
 * procesamiento y persistencia (por ejemplo, en las entidades {@link Station} y
 * {@link Observation}).
 * </p>
 * <p>
 * <strong>Flujo de operación:</strong>
 * </p>
 * <ol>
 * <li><b>Consumo</b>: Obtiene los últimos datos desde el endpoint de RedMeteo
 * usando {@link RestClient}.</li>
 * <li><b>Deserialización</b>: Convierte la respuesta JSON en una lista de
 * objetos {@link RedMeteo}.</li>
 * <li><b>Manejo de errores</b>: Captura excepciones HTTP, de conectividad o
 * inesperadas, registrándolas y retornando una lista vacía.</li>
 * </ol>
 * <p>
 * <strong>Nota:</strong> Aunque el flujo menciona la persistencia de estaciones
 * y observaciones, esta clase solo se encarga de la obtención de datos; la
 * lógica de persistencia se delega en otros componentes (por ejemplo, un
 * {@code Manager} o {@code Service} específico).
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see RedMeteo
 * @see Station
 * @see Observation
 * @see RestClient
 */
@Service

public class RedMeteoService {

    /**
     * Logger de la clase para registrar eventos, advertencias y errores.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedMeteoService.class);

    /**
     * Mapper JSON para deserializar la respuesta del servicio. Se utiliza una
     * instancia estática para reutilización y eficiencia.
     */
    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    /**
     * Cliente HTTP configurado para realizar las peticiones al servicio
     * RedMeteo.
     * <p>
     * <strong>Nota:</strong> A pesar de que el constructor recibe una URL base
     * mediante {@code @Value}, actualmente no se está utilizando para
     * configurar la base URL del cliente. Esto puede ser un error de
     * implementación; sin embargo, el cliente se construye sin URL base, por lo
     * que las peticiones deben incluir la URL completa en cada llamada.
     * </p>
     */
    private final RestClient restClient;

    /**
     * Constructor que inyecta las dependencias necesarias y configura el
     * {@link RestClient} para consumir el endpoint de RedMeteo.
     * <p>
     * El cliente se configura con los siguientes encabezados por defecto:
     * </p>
     * <ul>
     * <li>{@code Accept: ALL}</li>
     * <li>{@code User-Agent: Sebastian_CL/1.0}</li>
     * <li>{@code Accept-Language: es-CL,es;q=0.9}</li>
     * </ul>
     * <p>
     * <strong>Advertencia:</strong> El parámetro {@code baseUrl} se valida pero
     * no se utiliza en la configuración del {@code RestClient}. Todas las
     * peticiones deben incluir la URL completa en el método {@code
     * uri()
     * }
     *
     * .
     * </p>
     *
     * @param builder builder de {@link RestClient} auto-configurado por Spring.
     * @param baseUrl URL base del endpoint de RedMeteo (se valida pero no se
     * usa).
     * @throws IllegalArgumentException si la URL base es nula o vacía después
     * de recortar espacios.
     */
    @Autowired
    public RedMeteoService(RestClient.Builder builder, @Value("${redmeteo.base.url}") final String baseUrl) {

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
     * Obtiene la lista de observaciones desde el endpoint de RedMeteo.
     * <p>
     * Realiza una petición GET al endpoint {@code /last-data.json} y
     * deserializa la respuesta JSON en un arreglo de objetos {@link RedMeteo}.
     * </p>
     * <p>
     * <strong>Comportamiento ante errores:</strong>
     * </p>
     * <ul>
     * <li>Si la respuesta es exitosa pero no contiene datos (cuerpo vacío o
     * arreglo vacío), retorna una lista vacía.</li>
     * <li>En caso de errores HTTP (4xx o 5xx), errores de conectividad o
     * timeouts, se registra el error y se retorna una lista vacía.</li>
     * <li>No se lanza {@link SchException}; en su lugar, se maneja internamente
     * retornando lista vacía.</li>
     * </ul>
     *
     * @return lista de observaciones de RedMeteo; nunca {@code null}, puede
     * estar vacía si no hay datos o si ocurre un error.
     * @see RedMeteo
     */
    public List<RedMeteo> obtenerDatosRedMeteo() {
        final String path = "/last-data.json";
        LOGGER.info("Consultando datos de RedMeteo en: {}", path);

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

            final RedMeteo[] datos = MAPPER.readValue(
                    json, new TypeReference<RedMeteo[]>() {
            });

            if (ArrayUtils.isEmpty(datos)) {
                LOGGER.warn("No se encontraron datos de RedMeteo disponibles");
                return List.of();
            }

            final List<RedMeteo> resultado = Arrays.asList(datos);
            LOGGER.info("Se obtuvieron {} observaciones de RedMeteo", resultado.size());
            return resultado;
        } catch (final HttpClientErrorException e) {
            LOGGER.error("Error del cliente HTTP al consultar RedMeteo. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            return List.of();
        } catch (final HttpServerErrorException e) {
            LOGGER.error("Error del servidor HTTP al consultar RedMeteo. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            return List.of();
        } catch (final ResourceAccessException e) {
            LOGGER.error("Error de conectividad o timeout al consultar RedMeteo", e);
            return List.of();
        } catch (final Exception e) {
            LOGGER.error("Error inesperado al consultar RedMeteo", e);
            return List.of();
        }
    }
}
