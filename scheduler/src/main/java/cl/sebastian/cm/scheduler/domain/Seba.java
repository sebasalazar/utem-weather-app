package cl.sebastian.cm.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Clase base para todas las entidades del sistema. Proporciona funcionalidad
 * común como serialización y representación en string.
 *
 * @author Sebastián Salazar Molina
 * @since 0.9.9
 * @version 0.9.9
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Seba implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Genera una representación en string de la entidad usando el estilo JSON
     * de Apache Commons Lang.
     *
     * @return Representación en string de la entidad (formato similar a JSON,
     * pero no es JSON válido según RFC 8259).
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
