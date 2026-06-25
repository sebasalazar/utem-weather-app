package cl.sebastian.cm.rest.api.v1;

import cl.sebastian.cm.rest.domain.data.Pharma;
import cl.sebastian.cm.rest.domain.model.Pharmacy;
import cl.sebastian.cm.rest.exception.AuthException;
import cl.sebastian.cm.rest.exception.NoDataException;
import cl.sebastian.cm.rest.manager.PharmaManager;
import cl.sebastian.cm.rest.utils.GoogleAuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la búsqueda de farmacias de turno o en horario laboral
 * más cercanas a un punto geográfico.
 * <p>
 * Proporciona un endpoint que, dado un punto geográfico (latitud/longitud) y un
 * token de autenticación de Google, devuelve la farmacia más cercana que esté
 * en horario laboral o tenga turno en el momento de la consulta.
 * </p>
 *
 * <h2>Flujo de la operación</h2>
 * <ol>
 * <li>El cliente envía una solicitud GET con un token de autorización en el
 * encabezado {@code Authorization} y las coordenadas como variables de
 * ruta.</li>
 * <li>Se valida el token mediante {@link GoogleAuthUtils#getEmail(String)}, que
 * verifica firma, audiencia, expiración y dominio del correo.</li>
 * <li>Se obtiene la fecha y hora actual ({@link LocalDateTime#now()}) para
 * consultar farmacias en horario laboral o con turno.</li>
 * <li>Se utiliza
 * {@link PharmaManager#getNearbyOnDuty(double, double, LocalDateTime)} para
 * buscar la farmacia más cercana que cumpla las condiciones.</li>
 * <li>Si no se encuentra ninguna farmacia, se lanza una excepción
 * {@link NoDataException} que resulta en un error 404.</li>
 * <li>En caso de éxito, se devuelve la farmacia envuelta en un DTO
 * {@link Pharma} con código de estado 200.</li>
 * </ol>
 *
 * <h2>Seguridad</h2>
 * <p>
 * El endpoint requiere autenticación mediante un Bearer Token (JWT) de Google.
 * El token debe ser válido, contener un correo verificado y pertenecer al
 * dominio {@code utem.cl}. Los intentos fallidos de autenticación resultan en
 * un error 401 (No autorizado).
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see PharmaManager
 * @see GoogleAuthUtils
 * @see Pharma
 */
@RestController
@RequestMapping(value = "/v1/farmacias")
@SecurityRequirement(name = "bearerAuth")
public class PharmaRest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PharmaRest.class);

    private final PharmaManager pharmaManager;

    /**
     * Constructor que inyecta el gestor de farmacias.
     *
     * @param pharmaManager gestor de farmacias (no puede ser {@code null}).
     */
    @Autowired
    public PharmaRest(final PharmaManager pharmaManager) {
        this.pharmaManager = pharmaManager;
    }

    /**
     * Obtiene la farmacia más cercana que esté en horario laboral o tenga turno
     * en el momento actual, a partir de las coordenadas geográficas
     * proporcionadas.
     * <p>
     * El cliente debe enviar un token de autorización válido en el encabezado
     * {@code Authorization} (formato: {@code Bearer <idToken>}). El token es
     * verificado por Google y se extrae el correo del usuario. Si el correo no
     * pertenece al dominio {@code @utem.cl}, la solicitud es rechazada.
     * </p>
     * <p>
     * La búsqueda considera primero las farmacias más cercanas y, para cada
     * una, evalúa si está en horario laboral (día de semana y hora dentro del
     * rango definido) o si tiene un turno registrado para la fecha actual.
     * </p>
     *
     * @param request solicitud HTTP (no se usa directamente, pero puede ser
     * útil para futuras extensiones).
     * @param authorization encabezado de autorización con el token JWT de
     * Google (formato: {@code Bearer <token>}).
     * @param latitude latitud del punto de referencia en grados decimales
     * (rango válido: {@code [-90, 90]}).
     * @param longitude longitud del punto de referencia en grados decimales
     * (rango válido: {@code [-180, 180]}).
     * @return respuesta HTTP con el objeto {@link Pharma} que contiene la
     * farmacia encontrada más cercana.
     * @throws AuthException si el token es inválido, el correo no está
     * verificado o no pertenece al dominio UTEM.
     * @throws NoDataException si no se encuentra ninguna farmacia en horario
     * laboral o con turno dentro del radio configurado.
     */
    @Operation(
            summary = "Obtiene la farmacia más cercana en horario laboral o de turno",
            description = "Retorna la farmacia más cercana que esté en horario laboral o tenga turno "
            + "en el momento actual. Requiere autenticación con token de Google.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Farmacia encontrada",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Pharma.class))),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas o parámetros incorrectos",
                content = @Content),
        @ApiResponse(responseCode = "401", description = "Token de autenticación inválido o no autorizado",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "No hay farmacias disponibles en la zona o horario",
                content = @Content)
    })
    @GetMapping(value = {"/{latitud}/{longitud}"}, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pharma> nearby(
            HttpServletRequest request,
            @Parameter(description = "Token de autorización de Google (formato: 'Bearer <idToken>')", required = true)
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(description = "Latitud del punto de referencia en grados decimales", required = true, example = "-33.4567")
            @PathVariable("latitud") double latitude,
            @Parameter(description = "Longitud del punto de referencia en grados decimales", required = true, example = "-70.6543")
            @PathVariable("longitud") double longitude) {

        // Validación y autenticación: extrae el correo institucional del token
        final String email = GoogleAuthUtils.getEmail(authorization);
        if (!EmailValidator.getInstance().isValid(email)) {
            LOGGER.warn("Correo inválido extraído del token: {}", email);
            throw new AuthException("Correo electrónico inválido");
        }

        LOGGER.info("Consulta de farmacias del usuario {}", email);

        // Búsqueda de farmacia más cercana en horario laboral o con turno
        final LocalDateTime now = LocalDateTime.now();
        Optional<Pharmacy> opt = pharmaManager.getNearbyOnDuty(latitude, longitude, now);
        if (opt.isEmpty()) {
            LOGGER.info("No se encontraron farmacias para lat={}, lon={}, usuario={}", latitude, longitude, email);
            throw new NoDataException("No se ha encontrado una farmacia cercana en este horario");
        }

        // Construcción de la respuesta exitosa
        return ResponseEntity.ok(new Pharma(opt.get()));
    }
}
