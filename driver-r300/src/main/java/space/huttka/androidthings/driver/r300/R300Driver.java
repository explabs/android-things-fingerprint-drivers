package space.huttka.androidthings.driver.r300;

import android.content.Context;
import android.os.Handler;

import java.io.IOException;

/**
 * @author leon0399
 */
@SuppressWarnings("WeakerAccess")
public class R300Driver implements AutoCloseable {

    public static final int DEFAULT_BAUDRATE = 57600;

    private final R300Module module;

    /**
     * @param context        Current context, used for loading resources
     * @param uartName       UART port name where the module is attached. Cannot be null.
     * @param touchName
     * @param touchPowerName
     * @param password
     * @throws IOException
     */
    public R300Driver(Context context, String uartName, String touchName, String touchPowerName, int password) throws IOException {
        this(context, uartName, touchName, touchPowerName, password, DEFAULT_BAUDRATE, null);
    }

    /**
     * @param context        Current context, used for loading resources
     * @param uartName       UART port name where the module is attached. Cannot be null.
     * @param touchName
     * @param touchPowerName
     * @param password
     * @param handler        optional {@link Handler} for software polling and callback events.
     * @throws IOException
     */
    public R300Driver(Context context, String uartName, String touchName, String touchPowerName, int password, Handler handler) throws IOException {
        this(context, uartName, touchName, touchPowerName, password, DEFAULT_BAUDRATE, handler);
    }

    /**
     * @param context        Current context, used for loading resources
     * @param uartName       UART port name where the module is attached. Cannot be null.
     * @param touchName
     * @param touchPowerName
     * @param password
     * @param baudRate       Baud rate used for the module UART.
     * @param handler        optional {@link Handler} for software polling and callback events.
     * @throws IOException
     */
    protected R300Driver(Context context, String uartName, String touchName, String touchPowerName, int password, int baudRate, Handler handler) throws IOException {
        this.module = new R300Module(uartName, password, baudRate, handler);
    }

    @Override
    public void close() throws Exception {
        this.module.close();
    }

    public boolean verifyPassword() {
        return module.verifyPassword();
    }
}
