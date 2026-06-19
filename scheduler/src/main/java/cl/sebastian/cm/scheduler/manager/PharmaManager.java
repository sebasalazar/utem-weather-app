package cl.sebastian.cm.scheduler.manager;

import cl.sebastian.cm.scheduler.domain.data.FarmaciaTurno;
import cl.sebastian.cm.scheduler.domain.enums.Commerce;
import cl.sebastian.cm.scheduler.domain.model.Pharmacy;
import cl.sebastian.cm.scheduler.domain.model.PharmacyOnDuty;
import cl.sebastian.cm.scheduler.domain.repository.PharmacyOnDutyRepository;
import cl.sebastian.cm.scheduler.domain.repository.PharmacyRepository;
import cl.sebastian.cm.scheduler.utils.PhoneNumberUtils;
import cl.sebastian.cm.scheduler.utils.TextUtils;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de gestión de farmacias y sus turnos.
 * <p>
 * Esta clase orquesta la lógica de negocio para persistir información de
 * farmacias y sus registros de turno, garantizando la integridad referencial y
 * evitando duplicados.
 * </p>
 * <p>
 * Utiliza los repositorios {@link PharmacyRepository} y
 * {@link PharmacyOnDutyRepository} para realizar operaciones CRUD sobre las
 * entidades, y aplica normalización de datos mediante {@link TextUtils} y
 * {@link PhoneNumberUtils}.
 * </p>
 * <p>
 * El flujo principal consiste en:
 * </p>
 * <ol>
 * <li>Recibir un objeto {@link FarmaciaTurno} (fuente de datos externa).</li>
 * <li>Buscar o crear la farmacia correspondiente según su {@code storeId}.</li>
 * <li>Buscar o crear el registro de turno para esa farmacia y fecha.</li>
 * <li>Persistir ambos (o actualizar si ya existen) de manera atómica.</li>
 * </ol>
 * <p>
 * Las operaciones son transaccionales, por lo que cualquier fallo intermedio
 * produce un rollback completo.
 * </p>
 *
 * @author Sebastián Salazar
 * @version 1.0
 * @see Pharmacy
 * @see PharmacyOnDuty
 * @see FarmaciaTurno
 * @see PharmacyRepository
 * @see PharmacyOnDutyRepository
 */
@Service
public class PharmaManager {

    /**
     * Arreglo con todos los valores del enum {@link Commerce}, precalculado
     * para iteraciones eficientes en la detección del comercio.
     */
    private static final Commerce[] COMMERCE_VALUES = Commerce.values();

    /**
     * Logger de la clase para registrar eventos, advertencias y errores.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PharmaManager.class);

    /**
     * Repositorio para operaciones de la entidad {@link Pharmacy}.
     */
    private final PharmacyRepository pharmacyRepository;

    /**
     * Repositorio para operaciones de la entidad {@link PharmacyOnDuty}.
     */
    private final PharmacyOnDutyRepository pharmacyOnDutyRepository;

    /**
     * Constructor que inyecta los repositorios necesarios.
     *
     * @param pharmacyRepository repositorio de farmacias.
     * @param pharmacyOnDutyRepository repositorio de turnos de farmacias.
     */
    @Autowired
    public PharmaManager(final PharmacyRepository pharmacyRepository,
            final PharmacyOnDutyRepository pharmacyOnDutyRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.pharmacyOnDutyRepository = pharmacyOnDutyRepository;
    }

    /**
     * Construye la dirección completa de la farmacia a partir de los datos del
     * turno.
     * <p>
     * La dirección se compone de: dirección, localidad y comuna, normalizados y
     * recortados a un máximo de 254 caracteres. Si el turno es {@code null} o
     * alguno de los campos es nulo, se maneja adecuadamente (se usa cadena
     * vacía).
     * </p>
     *
     * @param ft el objeto {@link FarmaciaTurno} (puede ser {@code null}).
     * @return la dirección formateada y en mayúsculas, nunca {@code null}.
     */
    private String getAddress(final FarmaciaTurno ft) {
        if (ft == null) {
            return StringUtils.EMPTY;
        }

        final String address = TextUtils.normalize(ft.getPharmacyAddress());
        final String locality = TextUtils.normalize(ft.getLocalityName());
        final String commune = TextUtils.normalize(ft.getCommuneName());

        return TextUtils.upper(StringUtils.substring(String.format("%s, %s, %s",
                address,
                locality,
                commune), 0, 254)
        );
    }

    /**
     * Determina el tipo de comercio de la farmacia basándose en su nombre.
     * <p>
     * Compara el nombre de la farmacia (en mayúsculas) con los nombres de los
     * valores del enum {@link Commerce}. Si encuentra coincidencia (insensible
     * a mayúsculas, pero ya convertido), retorna ese comercio. En caso
     * contrario, retorna {@link Commerce#DESCONOCIDO}.
     * </p>
     *
     * @param ft el objeto {@link FarmaciaTurno} (puede ser {@code null}).
     * @return el comercio detectado, o {@code DESCONOCIDO} si no se detecta.
     */
    private Commerce getCommerce(final FarmaciaTurno ft) {
        if (ft == null) {
            return Commerce.DESCONOCIDO;
        }

        final String rawName = ft.getPharmacyName();
        if (rawName == null) {
            return Commerce.DESCONOCIDO;
        }

        final String pharmacyName = TextUtils.upper(rawName);
        for (final Commerce commerce : COMMERCE_VALUES) {
            if (commerce != Commerce.DESCONOCIDO && pharmacyName.contains(commerce.name())) {
                return commerce;
            }
        }

        return Commerce.DESCONOCIDO;
    }

