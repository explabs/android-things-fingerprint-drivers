package space.huttka.androidthings.driver.r301t;

import android.os.Handler;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static space.huttka.androidthings.driver.r301t.R301tPacket.FINGERPRINT_COMMANDPACKET;
import static space.huttka.androidthings.driver.r301t.R301tPacket.FINGERPRINT_OK;
import static space.huttka.androidthings.driver.r301t.R301tPacket.FINGERPRINT_PACKETRECIEVEERR;
import static space.huttka.androidthings.driver.r301t.R301tPacket.FINGERPRINT_VERIFYPASSWORD;

/**
 * @author leon0399
 */

public class R301tModule implements AutoCloseable {
    private static final String TAG = "R301tModule";

    private UartDevice mDevice;
    private int mPassword;

    public R301tModule(String uartName, int baudRate, int password) throws IOException {
        this(uartName, baudRate, password, null);
    }

    public R301tModule(String uartName, int baudRate, int password, Handler handler) throws IOException {
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
            if (getPacket(FINGERPRINT_VERIFYPASSWORD, (byte) (mPassword >> 24), (byte) (mPassword >> 16), (byte) (mPassword >> 8), (byte) (mPassword & 0xFF)).data[0] == FINGERPRINT_OK)
                return FINGERPRINT_OK;
            else
                return FINGERPRINT_PACKETRECIEVEERR;

        } catch (IOException e) {
            return FINGERPRINT_PACKETRECIEVEERR;
        }
    }

    private R301tPacket getPacket(byte... data) throws IOException {
        R301tPacket packet = new R301tPacket(FINGERPRINT_COMMANDPACKET, (char) data.length, data);
        return readStructuredPacket();
    }

    private void writeStructuredPacket(R301tPacket packet) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeChar(packet.start_code);
        dos.writeInt(packet.address);
        dos.writeByte(packet.type);

        char wireLength = (char) (packet.length + 2);
        dos.writeChar(wireLength);

        int sum = ((wireLength)>>8) + ((wireLength)&0xFF) + packet.type;
        for (byte i=0; i < packet.length; i++) {
            dos.writeByte(packet.data[i]);
            sum += packet.data[i];
        }
        dos.writeInt(sum);

        mDevice.write(baos.toByteArray(), baos.size());

        // mDevice.write(new byte[]{(byte) (packet.address >> 8), (byte) (packet.address & 0xFF)}, 2);
    }

    private R301tPacket readStructuredPacket() throws IOException {
        byte[] buf = new byte[1];
        int index = 0;

        mDevice.wait();
        while(true) {
            mDevice.read(buf, 1);

        }

    }
}
