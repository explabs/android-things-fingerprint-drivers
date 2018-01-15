package space.huttka.androidthings.driver.r300;

import java.io.IOException;

import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_OK;

/**
 * Driver to interact with R300 series and ZFM-20 series Fingerprint Scanner.
 *
 * @author leon0399
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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

    /**
     * Initializes R300 with the configured UART port, address and password.
     *
     * @param uartPort UART port name where the module is attached. Cannot be null.
     * @throws IOException if the hardware board had a problem with its hardware ports
     */
    public R300Driver(String uartPort) throws IOException {
        this(uartPort, DEFAULT_ADDER, DEFAULT_PASS);
    }

    /**
     * Initializes R300 with the configured UART port, address and password.
     *
     * @param uartPort UART port name where the module is attached. Cannot be null.
     * @param address  Address of module.
     * @param password Password of module.
     * @throws IOException if the hardware board had a problem with its hardware ports
     */
    public R300Driver(String uartPort, int address, int password) throws IOException {
        this(uartPort, intToBytes(address), intToBytes(password));
    }

    /**
     * Initializes R300 with the configured UART port, address and password.
     *
     * @param uartPort UART port name where the module is attached. Cannot be null.
     * @param address  Address of module.
     * @param password Password of module.
     * @throws IOException if the hardware board had a problem with its hardware ports
     */
    public R300Driver(String uartPort, byte[] address, byte[] password) throws IOException {
        this.module = new R300Module(uartPort, address, password);

        if (!verifyPassword()) {
            throw new IOException("Module not connected of wrong password!");
        }
    }

    /**
     * Transforms integer to array of unsigned bytes.
     *
     * @param i Integer to be transformed.
     * @return Array of unsigned bytes. Higher bit first.
     */
    public static byte[] intToBytes(int i) {
        return new byte[]{(byte) ((i >> 24) & 0xFF), (byte) ((i >> 16) & 0xFF), (byte) ((i >> 8) & 0xFF), (byte) (i & 0xFF)};
    }

    /**
     * Transforms array of unsigned bytes to integer.
     *
     * @param bytes Array of unsigned bytes. Higher bit first.
     * @return Transformed Integer.
     */
    public static int bytesToInt(byte[] bytes) {
        switch (bytes.length) {
            case 2:
                return (bytes[0] << 8) | bytes[1];
            case 3:
                return (bytes[0] << 16) | (bytes[1] << 8) | bytes[2];
            case 4:
                return (bytes[0] << 24) | (bytes[2] << 16) | bytes[3];
        }
        return bytes[0];

    }

    @Override
    public void close() throws Exception {
        this.module.close();
    }

    /**
     * Verify Module’s handshaking password.
     *
     * @return true if password is correct
     */
    public boolean verifyPassword() {
        return module.VfyPwd() == FINGERPRINT_OK;
    }

    /**
     * <b>Warning!</b> Avoid using! Password will not be saved!
     * <p>
     * Set Module’s handshaking password.
     *
     * @param password Password to be set
     * @return true if password setting completed
     */
    public boolean setPassword(int password) {
        return this.setPassword(intToBytes(password));
    }

    /**
     * <b>Warning!</b> Avoid using! Password will not be saved!
     * <p>
     * Set Module’s handshaking password.
     *
     * @param password Password to be set
     * @return true if password setting completed
     */
    public boolean setPassword(byte[] password) {
        return module.SetPwd(password) == FINGERPRINT_OK;
    }

    /**
     * <b>Warning!</b> Avoid using! Address will not be saved!
     * <p>
     * Set Module’s handshaking password.
     *
     * @param address Password to be set
     * @return true if address setting completed
     */
    public boolean setAddress(int address) {
        return this.setAddress(intToBytes(address));
    }

    /**
     * <b>Warning!</b> Avoid using! Address will not be saved!
     * <p>
     * Set Module’s handshaking password.
     *
     * @param address Password to be set
     * @return true if address setting completed
     */
    public boolean setAddress(byte[] address) {
        return module.SetAdder(address) == FINGERPRINT_OK;
    }

    /**
     * @return
     */
    public int getTemplateNum() {
        return bytesToInt(this.module.TempleteNum());
    }

    public int getRandomCode(){
        return bytesToInt(this.module.GetRandomCode());
    }
}
