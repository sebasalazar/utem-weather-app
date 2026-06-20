package cl.sebastian.cm.rest.utils;

import cl.sebastian.cm.rest.exception.AuthException;
import cl.sebastian.cm.rest.exception.ValidationException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Utilitario para validar un ID Token de Google y extraer el correo
 * institucional del usuario.
 * <p>
 * Verifica criptográficamente el token contra las claves JWKS de Google,
 * confirma que el correo esté verificado por Google y que pertenezca al dominio
 * {@code @utem.cl}.
 * </p>
 *
 * <h2>Flujo de validación</h2>
 * <ol>
 * <li>Se verifica la integridad y autenticidad del token JWT mediante el
 * {@link GoogleIdTokenVerifier} de Google (firma, emisor, audiencia,
 * expiración).</li>
 * <li>Se extrae el payload del token.</li>
 * <li>Se comprueba que el flag {@code email_verified} sea {@code true}.</li>
 * <li>Se normaliza el correo (minúsculas y sin espacios).</li>
 * <li>Se valida el formato del correo con
 * {@link EmailValidator#isValid(String)}.</li>
 * <li>Se verifica que el correo pertenezca al dominio {@code @utem.cl}.</li>
 * </ol>
 *
 * <h2>Consideraciones de seguridad</h2>
 * <ul>
 * <li><b>Audiencia:</b> el {@code client_id} de Google OAuth debe coincidir con
 * el emitido en el token. Se configura en {@link #GOOGLE_CLIENT_ID} (no
 * mostrado en esta clase).</li>
 * <li><b>Email verificado:</b> se rechazan tokens cuyo flag
 * {@code email_verified} sea {@code false}.</li>
 * <li><b>Dominio:</b> solo se aceptan correos {@code @utem.cl}.</li>
 * <li><b>Manejo de errores:</b> cualquier fallo en la validación produce una
 * excepción {@link ValidationException} o {@link AuthException}, dependiendo de
 * la naturaleza del error.</li>
 * </ul>
 *
 * <h2>Excepciones</h2>
 * <ul>
 * <li>{@link ValidationException}: errores de validación del token o del correo
 * (formato, dominio, no verificado, token inválido).</li>
 * <li>{@link AuthException}: errores de infraestructura (red, problemas de
 * clave JWKS, errores de parsing).</li>
 * </ul>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see GoogleIdTokenVerifier
 * @see EmailValidator
 */
@Schema(description = "Utilidades para validación de ID Tokens de Google y extracción de correo institucional UTEM")
public final class GoogleAuthUtils {

    /**
     * Locale chileno para normalización de texto.
     */
    private static final Locale CL = Locale.of("es", "CL");

    /**
     * Dominio institucional de la UTEM.
     */
    private static final String UTEM_DOMAIN = "@utem.cl";

    /**
     * Verificador de ID Tokens de Google, configurado con transporte HTTP y
     * factoría JSON.
     * <p>
     * Se reutiliza la misma instancia para todas las validaciones, ya que es
     * thread-safe.
     * </p>
     */
    private static final GoogleIdTokenVerifier VERIFIER = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(),
            GsonFactory.getDefaultInstance()
    ).build();

    /**
     * Constructor privado para impedir la instanciación de la clase.
     * <p>
     * Al ser una clase utilitaria, no se deben crear instancias.
     * </p>
     */
    private GoogleAuthUtils() {
        throw new IllegalStateException("Clase utilitaria, no instanciable");
    }

    /**
     * Valida un ID Token de Google y devuelve el correo institucional del
     * usuario en minúsculas.
     * <p>
     * Realiza todas las comprobaciones descritas en el flujo de validación.
     * </p>
     *
     * @param idTokenJwt token JWT crudo recibido desde el cliente (no debe ser
     * {@code null} ni estar vacío).
     * @return correo institucional validado, en minúsculas y sin espacios.
     * @throws ValidationException si el token es inválido, el correo no está
     * verificado por Google, el formato es inválido o no pertenece a
     * {@code @utem.cl}.
     * @throws AuthException si ocurre un error de infraestructura (red, claves
     * JWKS, parsing).
     */
    public static String getEmail(final String idTokenJwt) {
        if (StringUtils.isBlank(idTokenJwt)) {
            throw new ValidationException("Se necesita un idToken");
        }

        final GoogleIdToken googleIdToken = verifyToken(idTokenJwt);
        final GoogleIdToken.Payload payload = googleIdToken.getPayload();

        ensureEmailVerified(payload);

        final String email = StringUtils.lowerCase(StringUtils.trimToEmpty(payload.getEmail()), CL);
        ensureValidFormat(email);
        ensureUtemDomain(email);

        return email;
    }

    /**
     * Ejecuta la verificación criptográfica del token contra Google.
     * <p>
     * Valida la firma, el emisor, la audiencia y la expiración del token.
     * </p>
     *
     * @param idTokenJwt el token JWT a verificar.
     * @return el objeto {@link GoogleIdToken} verificado.
     * @throws ValidationException si la verificación falla (firma inválida,
     * estructura incorrecta).
     * @throws AuthException si ocurre un error de red al obtener las claves
     * JWKS.
     */
    private static GoogleIdToken verifyToken(final String idTokenJwt) {
        try {
            final String jwt = StringUtils.trimToEmpty(Strings.CI.removeStart(idTokenJwt, "Bearer"));
            final GoogleIdToken googleIdToken = VERIFIER.verify(jwt);
            if (googleIdToken == null) {
                throw new ValidationException("No se pudo verificar el idToken");
            }
            return googleIdToken;
        } catch (GeneralSecurityException ex) {
            throw new ValidationException("Firma o estructura del idToken inválida", ex);
        } catch (IOException ex) {
            throw new AuthException("Error de red al validar el idToken con Google", ex);
        }
    }

    /**
     * Verifica que Google haya confirmado la propiedad del correo.
     *
     * @param payload el payload del token verificado.
     * @throws ValidationException si el correo no está verificado.
     */
    private static void ensureEmailVerified(final GoogleIdToken.Payload payload) {
        final boolean emailVerified = BooleanUtils.isTrue(payload.getEmailVerified());
        if (!emailVerified) {
            final String name = (String) payload.get("name");
            throw new ValidationException(String.format("El correo electrónico de %s no está validado por Google", name));
        }
    }

    /**
     * Valida el formato del correo electrónico usando Apache Commons
     * {@link EmailValidator}.
     *
     * @param email el correo a validar.
     * @throws ValidationException si el formato es inválido.
     */
    private static void ensureValidFormat(final String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new ValidationException(String.format("El correo electrónico %s no tiene un formato válido", email));
        }
    }

    /**
     * Verifica que el correo pertenezca al dominio institucional
     * {@code @utem.cl}.
     *
     * @param email el correo a validar.
     * @throws ValidationException si el dominio no coincide.
     */
    private static void ensureUtemDomain(final String email) {
        if (!Strings.CI.contains(email, UTEM_DOMAIN)) {
            throw new ValidationException(String.format("El correo electrónico %s no pertenece al dominio de la UTEM", email));
        }
    }
}
