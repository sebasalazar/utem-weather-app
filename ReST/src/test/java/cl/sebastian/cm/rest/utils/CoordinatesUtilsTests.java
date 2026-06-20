package cl.sebastian.cm.rest.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesUtilsTests {

    @Test
    void testConstructorThrowsException() throws Exception {
        Constructor<CoordinatesUtils> constructor = CoordinatesUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    void testValidCoordinates() {
        assertTrue(CoordinatesUtils.areValid(0, 0));
        assertTrue(CoordinatesUtils.areValid(-33.4, -70.6));
        assertTrue(CoordinatesUtils.areValid(90, 180));
        assertTrue(CoordinatesUtils.areValid(-90, -180));
    }

    @Test
    void testBoundaryLatitudes() {
        assertTrue(CoordinatesUtils.areValid(-90.0, 0));
        assertTrue(CoordinatesUtils.areValid(90.0, 0));
    }

    @Test
    void testBoundaryLongitudes() {
        assertTrue(CoordinatesUtils.areValid(0, -180.0));
        assertTrue(CoordinatesUtils.areValid(0, 180.0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-90.1, -91, -100, -180})
    void testInvalidLatitudeTooSmall(double lat) {
        assertFalse(CoordinatesUtils.areValid(lat, 0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {90.1, 91, 100, 180})
    void testInvalidLatitudeTooLarge(double lat) {
        assertFalse(CoordinatesUtils.areValid(lat, 0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-180.1, -181, -200, -360})
    void testInvalidLongitudeTooSmall(double lon) {
        assertFalse(CoordinatesUtils.areValid(0, lon));
    }

    @ParameterizedTest
    @ValueSource(doubles = {180.1, 181, 200, 360})
    void testInvalidLongitudeTooLarge(double lon) {
        assertFalse(CoordinatesUtils.areValid(0, lon));
    }

    @Test
    void testInvalidBothCoordinates() {
        assertFalse(CoordinatesUtils.areValid(91, 181));
        assertFalse(CoordinatesUtils.areValid(-91, -181));
        assertFalse(CoordinatesUtils.areValid(100, -200));
    }

    @Test
    void testChileanCoordinates() {
        assertTrue(CoordinatesUtils.areValid(-33.8668, -70.1693));
        assertTrue(CoordinatesUtils.areValid(-23.6345, -70.3977));
        assertTrue(CoordinatesUtils.areValid(-41.4667, -72.9333));
    }

}