    /**
     * Obtiene el número de teléfono normalizado a formato internacional (+56).
     * <p>
     * Utiliza {@link PhoneNumberUtils#normalizeChileanPhone(String)} para
     * limpiar y validar el número. Si el turno es {@code null}, retorna
     * {@code 0L}.
     * </p>
     *
     * @param ft el objeto {@link FarmaciaTurno} (puede ser {@code null}).
     * @return el teléfono normalizado como {@code long}, o {@code 0L} si no es
     * válido.
     */
    private long getPhone(final FarmaciaTurno ft) {
        if (ft == null) {
            return 0L;
        }

        return PhoneNumberUtils.normalizeChileanPhone(ft.getPharmacyPhone());
    }

    /**
     * Persiste la información de una farmacia y su turno a partir de un objeto
     * {@link FarmaciaTurno}.
     * <p>
     * El método realiza las siguientes operaciones en una transacción:
     * </p>
     * <ol>
     * <li>Valida que el objeto y sus campos obligatorios ({@code storeId},
     * {@code date}) no sean nulos.</li>
     * <li>Busca la farmacia por {@code storeId}. Si no existe, la crea y la
     * guarda.</li>
     * <li>Busca un turno existente para esa farmacia y fecha. Si no existe, lo
     * crea y lo guarda.</li>
     * <li>Si ya existe la farmacia o el turno, no se actualiza (se omite para
     * evitar duplicados).</li>
     * </ol>
     * <p>
     * <strong>Nota:</strong> Los objetos creados se inicializan con las marcas
     * de tiempo {@code created} y {@code updated} al momento actual.
     * </p>
     *
     * @param ft el objeto {@link FarmaciaTurno} con los datos a guardar.
     * @throws IllegalArgumentException si {@code storeId} es {@code null}.
     */
    @Transactional
    public void save(final FarmaciaTurno ft) {
        if (ft == null) {
            LOGGER.warn("FarmaciaTurno es null, omitiendo guardado");
            return;
        }

        final LocalDate dutyDate = ft.getDate();
        if (dutyDate == null) {
            LOGGER.warn("Fecha de turno es null para storeId={}, omitiendo", ft.getStoreId());
            return;
        }

        if (ft.getStoreId() == null) {
            throw new IllegalArgumentException("storeId no puede ser null");
        }

        final OffsetDateTime now = OffsetDateTime.now();

        Pharmacy pharmacy = pharmacyRepository.findByStoreId(ft.getStoreId());
        if (pharmacy == null) {
            LOGGER.info("Creando nueva farmacia con storeId={}", ft.getStoreId());
            pharmacy = createPharmacy(ft, now);
            pharmacy = pharmacyRepository.save(pharmacy);
        }

        PharmacyOnDuty pod = pharmacyOnDutyRepository.findByPharmacyAndDutyDate(pharmacy, dutyDate);
        if (pod == null) {
            LOGGER.info("Creando turno para farmacia id={} en fecha={}", pharmacy.getId(), dutyDate);
            pod = createPharmacyOnDuty(pharmacy, dutyDate, now);
            pharmacyOnDutyRepository.save(pod);
        }
    }

    /**
     * Crea una instancia de {@link Pharmacy} a partir de los datos del turno,
     * asignando todas sus propiedades y marcas de tiempo.
     *
     * @param ft el objeto {@link FarmaciaTurno} fuente.
     * @param now la marca de tiempo actual para los campos {@code created} y
     * {@code updated}.
     * @return una nueva entidad {@link Pharmacy} (no persistida).
     * @see #getAddress(FarmaciaTurno)
     * @see #getCommerce(FarmaciaTurno)
     * @see #getPhone(FarmaciaTurno)
     */
    private Pharmacy createPharmacy(final FarmaciaTurno ft, final OffsetDateTime now) {
        final Pharmacy pharmacy = new Pharmacy();
        pharmacy.setAddress(getAddress(ft));
        pharmacy.setCommerce(getCommerce(ft));
        pharmacy.setEndTime(ft.getEndTime());
        pharmacy.setLatitude(ft.getLatitude());
        pharmacy.setLongitude(ft.getLongitude());
        pharmacy.setName(TextUtils.upper(ft.getPharmacyName()));
        pharmacy.setPhone(getPhone(ft));
        pharmacy.setStartTime(ft.getStartTime());
        pharmacy.setStoreId(ft.getStoreId());
        pharmacy.setCreated(now);
        pharmacy.setUpdated(now);
        return pharmacy;
    }

    /**
     * Crea una instancia de {@link PharmacyOnDuty} para una farmacia y fecha
     * dadas, asignando las marcas de tiempo.
     *
     * @param pharmacy la farmacia asociada.
     * @param dutyDate la fecha del turno.
     * @param now la marca de tiempo actual para los campos {@code created} y
     * {@code updated}.
     * @return una nueva entidad {@link PharmacyOnDuty} (no persistida).
     */
    private PharmacyOnDuty createPharmacyOnDuty(final Pharmacy pharmacy,
            final LocalDate dutyDate, final OffsetDateTime now) {
        final PharmacyOnDuty pod = new PharmacyOnDuty();
        pod.setDutyDate(dutyDate);
        pod.setPharmacy(pharmacy);
        pod.setCreated(now);
        pod.setUpdated(now);
        return pod;
    }
}
