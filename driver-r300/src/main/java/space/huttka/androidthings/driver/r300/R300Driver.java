package space.huttka.androidthings.driver.r300;

import java.io.IOException;

import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_OK;

/**
 * @author leon0399
 */
@SuppressWarnings("WeakerAccess")
public class R300Driver implements AutoCloseable {

    public static final byte DEFAULT_ADDER_1 = (byte) 0xFF;
    public static final byte DEFAULT_ADDER_2 = (byte) 0xFF;
    public static final byte DEFAULT_ADDER_3 = (byte) 0xFF;
    public static final byte DEFAULT_ADDER_4 = (byte) 0xFF;
    public static final byte[] DEFAULT_ADDER = new byte[]{DEFAULT_ADDER_1, DEFAULT_ADDER_2, DEFAULT_ADDER_3, DEFAULT_ADDER_4};

    public static final byte DEFAULT_PASS_1 = (byte) 0xFF;
    public static final byte DEFAULT_PASS_2 = (byte) 0xFF;
    public static final byte DEFAULT_PASS_3 = (byte) 0xFF;
    public static final byte DEFAULT_PASS_4 = (byte) 0xFF;
    public static final byte[] DEFAULT_PASS = new byte[]{DEFAULT_PASS_1, DEFAULT_PASS_2, DEFAULT_PASS_3, DEFAULT_PASS_4};

    private final R300Module module;

    public R300Driver(String uartPort) throws IOException {
        this(uartPort, DEFAULT_ADDER, DEFAULT_PASS);
    }

    public R300Driver(String uartPort, int address, int password) throws IOException {
        this(uartPort, intToBytes(address), intToBytes(password));
    }

    public R300Driver(String uartPort, byte[] address, byte[] password) throws IOException {
        this.module = new R300Module(uartPort, address, password);

        if (!verifyPassword()) {
            throw new IOException("Module not connected of wrong password!");
        }
    }

    private static byte[] intToBytes(int i) {
        return new byte[]{(byte) ((i >> 24) & 0xFF), (byte) ((i >> 16) & 0xFF), (byte) ((i >> 8) & 0xFF), (byte) (i & 0xFF)};
    }

    @Override
    public void close() throws Exception {
        this.module.close();
    }

    public boolean verifyPassword() {
        return module.VfyPwd() == FINGERPRINT_OK;
    }


    /**
     * AVOID USING!
     *
     * @param password
     * @return
     */
    public boolean setPassword(int password) {
        return this.setPassword(intToBytes(password));
    }

    /**
     * AVOID USING!
     *
     * @param password
     * @return
     */
    public boolean setPassword(byte[] password) {
        return module.SetPwd(password) == FINGERPRINT_OK;
    }

    /**
     * AVOID USING!
     *
     * @param address
     * @return
     */
    public boolean setAddress(int address) {
        return this.setAddress(intToBytes(address));
    }

    /**
     * AVOID USING!
     *
     * @param address
     * @return
     */
    public boolean setAddress(byte[] address) {
        return module.SetAdder(address) == FINGERPRINT_OK;
    }
}
