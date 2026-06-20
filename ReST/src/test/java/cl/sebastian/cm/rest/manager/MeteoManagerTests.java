package cl.sebastian.cm.rest.manager;

import cl.sebastian.cm.rest.domain.data.IdDistance;
import cl.sebastian.cm.rest.domain.model.Observation;
import cl.sebastian.cm.rest.domain.model.Station;
import cl.sebastian.cm.rest.domain.repository.ObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeteoManagerTests {

    @Mock
    private ObservationRepository observationRepository;

    private MeteoManager meteoManager;

    @BeforeEach
    void setUp() {
        meteoManager = new MeteoManager(observationRepository, 15000);
    }

    @Test
    void testConstructorWithDefaultMaxDistance() {
        MeteoManager manager = new MeteoManager(observationRepository, null);
        assertEquals(1, manager.getMaxDistance());
    }

    @Test
    void testConstructorWithMinimalMaxDistance() {
        MeteoManager manager = new MeteoManager(observationRepository, 0);
        assertEquals(1, manager.getMaxDistance());
    }

    @Test
    void testConstructorWithValidMaxDistance() {
        MeteoManager manager = new MeteoManager(observationRepository, 5000);
        assertEquals(5000, manager.getMaxDistance());
    }

    @Test
    void testGetLastNearbyWithValidCoordinates() {
        double latitude = -33.4567;
        double longitude = -70.6543;

        Station station = new Station();
        station.setId(1L);

        Observation observation = new Observation();
        observation.setId(1L);
        observation.setStation(station);
        observation.setTemperature(25.5);
        observation.setDate(OffsetDateTime.now(ZoneOffset.UTC));

        IdDistance idDistance = new IdDistance(1L, 5000);

        when(observationRepository.searchByDistance(latitude, longitude, 15000L))
            .thenReturn(List.of(idDistance));
        when(observationRepository.findById(1L))
            .thenReturn(Optional.of(observation));

        Optional<Observation> result = meteoManager.getLastNearby(latitude, longitude);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(observationRepository).searchByDistance(latitude, longitude, 15000L);
        verify(observationRepository).findById(1L);
    }

    @Test
    void testGetLastNearbyWithInvalidLatitudeTooLarge() {
        Optional<Observation> result = meteoManager.getLastNearby(91, 0);
        assertTrue(result.isEmpty());
        verify(observationRepository, never()).searchByDistance(anyDouble(), anyDouble(), anyLong());
    }

    @Test
    void testGetLastNearbyWithInvalidLatitudeTooSmall() {
        Optional<Observation> result = meteoManager.getLastNearby(-91, 0);
        assertTrue(result.isEmpty());
        verify(observationRepository, never()).searchByDistance(anyDouble(), anyDouble(), anyLong());
    }

    @Test
    void testGetLastNearbyWithInvalidLongitudeTooLarge() {
        Optional<Observation> result = meteoManager.getLastNearby(0, 181);
        assertTrue(result.isEmpty());
        verify(observationRepository, never()).searchByDistance(anyDouble(), anyDouble(), anyLong());
    }

    @Test
    void testGetLastNearbyWithInvalidLongitudeTooSmall() {
        Optional<Observation> result = meteoManager.getLastNearby(0, -181);
        assertTrue(result.isEmpty());
        verify(observationRepository, never()).searchByDistance(anyDouble(), anyDouble(), anyLong());
    }

    @Test
    void testGetLastNearbyNoObservationsFound() {
        double latitude = -33.4567;
        double longitude = -70.6543;

        when(observationRepository.searchByDistance(latitude, longitude, 15000L))
            .thenReturn(Collections.emptyList());

        Optional<Observation> result = meteoManager.getLastNearby(latitude, longitude);

        assertTrue(result.isEmpty());
        verify(observationRepository).searchByDistance(latitude, longitude, 15000L);
        verify(observationRepository, never()).findById(anyLong());
    }

    @Test
    void testGetLastNearbyObservationNotFound() {
        double latitude = -33.4567;
        double longitude = -70.6543;

        IdDistance idDistance = new IdDistance(1L, 5000);

        when(observationRepository.searchByDistance(latitude, longitude, 15000L))
            .thenReturn(List.of(idDistance));
        when(observationRepository.findById(1L))
            .thenReturn(Optional.empty());

        Optional<Observation> result = meteoManager.getLastNearby(latitude, longitude);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLastNearbyWithMultipleObservations() {
        double latitude = -33.4567;
        double longitude = -70.6543;

        Station station = new Station();
        station.setId(1L);

        Observation observation = new Observation();
        observation.setId(2L);
        observation.setStation(station);

        IdDistance id1 = new IdDistance(1L, 3000);
        IdDistance id2 = new IdDistance(2L, 5000);

        when(observationRepository.searchByDistance(latitude, longitude, 15000L))
            .thenReturn(List.of(id1, id2));
        when(observationRepository.findById(1L))
            .thenReturn(Optional.of(observation));

        Optional<Observation> result = meteoManager.getLastNearby(latitude, longitude);

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());
    }

    @Test
    void testGetLastNearbyWithChileanCoordinates() {
        Station station = new Station();
        station.setId(1L);

        Observation observation = new Observation();
        observation.setId(1L);
        observation.setStation(station);

        IdDistance idDistance = new IdDistance(1L, 100);

        when(observationRepository.searchByDistance(-33.8668, -70.1693, 15000))
            .thenReturn(List.of(idDistance));
        when(observationRepository.findById(1L))
            .thenReturn(Optional.of(observation));

        Optional<Observation> result = meteoManager.getLastNearby(-33.8668, -70.1693);

        assertTrue(result.isPresent());
    }
}
