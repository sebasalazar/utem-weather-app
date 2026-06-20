package cl.sebastian.cm.rest.api.v1;

import cl.sebastian.cm.rest.domain.data.MeteoObs;
import cl.sebastian.cm.rest.domain.model.Observation;
import cl.sebastian.cm.rest.exception.AuthException;
import cl.sebastian.cm.rest.exception.NoDataException;
import cl.sebastian.cm.rest.manager.MeteoManager;
import cl.sebastian.cm.rest.utils.GoogleAuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Optional;

/**
 * Controlador REST para consultas meteorológicas con autenticación mediante ID
 * Token de Google.
 * <p>
 * Proporciona un endpoint para obtener la observación meteorológica más
 * reciente dentro de un radio de búsqueda configurado, a partir de coordenadas
 * geográficas proporcionadas por el cliente. La autenticación se realiza
 * mediante un token JWT de Google, del cual se extrae el correo institucional
 * del usuario para validar que pertenezca al dominio {@code @utem.cl}.
 * </p>
 *
 * <h2>Flujo de la operación</h2>
 * <ol>
 * <li>El cliente envía una solicitud GET con un token de autorización en el
 * encabezado {@code Authorization} y las coordenadas {@code latitude} y
 * {@code longitude} como variables de ruta.</li>
 * <li>Se valida el token mediante {@link GoogleAuthUtils#getEmail(String)}, que
 * verifica firma, audiencia, expiración y dominio del correo.</li>
 * <li>Se utiliza {@link MeteoManager#getLastNearby(double, double)} para
 * obtener la observación más reciente dentro del radio configurado.</li>
 * <li>Si no se encuentra ninguna observación, se lanza una excepción
 * {@link NoDataException} que resulta en un error 404.</li>
 * <li>En caso de éxito, se devuelve la observación envuelta en un DTO
 * {@link MeteoObs} con código de estado 200.</li>
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
 * @see MeteoManager
 * @see GoogleAuthUtils
 */
@RestController
@RequestMapping(value = "/v1/weather")
@SecurityRequirement(name = "bearerAuth")
public class WeatherRest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherRest.class);

    private final MeteoManager meteoManager;

    /**
     * Constructor que inyecta el gestor de meteorología.
     *
     * @param meteoManager gestor de consultas meteorológicas (no puede ser
     * {@code null}).
     */
    @Autowired
    public WeatherRest(MeteoManager meteoManager) {
        this.meteoManager = meteoManager;
    }

    /**
     * Obtiene la observación meteorológica más reciente dentro del radio de
     * búsqueda configurado, a partir de las coordenadas geográficas
     * proporcionadas.
     * <p>
     * El cliente debe enviar un token de autorización válido en el encabezado
     * {@code Authorization} (formato: {@code Bearer <idToken>}). El token es
     * verificado por Google y se extrae el correo del usuario. Si el correo no
     * pertenece al dominio {@code @utem.cl}, la solicitud es rechazada.
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
     * @return respuesta HTTP con el objeto {@link MeteoObs} que contiene la
     * observación más reciente encontrada.
     * @throws AuthException si el token es inválido, el correo no está
     * verificado o no pertenece al dominio UTEM.
     * @throws NoDataException si no se encuentra ninguna observación dentro del
     * radio de búsqueda configurado.
     */
    @Operation(
            summary = "Obtiene la observación meteorológica más reciente cercana a un punto geográfico",
            description = "Retorna la observación más reciente dentro del radio de búsqueda configurado "
            + "alrededor de las coordenadas proporcionadas. Requiere autenticación con token de Google.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Observación encontrada",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = MeteoObs.class))),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas o parámetros incorrectos",
                content = @Content),
        @ApiResponse(responseCode = "401", description = "Token de autenticación inválido o no autorizado",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "No hay observaciones en el radio especificado",
                content = @Content)
    })
    @GetMapping(value = {"/{latitude}/{longitude}"}, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MeteoObs> nearby(
            HttpServletRequest request,
            @Parameter(description = "Token de autorización de Google (formato: 'Bearer <idToken>')", required = true)
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(description = "Latitud del punto de referencia en grados decimales", required = true, example = "-33.4567")
            @PathVariable("latitude") double latitude,
            @Parameter(description = "Longitud del punto de referencia en grados decimales", required = true, example = "-70.6543")
            @PathVariable("longitude") double longitude) {

        // Validación y autenticación: extrae el correo institucional del token
        final String email = GoogleAuthUtils.getEmail(authorization);
        if (!EmailValidator.getInstance().isValid(email)) {
            LOGGER.warn("Correo inválido extraído del token: {}", email);
            throw new AuthException("Correo electrónico inválido");
        }

        LOGGER.info("Consulta de clima del usuario {}", email);

        // Búsqueda de la observación más reciente dentro del radio
        Optional<Observation> opt = meteoManager.getLastNearby(latitude, longitude);
        if (opt.isEmpty()) {
            LOGGER.info("No se encontraron observaciones para lat={}, lon={}, usuario={}", latitude, longitude, email);
            throw new NoDataException("No se han encontrado datos dentro del rango especificado");
        }

        // Construcción de la respuesta exitosa
        return ResponseEntity.ok(new MeteoObs(opt.get()));
    }
}
