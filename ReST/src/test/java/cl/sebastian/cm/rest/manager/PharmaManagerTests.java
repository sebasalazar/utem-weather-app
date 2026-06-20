package cl.sebastian.cm.rest.manager;

import cl.sebastian.cm.rest.domain.data.IdDistance;
import cl.sebastian.cm.rest.domain.model.Pharmacy;
import cl.sebastian.cm.rest.domain.model.PharmacyOnDuty;
import cl.sebastian.cm.rest.domain.repository.PharmacyOnDutyRepository;
import cl.sebastian.cm.rest.domain.repository.PharmacyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PharmaManagerTests {

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PharmacyOnDutyRepository pharmacyOnDutyRepository;

    private PharmaManager pharmaManager;

    @BeforeEach
    void setUp() {
        pharmaManager = new PharmaManager(pharmacyRepository, pharmacyOnDutyRepository, 15000);
    }

    private Pharmacy createPharmacy(long id, LocalTime startTime, LocalTime endTime) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(id);
        pharmacy.setName("Farmacia " + id);
        pharmacy.setStartTime(startTime);
        pharmacy.setEndTime(endTime);
        return pharmacy;
    }

    private LocalDateTime createDateTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    @Test
    void testConstructorWithDefaultMaxDistance() {
        PharmaManager manager = new PharmaManager(pharmacyRepository, pharmacyOnDutyRepository, null);
        assertEquals(1, manager.getMaxDistance());
    }

    @Test
    void testConstructorWithValidMaxDistance() {
        PharmaManager manager = new PharmaManager(pharmacyRepository, pharmacyOnDutyRepository, 5000);
        assertEquals(5000, manager.getMaxDistance());
    }

    @Test
    void testGetNearbyOnDutyWithNullDateTime() {
        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, null);
        assertTrue(result.isEmpty());
        verify(pharmacyRepository, never()).searchByDistance(anyDouble(), anyDouble(), anyLong());
    }

    @Test
    void testGetNearbyOnDutyWithInvalidCoordinates() {
        LocalDateTime ldt = LocalDateTime.now();
        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(91, 0, ldt);
        assertTrue(result.isEmpty());
        verify(pharmacyRepository, never()).searchByDistance(anyDouble(), anyDouble(), anyLong());
    }

    @Test
    void testGetNearbyOnDutyNoPharmaciesFound() {
        LocalDateTime ldt = LocalDateTime.now();
        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(Collections.emptyList());

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, ldt);

        assertTrue(result.isEmpty());
        verify(pharmacyRepository).searchByDistance(-33.4, -70.6, 15000L);
    }

    @Test
    void testGetNearbyOnDutyPharmacyInLaboralTime() {
        LocalDateTime mondayAt10am = createDateTime(2024, 1, 8, 10, 0);
        Pharmacy pharmacy = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, mondayAt10am);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetNearbyOnDutyPharmacyOutsideLaboralTimeButWithDuty() {
        LocalDateTime sunday = createDateTime(2024, 1, 7, 10, 0);
        Pharmacy pharmacy = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));
        PharmacyOnDuty duty = new PharmacyOnDuty();
        duty.setPharmacy(pharmacy);
        duty.setDutyDate(sunday.toLocalDate());

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));
        when(pharmacyOnDutyRepository.findByPharmacyAndDutyDate(pharmacy, sunday.toLocalDate()))
            .thenReturn(duty);

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, sunday);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetNearbyOnDutyPharmacyOutsideLaboralTimeAndNoDuty() {
        LocalDateTime sunday = createDateTime(2024, 1, 7, 10, 0);
        Pharmacy pharmacy = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));
        when(pharmacyOnDutyRepository.findByPharmacyAndDutyDate(pharmacy, sunday.toLocalDate()))
            .thenReturn(null);

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, sunday);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNearbyOnDutyReturnsClosestPharmacy() {
        LocalDateTime mondayAt10am = createDateTime(2024, 1, 8, 10, 0);
        Pharmacy pharmacy1 = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));
        Pharmacy pharmacy2 = createPharmacy(2, LocalTime.of(8, 0), LocalTime.of(18, 0));

        IdDistance id1 = new IdDistance(1L, 10000);
        IdDistance id2 = new IdDistance(2L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(id1, id2));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy1));

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, mondayAt10am);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetNearbyOnDutyPharmacyNotFoundAfterSearch() {
        LocalDateTime ldt = LocalDateTime.now();
        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.empty());

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, ldt);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNearbyOnDutyMultiplePharmaciesFirstMatches() {
        LocalDateTime mondayAt10am = createDateTime(2024, 1, 8, 10, 0);
        Pharmacy pharmacy1 = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));
        Pharmacy pharmacy2 = createPharmacy(2, LocalTime.of(8, 0), LocalTime.of(18, 0));

        IdDistance id1 = new IdDistance(1L, 500);
        IdDistance id2 = new IdDistance(2L, 1000);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(id1, id2));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy1));

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, mondayAt10am);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(pharmacyRepository).findById(1L);
        verify(pharmacyRepository, never()).findById(2L);
    }

    @Test
    void testGetNearbyOnDutyPharmacyTimeAtStartBoundary() {
        LocalDateTime mondayAt8am = createDateTime(2024, 1, 8, 8, 0);
        Pharmacy pharmacy = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, mondayAt8am);

        assertTrue(result.isPresent());
    }

    @Test
    void testGetNearbyOnDutyPharmacyTimeAtEndBoundary() {
        LocalDateTime mondayAt6pm = createDateTime(2024, 1, 8, 18, 0);
        Pharmacy pharmacy = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, mondayAt6pm);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNearbyOnDutyPharmacyTimeBeforeStart() {
        LocalDateTime mondayAt7am = createDateTime(2024, 1, 8, 7, 0);
        Pharmacy pharmacy = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));
        when(pharmacyOnDutyRepository.findByPharmacyAndDutyDate(pharmacy, mondayAt7am.toLocalDate()))
            .thenReturn(null);

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, mondayAt7am);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNearbyOnDutySaturday() {
        LocalDateTime saturday = createDateTime(2024, 1, 6, 10, 0);
        Pharmacy pharmacy = createPharmacy(1, LocalTime.of(8, 0), LocalTime.of(18, 0));

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));
        when(pharmacyOnDutyRepository.findByPharmacyAndDutyDate(pharmacy, saturday.toLocalDate()))
            .thenReturn(null);

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, saturday);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNearbyOnDutyWithNullStartTime() {
        LocalDateTime mondayAt10am = createDateTime(2024, 1, 8, 10, 0);
        Pharmacy pharmacy = createPharmacy(1, null, LocalTime.of(18, 0));

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));
        when(pharmacyOnDutyRepository.findByPharmacyAndDutyDate(pharmacy, mondayAt10am.toLocalDate()))
            .thenReturn(null);

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, mondayAt10am);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNearbyOnDutyWithNullEndTime() {
        LocalDateTime mondayAt10am = createDateTime(2024, 1, 8, 10, 0);
        Pharmacy pharmacy = createPharmacy(1, LocalTime.of(8, 0), null);

        IdDistance idDistance = new IdDistance(1L, 500);

        when(pharmacyRepository.searchByDistance(-33.4, -70.6, 15000L))
            .thenReturn(List.of(idDistance));
        when(pharmacyRepository.findById(1L))
            .thenReturn(Optional.of(pharmacy));

        Optional<Pharmacy> result = pharmaManager.getNearbyOnDuty(-33.4, -70.6, mondayAt10am);

        assertTrue(result.isEmpty());
    }
}
