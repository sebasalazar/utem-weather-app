package cl.sebastian.cm.rest.conf;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada de OpenAPI 3.0 y Swagger UI para la API REST.
 * <p>
 * Define la especificación OpenAPI que documenta automáticamente todos los
 * endpoints REST de la plataforma. La documentación interactiva estará
 * disponible en {@code /swagger-ui.html} y el esquema JSON en
 * {@code /v3/api-docs}.
 * </p>
 * <p>
 * Esta configuración aplica un esquema de seguridad global basado en JWT
 * (Bearer Token) a todos los endpoints. Los endpoints que no requieran
 * autenticación deben anotarse con {@code @Operation(security = {})}.
 * </p>
 *
 * <h2>Flujo de generación de la documentación</h2>
 * <ol>
 * <li>SpringDoc escanea todos los controladores anotados con
 * {@code @RestController}.</li>
 * <li>Para cada método con mapeo HTTP ({@code @GetMapping},
 * {@code @PostMapping}, etc.) genera la documentación del endpoint.</li>
 * <li>Combina esa información con los metadatos definidos en este bean
 * {@link OpenAPI}.</li>
 * <li>Expone el esquema completo en {@code /v3/api-docs}.</li>
 * <li>Swagger UI consume este esquema para generar la interfaz
 * interactiva.</li>
 * </ol>
 *
 * <h2>Configuración de seguridad</h2>
 * <p>
 * La línea
 * {@code .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))}
 * aplica el esquema "bearerAuth" globalmente a todos los endpoints, indicando
 * que requieren autenticación. Si un endpoint no necesita autenticación, se
 * debe anotar con {@code @Operation(security = {})}.
 * </p>
 *
 * <h2>Ambientes</h2>
 * <p>
 * Se definen dos servidores:
 * <ul>
 * <li><strong>Desarrollo</strong>: {@code http://localhost:8080/cmutem}</li>
 * <li><strong>Producción</strong>: {@code https://api.sebastian.cl/cmutem}</li>
 * </ul>
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 * @see OpenAPI
 * @see SecurityScheme
 */
@Configuration
@Schema(description = "Configuración de OpenAPI para la documentación Swagger de los servicios REST")
public class OpenApiConfig {

    /**
     * Nombre del esquema de seguridad utilizado para el JWT Bearer Token. Este
     * nombre se usa tanto en el requerimiento de seguridad como en la
     * definición del componente.
     */
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Crea el bean {@link OpenAPI} que contiene toda la especificación.
     * <p>
     * Este bean es utilizado automáticamente por SpringDoc para generar el
     * esquema OpenAPI. Define:
     * <ul>
     * <li><strong>Metadatos del API</strong>: título, versión, descripción,
     * términos de servicio, contacto y licencia.</li>
     * <li><strong>Servidores</strong>: URLs de los ambientes de desarrollo y
     * producción.</li>
     * <li><strong>Seguridad global</strong>: esquema de autenticación Bearer
     * JWT.</li>
     * </ul>
     * </p>
     *
     * @return una instancia de {@link OpenAPI} completamente configurada, lista
     * para ser serializada como JSON OpenAPI 3.0.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 1. Requerimiento de seguridad global (Bearer JWT)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                // 2. Componentes de seguridad: definición del esquema Bearer
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                // 3. Servidores (ambientes)
                .addServersItem(new Server()
                        .url("http://localhost:8080/cmutem")
                        .description("Ambiente de desarrollo local"))
                .addServersItem(new Server()
                        .url("https://api.sebastian.cl/cmutem")
                        .description("Ambiente de producción"))
                // 4. Metadatos de la API
                .info(new Info()
                        .title("Servicios ReST")
                        .version("v1")
                        .description("API para los proyectos de computación móvil "
                                + "de la Universidad Tecnológica Metropolitana del estado de Chile")
                        .termsOfService("https://transparencia.utem.cl/potestades-y-marco-normativo/marco-normativo/reglamentos-generales-de-la-utem/")
                        .contact(new Contact()
                                .name("Sebastián Salazar")
                                .email("ssalazar@utem.cl"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
