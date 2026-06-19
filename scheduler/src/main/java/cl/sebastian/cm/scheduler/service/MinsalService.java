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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio encargado de consultar el endpoint del Ministerio de Salud (MINSAL)
 * para obtener la lista de farmacias de turno vigentes.
 * <p>
 * Utiliza un {@link RestTemplate} configurado para realizar peticiones HTTP al
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

    private final RestTemplate restTemplate;
    private final String baseUrl;

    /**
     * Constructor que inyecta el {@link RestTemplate} y la URL base del
     * servicio MINSAL desde el archivo de propiedades.
     *
     * @param restTemplate cliente HTTP configurado (con timeouts,
     * interceptores, etc.)
     * @param baseUrl URL base del endpoint de farmacias (ej.
     * {@code https://minsal.cl/api})
     * @throws IllegalArgumentException si la URL base es nula o vacía después
     * de recortar espacios
     */
    @Autowired
    public MinsalService(RestTemplate restTemplate,
            @Value("${minsal.farmacias.base.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = StringUtils.trimToEmpty(baseUrl);
        // Validación temprana para evitar errores en tiempo de ejecución
        if (this.baseUrl.isEmpty()) {
            throw new IllegalArgumentException("La URL base de MINSAL no puede estar vacía");
        }
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
        String endpoint = baseUrl + "/getLocalesTurnos.php";

        LOGGER.info("Consultando farmacias de turno en: {}", endpoint);

        try {
            HttpHeaders headers = crearCabeceras();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<FarmaciaTurno[]> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    entity,
                    FarmaciaTurno[].class
            );

            FarmaciaTurno[] farmacias = response.getBody();
            if (ArrayUtils.isEmpty(farmacias)) {
                LOGGER.warn("No se encontraron farmacias de turno disponibles");
                return List.of();
            }

            List<FarmaciaTurno> resultado = Arrays.asList(farmacias);
            LOGGER.info("Se obtuvieron {} farmacias de turno", resultado.size());

            return resultado;
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error del cliente HTTP al consultar farmacias de turno. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new SchException("Error en la solicitud al MINSAL (cliente)", e);
        } catch (HttpServerErrorException e) {
            LOGGER.error("Error del servidor HTTP al consultar farmacias de turno. Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new SchException("Error interno del servidor MINSAL", e);
        } catch (ResourceAccessException e) {
            LOGGER.error("Error de conectividad o timeout al consultar farmacias de turno", e);
            throw new SchException("No se pudo conectar con el servicio MINSAL (timeout o red)", e);
        } catch (Exception e) {
            // Error inesperado (ej. deserialización, NullPointer, etc.)
            LOGGER.error("Error inesperado al consultar farmacias de turno", e);
            throw new SchException("Error inesperado en la consulta al MINSAL", e);
        }
    }

    /**
     * Construye los encabezados HTTP necesarios para la petición al servicio
     * MINSAL.
     * <p>
     * Se incluyen:
     * <ul>
     * <li>{@code Accept: application/json} para indicar que se espera
     * JSON.</li>
     * <li>{@code User-Agent} personalizado para identificar la aplicación.</li>
     * <li>{@code Accept-Language: es-CL,es} para preferir contenido en español
     * chileno.</li>
     * </ul>
     * </p>
     *
     * @return instancia de {@link HttpHeaders} con los valores configurados.
     */
    private HttpHeaders crearCabeceras() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "Sebastian_CL/1.0");
        headers.set("Accept-Language", "es-CL,es;q=0.9");
        return headers;
    }
}
