package space.huttka.androidthings.driver.r300;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_BADPACKET;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_COMMANDPACKET;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_OK;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_PACKETRECIEVEERR;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_PASSFAIL;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_SETPASSWORD;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_VERIFYPASSWORD;

/**
 * @author leon0399
 */
@SuppressWarnings("WeakerAccess")
public class R300Module implements AutoCloseable {
    private static final String TAG = "R300Module";

    private UartDevice mDevice;
    private int mPassword;

    protected R300Module(String uartName, int password, int baudRate) throws IOException {
        this(uartName, baudRate, password, null);
    }

    protected R300Module(String uartName, int password, int baudRate, Handler handler) throws IOException {
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            UartDevice device = manager.openUartDevice(uartName);
            init(device, baudRate, password, handler);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public static String byteArrayToHexString(final byte[] bytes) { //todo: delete this crap
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("0x%02x ", b & 0xff));
        }
        return sb.toString();
    }

    public void init(UartDevice device, int baudRate, int password, Handler handler) throws IOException {
        mPassword = password;

        mDevice = device;
        mDevice.setBaudrate(baudRate);
        mDevice.setDataSize(8);
        mDevice.setParity(UartDevice.PARITY_NONE);
        mDevice.setStopBits(1);
    }

    @Override
    public void close() throws IOException {
        if (mDevice != null) {
            try {
                mDevice.close();
            } finally {
                mDevice = null;
            }
        }
    }

    public int VfyPwd() {
        try {
            R300Packet packet = getPacket(
                    FINGERPRINT_VERIFYPASSWORD,
                    (byte) ((mPassword >> 24) & 0xFF),
                    (byte) ((mPassword >> 16) & 0xFF),
                    (byte) ((mPassword >> 8) & 0xFF),
                    (byte) (mPassword & 0xFF)
            );

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

    public int SetPwd(int password) {
        try {
            R300Packet packet = getPacket(
                    FINGERPRINT_SETPASSWORD,
                    (byte) ((password >> 24) & 0xFF),
                    (byte) ((password >> 16) & 0xFF),
                    (byte) ((password >> 8) & 0xFF),
                    (byte) (password & 0xFF)
            );

            if (packet.data[0] == FINGERPRINT_OK) {
                mPassword = password;
                return FINGERPRINT_OK;
            } else {
                return FINGERPRINT_PACKETRECIEVEERR;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error setting password: ", e);
            return FINGERPRINT_PACKETRECIEVEERR;
        }
    }

    private R300Packet getPacket(byte... data) throws IOException {
        writeStructuredPacket(new R300Packet(FINGERPRINT_COMMANDPACKET, data));
        R300Packet packet = new R300Packet();
        readStructuredPacket(packet);
        return packet;
    }

    private void writeStructuredPacket(R300Packet packet) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        packet.write(byteArrayOutputStream);
        try {
            mDevice.write(byteArrayOutputStream.toByteArray(), byteArrayOutputStream.size());
            Log.d(TAG, String.format("Written to module: %s", byteArrayToHexString(byteArrayOutputStream.toByteArray())));
        } finally {
            byteArrayOutputStream.close();
        }
    }

    private int readStructuredPacket(R300Packet packet) {
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
                                return FINGERPRINT_OK;
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
        return FINGERPRINT_BADPACKET;
    }
}
