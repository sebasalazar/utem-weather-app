package cl.sebastian.cm.rest.api;

import cl.sebastian.cm.rest.exception.AuthException;
import cl.sebastian.cm.rest.exception.ForbiddenException;
import cl.sebastian.cm.rest.exception.LimitException;
import cl.sebastian.cm.rest.exception.NoDataException;
import cl.sebastian.cm.rest.exception.UtemException;
import cl.sebastian.cm.rest.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Manejador global centralizado de excepciones para la API REST.
 *
 * <p>
 * Proporciona un punto único de transformación de excepciones (técnicas y de
 * dominio) en respuestas HTTP consistentes conforme al estándar RFC 9457
 * ({@link ProblemDetail}). Esto elimina la duplicación de lógica de error en
 * controladores y garantiza una estructura uniforme para clientes, frontend y
 * herramientas de monitoreo.
 * </p>
 *
 * <p>
 * <b>Estructura de handlers (por especificidad descendente):</b>
 * <ol>
 * <li><strong>Excepciones de dominio:</strong> {@link AuthException},
 *       {@link NoDataException}, {@link ValidationException}, {@link LimitException},
 *       {@link TenantException}, {@link UtemException}</li>
 * <li><strong>Bean Validation:</strong> {@link MethodArgumentNotValidException},
 *       {@link ConstraintViolationException}</li>
 * <li><strong>HTTP Parsing/Binding:</strong> {@link HttpMessageNotReadableException},
 *       {@link MethodArgumentTypeMismatchException}, {@link MissingServletRequestParameterException}</li>
 * <li><strong>Negociación HTTP:</strong> {@link HttpMediaTypeNotSupportedException},
 *       {@link HttpRequestMethodNotSupportedException}</li>
 * <li><strong>Fallback genérico:</strong> Cualquier otra {@link Exception}</li>
 * </ol>
 * </p>
 *
 * <p>
 * <b>Respuestas:</b> Todas las respuestas de error incluyen:
 * <ul>
 * <li>Status HTTP apropiado (200-599)</li>
 * <li>Tipo (referencia a documentación MDN)</li>
 * <li>Título (reason phrase del status)</li>
 * <li>Detalle (mensaje descriptivo)</li>
 * <li>Instancia (URI de la solicitud)</li>
 * <li>Propiedades personalizadas: timestamp, errorCode, method, query (si
 * presente)</li>
 * </ul>
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Clave usada en los mapas de error de validación para el mensaje
     * descriptivo.
     */
    private static final String KEY_MESSAGE = "message";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * Códigos internos para clasificar el tipo de error en la respuesta
     * {@link ProblemDetail}.
     *
     * <p>
     * Estos códigos son consumidos por el frontend y por herramientas de
     * monitoreo para diferenciar el origen del problema sin depender solo del
     * HTTP status.
     */
    public enum ErrorCode {

        /**
         * Sin autorización: credenciales ausentes, inválidas o token expirado.
         */
        SA,
        /**
         * Sin datos: el recurso solicitado no existe en el sistema.
         */
        SD,
        /**
         * Validación fallida: regla de negocio o constraint personalizado no
         * cumplido.
         */
        VF,
        /**
         * MethodArgumentInvalid: fallo en {@code @Valid} sobre un DTO.
         */
        MI,
        /**
         * Mala petición genérica: parámetro ausente, tipo incorrecto o cuerpo
         * malformado.
         */
        MP,
        /**
         * Media Type no soportado: {@code Content-Type} incompatible con el
         * endpoint.
         */
        MT,
        /**
         * Método HTTP no soportado: el verbo usado no está habilitado en el
         * endpoint.
         */
        MS,
        /**
         * Desconocido: excepción no contemplada; requiere revisión en los logs.
         */
        DC
    }

    // -------------------------------------------------------------------------
    // Métodos privados de construcción
    // -------------------------------------------------------------------------
    /**
     * Construye el cuerpo {@link ProblemDetail} común a todas las respuestas de
     * error.
     *
     * <p>
     * Enriquece el objeto con metadatos de diagnóstico: timestamp, código
     * interno, URI de la petición, método HTTP y query string cuando está
     * presente.
     *
     * @param request petición HTTP original; puede ser {@code null} en
     * contextos no web.
     * @param status código HTTP que se asignará a la respuesta.
     * @param detail mensaje descriptivo del error, visible para el consumidor
     * de la API.
     * @param errorCode clasificación interna del error según {@link ErrorCode}.
     * @return {@link ProblemDetail} enriquecido y listo para incluir en la
     * respuesta.
     */
    private ProblemDetail makeProblemDetail(
            HttpServletRequest request,
            HttpStatus status,
            String detail,
            ErrorCode errorCode) {

        URI type = URI.create(
                "https://developer.mozilla.org/es/docs/Web/HTTP/Reference/Status/" + status.value());

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, StringUtils.trimToEmpty(detail));
        pd.setType(type);
        pd.setTitle(status.getReasonPhrase());
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("errorCode", errorCode.name());

        if (request != null) {
            if (StringUtils.isNotBlank(request.getRequestURI())) {
                pd.setInstance(URI.create(request.getRequestURI()));
            }
            pd.setProperty("method", request.getMethod());
            String queryString = request.getQueryString();
            if (StringUtils.isNotBlank(queryString)) {
                pd.setProperty("query", queryString);
            }
        }

        return pd;
    }

    /**
     * Registra el error en el log y construye la respuesta HTTP con
     * {@link ProblemDetail}.
     *
     * <p>
     * El nivel {@code ERROR} registra solo el mensaje corto para no saturar los
     * logs en producción. El nivel {@code DEBUG} incluye el stack trace
     * completo para diagnóstico en ambientes de desarrollo.
     *
     * @param request petición HTTP original.
     * @param status código HTTP de la respuesta.
     * @param exception excepción capturada.
     * @param logMessage prefijo descriptivo para identificar el handler en el
     * log.
     * @param errorCode clasificación interna del error según {@link ErrorCode}.
     * @return {@link ResponseEntity} con el {@link ProblemDetail} serializado.
     */
    private ResponseEntity<ProblemDetail> buildErrorResponse(
            HttpServletRequest request,
            HttpStatus status,
            Exception exception,
            String logMessage,
            ErrorCode errorCode) {

        String shortMessage = StringUtils.defaultIfBlank(
                exception.getLocalizedMessage(),
                exception.getClass().getSimpleName());

        LOGGER.error("{}: {}", logMessage, shortMessage);
        LOGGER.debug("{}: {}", logMessage, exception.getMessage(), exception);

        ProblemDetail body = makeProblemDetail(request, status, shortMessage, errorCode);
        return new ResponseEntity<>(body, status);
    }

    // -------------------------------------------------------------------------
    // Excepciones de dominio personalizadas
    // -------------------------------------------------------------------------
    /**
     * Maneja fallos de autenticación: credenciales inválidas o token
     * ausente/expirado.
     *
     * @param request petición HTTP original.
     * @param e excepción de autenticación capturada.
     * @return {@link ResponseEntity} con status {@code 401 Unauthorized}.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ProblemDetail> handleAuthException(
            HttpServletRequest request, AuthException e) {
        return buildErrorResponse(request, HttpStatus.UNAUTHORIZED,
                e, "Sin autorización", ErrorCode.SA);
    }

    /**
     * Maneja accesos a recursos para los que el usuario no tiene permisos
     * suficientes.
     *
     * @param request petición HTTP original.
     * @param e excepción de acceso prohibido capturada.
     * @return {@link ResponseEntity} con status {@code 403 Forbidden}.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenException(
            HttpServletRequest request, ForbiddenException e) {
        return buildErrorResponse(request, HttpStatus.FORBIDDEN,
                e, "No autorizado", ErrorCode.SA);
    }

    /**
     * Maneja búsquedas de recursos que no existen en el sistema.
     *
     * @param request petición HTTP original.
     * @param e excepción de datos no encontrados capturada.
     * @return {@link ResponseEntity} con status {@code 404 Not Found}.
     */
    @ExceptionHandler(NoDataException.class)
    public ResponseEntity<ProblemDetail> handleNoDataException(
            HttpServletRequest request, NoDataException e) {
        return buildErrorResponse(request, HttpStatus.NOT_FOUND,
                e, "Sin datos", ErrorCode.SD);
    }

    /**
     * Maneja errores generales de lógica de negocio definidos en el dominio.
     *
     * @param request petición HTTP original.
     * @param e excepción de dominio capturada.
     * @return {@link ResponseEntity} con status
     * {@code 422 Unprocessable Content}.
     */
    @ExceptionHandler(UtemException.class)
    public ResponseEntity<ProblemDetail> handleLyException(
            HttpServletRequest request, UtemException e) {
        return buildErrorResponse(request, HttpStatus.UNPROCESSABLE_CONTENT,
                e, "Error de negocio", ErrorCode.VF);
    }

    /**
     * Maneja violaciones de reglas de validación de dominio personalizadas.
     *
     * @param request petición HTTP original.
     * @param e excepción de validación capturada.
     * @return {@link ResponseEntity} con status {@code 400 Bad Request}.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            HttpServletRequest request, ValidationException e) {
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST,
                e, "Error de validación", ErrorCode.VF);
    }

    /**
     * Maneja la superación del límite de solicitudes permitidas (rate
     * limiting).
     *
     * @param request petición HTTP original.
     * @param e excepción de límite de tasa capturada.
     * @return {@link ResponseEntity} con status {@code 429 Too Many Requests}.
     */
    @ExceptionHandler(LimitException.class)
    public ResponseEntity<ProblemDetail> handleLimitException(
            HttpServletRequest request, LimitException e) {
        return buildErrorResponse(request, HttpStatus.TOO_MANY_REQUESTS,
                e, "Tasa de consulta superada, por favor espere", ErrorCode.VF);
    }

    // -------------------------------------------------------------------------
    // Bean Validation
    // -------------------------------------------------------------------------
    /**
     * Maneja fallos de validación en DTOs anotados con {@code @Valid} o
     * {@code @Validated}.
     *
     * <p>
     * Agrega al {@link ProblemDetail} una propiedad {@code errors} con la lista
     * de campos inválidos y sus mensajes, para que el cliente identifique
     * exactamente qué atributos debe corregir.
     *
     * @param request petición HTTP original.
     * @param ex excepción con el resultado de binding que contiene los errores
     * de campo.
     * @return {@link ResponseEntity} con status {@code 400 Bad Request} y el
     * detalle de cada campo inválido en la propiedad {@code errors}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
            HttpServletRequest request, MethodArgumentNotValidException ex) {

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                "field", fieldError.getField(),
                KEY_MESSAGE, Objects.toString(
                        StringUtils.trimToNull(fieldError.getDefaultMessage()), "Invalid value")
        ))
                .toList();

        String detail = errors.stream()
                .map(errorMap -> errorMap.get("field") + ": " + errorMap.get(KEY_MESSAGE))
                .collect(Collectors.joining(", "));

        ProblemDetail pd = makeProblemDetail(request, HttpStatus.BAD_REQUEST, detail, ErrorCode.MI);
        pd.setProperty("errors", errors);
        return new ResponseEntity<>(pd, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja violaciones de constraints de Bean Validation en parámetros de
     * ruta y query string (requiere {@code @Validated} en el controlador).
     *
     * <p>
     * Incluye en el {@link ProblemDetail} la propiedad {@code errors} con cada
     * propiedad violada y su mensaje descriptivo.
     *
     * @param request petición HTTP original.
     * @param ex excepción con el conjunto de violaciones de constraint
     * detectadas.
     * @return {@link ResponseEntity} con status {@code 400 Bad Request} y el
     * detalle de cada propiedad inválida en la propiedad {@code errors}.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            HttpServletRequest request, ConstraintViolationException ex) {

        List<Map<String, String>> errors = ex.getConstraintViolations().stream()
                .map(violation -> Map.of(
                "property", String.valueOf(violation.getPropertyPath()),
                KEY_MESSAGE, Objects.toString(
                        StringUtils.trimToNull(violation.getMessage()), "Invalid value")
        ))
                .toList();

        String detail = errors.stream()
                .map(errorMap -> errorMap.get("property") + ": " + errorMap.get(KEY_MESSAGE))
                .collect(Collectors.joining(", "));

        ProblemDetail pd = makeProblemDetail(request, HttpStatus.BAD_REQUEST, detail, ErrorCode.MP);
        pd.setProperty("errors", errors);
        return new ResponseEntity<>(pd, HttpStatus.BAD_REQUEST);
    }

    // -------------------------------------------------------------------------
    // Parsing y binding
    // -------------------------------------------------------------------------
    /**
     * Maneja cuerpos de solicitud JSON inválidos o malformados, por ejemplo
     * JSON sintácticamente incorrecto o un tipo incompatible con el campo
     * esperado.
     *
     * @param request petición HTTP original.
     * @param e excepción de lectura del mensaje HTTP capturada.
     * @return {@link ResponseEntity} con status {@code 400 Bad Request}.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleNotReadable(
            HttpServletRequest request, HttpMessageNotReadableException e) {
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST,
                e, "Cuerpo de solicitud inválido o malformado", ErrorCode.MP);
    }

    /**
     * Maneja el caso en que un parámetro de ruta o query no puede convertirse
     * al tipo esperado por el controlador, por ejemplo un valor de texto donde
     * se espera un {@link Long}.
     *
     * @param request petición HTTP original.
     * @param e excepción de tipo de argumento incompatible capturada.
     * @return {@link ResponseEntity} con status {@code 400 Bad Request}.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
            HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST,
                e, "Tipo de argumento inválido", ErrorCode.MP);
    }

    /**
     * Maneja la ausencia de parámetros de query string declarados como
     * requeridos en el controlador.
     *
     * @param request petición HTTP original.
     * @param e excepción de parámetro de servlet ausente capturada.
     * @return {@link ResponseEntity} con status {@code 400 Bad Request}.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParam(
            HttpServletRequest request, MissingServletRequestParameterException e) {
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST,
                e, "Parámetro requerido ausente", ErrorCode.MP);
    }

    // -------------------------------------------------------------------------
    // Negociación HTTP
    // -------------------------------------------------------------------------
    /**
     * Maneja solicitudes cuyo {@code Content-Type} no es soportado por el
     * endpoint invocado.
     *
     * @param request petición HTTP original.
     * @param e excepción de media type no soportado capturada.
     * @return {@link ResponseEntity} con status
     * {@code 415 Unsupported Media Type}.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMediaType(
            HttpServletRequest request, HttpMediaTypeNotSupportedException e) {
        return buildErrorResponse(request, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                e, "Tipo de contenido no soportado", ErrorCode.MT);
    }

    /**
     * Maneja solicitudes HTTP cuyo verbo (GET, POST, PUT, etc.) no está
     * habilitado en el endpoint invocado.
     *
     * @param request petición HTTP original.
     * @param e excepción de método HTTP no soportado capturada.
     * @return {@link ResponseEntity} con status {@code 405 Method Not Allowed}.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(
            HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        return buildErrorResponse(request, HttpStatus.METHOD_NOT_ALLOWED,
                e, "Método HTTP no permitido", ErrorCode.MS);
    }

    // -------------------------------------------------------------------------
    // Fallback
    // -------------------------------------------------------------------------
    /**
     * Captura cualquier excepción no contemplada por los handlers anteriores.
     *
     * <p>
     * <strong>Importante:</strong> toda ocurrencia de este handler indica un
     * caso no modelado en el dominio. Debe revisarse en los logs y evaluarse si
     * amerita una excepción personalizada o un nuevo handler específico.
     *
     * @param request petición HTTP original.
     * @param e excepción no contemplada capturada.
     * @return {@link ResponseEntity} con status
     * {@code 500 Internal Server Error}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            HttpServletRequest request, Exception e) {
        return buildErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR,
                e, "Error NO manejado", ErrorCode.DC);
    }
}
