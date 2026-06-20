package cl.sebastian.cm.rest.domain.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * DTO que representa la distancia calculada desde un punto de referencia hasta
 * un elemento identificado por su ID.
 * <p>
 * Esta clase es inmutable: sus campos son {@code final} y no posee setters. Se
 * utiliza principalmente en respuestas de servicios que requieren devolver un
 * identificador junto con la distancia calculada (por ejemplo, farmacias más
 * cercanas, puntos de interés, etc.).
 * </p>
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
@Schema(description = "DTO que asocia un identificador con la distancia calculada desde un punto de referencia")
public class IdDistance {

    /**
     * Identificador único del elemento (por ejemplo, ID de una farmacia o
     * estación).
     */
    @Schema(description = "Identificador único del elemento", example = "12345")
    private final long id;

    /**
     * Distancia calculada desde el punto de referencia al elemento, en metros.
     */
    @Schema(description = "Distancia calculada en metros desde el punto de referencia", example = "1500")
    private final int distance;

    /**
     * Constructor que inicializa el ID y la distancia.
     *
     * @param id identificador del elemento (no puede ser negativo en el
     * contexto de la aplicación, aunque no se valida aquí).
     * @param distance distancia en metros (valor absoluto, no negativo).
     */
    public IdDistance(long id, int distance) {
        this.id = id;
        this.distance = distance;
    }

    /**
     * Obtiene el identificador del elemento.
     *
     * @return el ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Obtiene la distancia en metros.
     *
     * @return la distancia.
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Calcula el código hash basado exclusivamente en el {@code id}.
     *
     * @return el valor hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compara este objeto con otro para determinar igualdad.
     * <p>
     * Dos instancias de {@code IdDistance} se consideran iguales si tienen el
     * mismo {@code id}, independientemente de la distancia.
     * </p>
     *
     * @param obj el objeto a comparar.
     * @return {@code true} si son iguales, {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IdDistance other = (IdDistance) obj;
        return this.id == other.id;
    }
}
