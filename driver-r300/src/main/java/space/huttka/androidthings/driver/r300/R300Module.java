package space.huttka.androidthings.driver.r300;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_COMMANDPACKET;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_OK;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_PACKETRECIEVEERR;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_PASSFAIL;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_SETADDRESS;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_SETPASSWORD;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_VERIFYPASSWORD;

/**
 * @author leon0399
 */
@SuppressWarnings("WeakerAccess")
public class R300Module implements AutoCloseable {
    public static final int DEFAULT_BAUDRATE = 57600;

    private static final String TAG = "R300Module";

    private UartDevice mDevice;

    private byte[] mAddress;
    private byte[] mPassword;

    protected R300Module(String uartPort, byte[] address, byte[] password) throws IOException {
        try {
            PeripheralManagerService peripheralManagerService = new PeripheralManagerService();
            this.mDevice = peripheralManagerService.openUartDevice(uartPort);
            this.mDevice.setBaudrate(DEFAULT_BAUDRATE);
            this.mDevice.setDataSize(8);
            this.mDevice.setStopBits(1);
            this.mDevice.setParity(UartDevice.PARITY_NONE);
        } catch (IOException e) {
            Log.e(TAG, "Error initializing UART Device", e);
            close();
            throw e;
        }

        this.mAddress = address;
        this.mPassword = password;
    }

    public static String byteArrayToHexString(final byte[] bytes) { //todo: delete this crap
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("0x%02x ", b & 0xff));
        }
        return sb.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.mDevice != null) {
            try {
                this.mDevice.close();
            } finally {
                this.mDevice = null;
            }
        }
    }

    public int VfyPwd() {
        try {
            R300Packet packet = getPacket(FINGERPRINT_VERIFYPASSWORD, this.mPassword);

            if (packet.data[0] == FINGERPRINT_OK)
                return FINGERPRINT_OK;
            else if (packet.data[0] == FINGERPRINT_PASSFAIL)
                return FINGERPRINT_PASSFAIL;
            else
                return FINGERPRINT_PACKETRECIEVEERR;
        } catch (IOException e) {
            Log.e(TAG, "Error checking password: ", e);
            return FINGERPRINT_PACKETRECIEVEERR;
        }
    }

    public int SetPwd(byte[] password) {
        try {
            R300Packet packet = getPacket(FINGERPRINT_SETPASSWORD, password);

            if (packet.data[0] == FINGERPRINT_OK) {
                this.mPassword = password;
                return FINGERPRINT_OK;
            } else {
                return FINGERPRINT_PACKETRECIEVEERR;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error setting password: ", e);
            return FINGERPRINT_PACKETRECIEVEERR;
        }
    }

    public int SetAdder(byte[] adder) {
        try {
            R300Packet packet = getPacket(FINGERPRINT_SETADDRESS, adder);

            if (packet.data[0] == FINGERPRINT_OK) {
                this.mAddress = adder;
                return FINGERPRINT_OK;
            } else {
                return FINGERPRINT_PACKETRECIEVEERR;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error setting password: ", e);
            return FINGERPRINT_PACKETRECIEVEERR;
        }
    }

    private R300Packet getPacket(byte instruction, byte[] data) throws IOException {
        writeCommand(instruction, data);
        return readStructuredPacket();
    }

    private void writeCommand(byte instruction, byte[] data) throws IOException {
        byte[] dataFull = new byte[data.length + 1];
        dataFull[0] = instruction;
        System.arraycopy(data, 0, dataFull, 1, data.length);
        writePacket(new R300Packet(this.mAddress, FINGERPRINT_COMMANDPACKET, dataFull));
    }

    private void writePacket(R300Packet packet) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        packet.write(byteArrayOutputStream);
        try {
            mDevice.write(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.size());
            Log.d(TAG, String.format("Written to module: %s", byteArrayToHexString(byteArrayOutputStream.toByteArray())));
        } finally {
            byteArrayOutputStream.close();
        }
    }

    private R300Packet readStructuredPacket() {
        R300Packet packet = new R300Packet();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int index = 0;
        int wireLength = 0;

        try {
            byte[] read = new byte[1];
            byte buf;

            while (true) {
                if (mDevice.read(read, 1) > 0) {
                    buf = read[0];
                    byteArrayOutputStream.write(read);

                    switch (index) {
                        case 0: // Start code 1
                            if (buf != R300Packet.FINGERPRINT_STARTCODE_1)
                                continue;
                            packet.header[index] = buf;
                            break;
                        case 1: // Start code 2
                            if (buf != R300Packet.FINGERPRINT_STARTCODE_2)
                                continue;
                            packet.header[index] = buf;
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            packet.adder[(index - 2)] = buf;
                            break;
                        case 6:
                            packet.pid = buf;
                            break;
                        case 7:
                        case 8:
                            packet.length[(index - 7)] = buf;
                            wireLength = (packet.length[0] << 8) | packet.length[1];
                            break;
                        default:
                            packet.data[(index - 9)] = buf;
                            if ((index - 8) >= wireLength) {
                                Log.d(TAG, String.format("Read from module: %s", byteArrayToHexString(byteArrayOutputStream.toByteArray())));
                                return packet;
                            }
                            break;
                    }

                    index++;
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Unable to transfer data over UART", e);
        }

        // Shouldn't get here so...
        return null;
    }
}
