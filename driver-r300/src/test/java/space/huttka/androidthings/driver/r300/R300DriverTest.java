package space.huttka.androidthings.driver.r300;

import android.util.Log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author leon0399
 */
class R300DriverTest {

    private static final String TAG = "R300DriverTest";

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

    @Test
    void bytesToInt() {
        final int reference = 0xffffffff;
        final byte[] bytes = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        final int result = R300Driver.bytesToInt(bytes);

        assertEquals(reference, result);
    }

    @ParameterizedTest
    @ValueSource(ints = {0xffffffff, 0xfafafafa, 0x4215f1ee, 0x12fe32ab})
    void parameterizedTest(int value) {
        assertAll(() -> {
            final byte[] bytes = R300Driver.intToBytes(value);
            final int result = R300Driver.bytesToInt(bytes);

            System.out.println(R300Module.byteArrayToHexString(bytes));

            // assertEquals(value, result);
            assertArrayEquals(bytes, R300Driver.intToBytes(result));
        });
    }

}