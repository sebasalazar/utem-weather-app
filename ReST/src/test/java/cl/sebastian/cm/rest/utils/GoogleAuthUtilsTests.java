package cl.sebastian.cm.rest.utils;

import cl.sebastian.cm.rest.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class GoogleAuthUtilsTests {

    @Test
    void testConstructorThrowsException() throws Exception {
        Constructor<GoogleAuthUtils> constructor = GoogleAuthUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    void testGetEmailWithBlankToken() {
        assertThrows(ValidationException.class, () -> GoogleAuthUtils.getEmail(null));
        assertThrows(ValidationException.class, () -> GoogleAuthUtils.getEmail(""));
        assertThrows(ValidationException.class, () -> GoogleAuthUtils.getEmail("   "));
    }
}
