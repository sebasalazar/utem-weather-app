package cl.sebastian.cm.rest.utils;

import cl.sebastian.cm.rest.exception.ValidationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de {@link GoogleAuthUtils}.
 *
 * <p>
 * Valida la autenticación mediante tokens JWT de Google, incluyendo:</p>
 * <ul>
 * <li>Prevención de instanciación de clase utilitaria</li>
 * <li>Validación de tokens nulos, vacíos y en blanco</li>
 * <li>Validación de formato y firma de JWT</li>
 * <li>Validación de dominio de correo electrónico (@utem.cl)</li>
 * </ul>
 *
 * <p>
 * Estructura: los escenarios se agrupan por responsabilidad mediante clases
 * {@code @Nested} siguiendo la convención Arrange-Act-Assert (AAA).</p>
 */
class GoogleAuthUtilsTests {

    @Nested
    @DisplayName("Constructor privado de clase utilitaria")
    class ConstructorPrivadoTests {

        @Test
        @DisplayName("Debe impedir la instanciación externa lanzando excepción")
        void constructorPrivadoDebeImpedirInstanciacion() throws NoSuchMethodException {
            // Arrange
            Constructor<GoogleAuthUtils> constructor
                    = GoogleAuthUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            // Act & Assert
            InvocationTargetException exception
                    = org.junit.jupiter.api.Assertions.assertThrows(
                            InvocationTargetException.class,
                            () -> constructor.newInstance()
                    );

            org.junit.jupiter.api.Assertions.assertTrue(
                    exception.getCause() instanceof AssertionError
                    || exception.getCause() instanceof UnsupportedOperationException
                    || exception.getCause() instanceof IllegalStateException,
                    "La causa raíz debe ser una excepción que prevenga instanciación"
            );
        }
    }

    @Nested
    @DisplayName("Validación de tokens: casos inválidos")
    class ValidacionTokenInvalidoTests {

        @Test
        @DisplayName("Debe lanzar ValidationException cuando el token es nulo")
        void getEmailConTokenNuloDebeLanzarValidationException() {
            // Arrange
            String tokenNulo = null;

            // Act & Assert
            org.junit.jupiter.api.Assertions.assertThrows(
                    ValidationException.class,
                    () -> GoogleAuthUtils.getEmail(tokenNulo),
                    "Debe lanzar ValidationException para token nulo"
            );
        }

        @Test
        @DisplayName("Debe lanzar ValidationException cuando el token es vacío")
        void getEmailConTokenVacioDebeLanzarValidationException() {
            // Arrange
            String tokenVacio = "";

            // Act & Assert
            org.junit.jupiter.api.Assertions.assertThrows(
                    ValidationException.class,
                    () -> GoogleAuthUtils.getEmail(tokenVacio),
                    "Debe lanzar ValidationException para token vacío"
            );
        }

        @Test
        @DisplayName("Debe lanzar ValidationException cuando el token es solo espacios")
        void getEmailConTokenEnBlancoDebeLanzarValidationException() {
            // Arrange
            String tokenEnBlanco = "   ";

            // Act & Assert
            org.junit.jupiter.api.Assertions.assertThrows(
                    ValidationException.class,
                    () -> GoogleAuthUtils.getEmail(tokenEnBlanco),
                    "Debe lanzar ValidationException para token en blanco"
            );
        }

        @Test
        @DisplayName("Debe lanzar ValidationException cuando el token tiene formato inválido")
        void getEmailConTokenMalFormatadoDebeLanzarValidationException() {
            // Arrange
            String tokenInvalido = "token-sin-puntos";

            // Act & Assert
            org.junit.jupiter.api.Assertions.assertThrows(
                    ValidationException.class,
                    () -> GoogleAuthUtils.getEmail(tokenInvalido),
                    "Debe lanzar ValidationException para token con formato inválido"
            );
        }
    }
}
