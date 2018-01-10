package space.huttka.androidthings.driver.r300;

import android.os.Handler;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_COMMANDPACKET;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_OK;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_PACKETRECIEVEERR;
import static space.huttka.androidthings.driver.r300.R300Packet.FINGERPRINT_VERIFYPASSWORD;

/**
 * @author leon0399
 */

public class R300Module implements AutoCloseable {
    private static final String TAG = "R300Module";

    private UartDevice mDevice;
    private int mPassword;

    public R300Module(String uartName, int baudRate, int password) throws IOException {
        this(uartName, baudRate, password, null);
    }

    public R300Module(String uartName, int baudRate, int password, Handler handler) throws IOException {
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            UartDevice device = manager.openUartDevice(uartName);
            init(device, baudRate, password, handler);
        } catch (IOException | RuntimeException e) {
            close();
            throw e;
        }
    }

    public void init(UartDevice device, int baudRate, int password, Handler handler) throws IOException {
        mPassword = password;

        mDevice = device;
        mDevice.setBaudrate(baudRate);

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

    public boolean verifyPassword() {
        return checkPassword() == FINGERPRINT_OK;
    }

    private int checkPassword() {
        try {
            if (getPacket(FINGERPRINT_VERIFYPASSWORD, (byte) (mPassword >> 24), (byte) ((mPassword >> 16) & 0xFF), (byte) ((mPassword >> 8) & 0xFF), (byte) (mPassword & 0xFF)).data[0] == FINGERPRINT_OK)
                return FINGERPRINT_OK;
            else
                return FINGERPRINT_PACKETRECIEVEERR;

        } catch (IOException e) {
            return FINGERPRINT_PACKETRECIEVEERR;
        }
    }

    private R300Packet getPacket(byte... data) throws IOException {
        writeStructuredPacket(new R300Packet(FINGERPRINT_COMMANDPACKET, data));
        return readStructuredPacket();
    }

    private void writeStructuredPacket(R300Packet packet) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        packet.write(baos);
        mDevice.write(baos.toByteArray(), baos.size());
    }

    private R300Packet readStructuredPacket() throws IOException {
        byte[] buf = new byte[1];
        int index = 0;

        while (true) {
            mDevice.read(buf, 1);

        }

    }
}
