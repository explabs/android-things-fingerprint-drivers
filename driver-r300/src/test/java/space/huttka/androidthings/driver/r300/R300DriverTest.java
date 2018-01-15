package space.huttka.androidthings.driver.r300;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author leon0399
 */
class R300DriverTest {
    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void intToBytes() {
        final byte[] reference = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        final byte[] result = R300Driver.intToBytes(0xffffffff);
        assertArrayEquals(reference, result);
    }

}