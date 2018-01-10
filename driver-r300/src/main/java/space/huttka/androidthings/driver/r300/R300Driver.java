package space.huttka.androidthings.driver.r300;

import android.content.Context;
import android.os.Handler;

import java.io.IOException;

/**
 * @author leon0399
 */
@SuppressWarnings("WeakerAccess")
public class R300Driver implements AutoCloseable {

    private final R300Module module;

    /**
     * @param context        Current context, used for loading resources
     * @param uartName       UART port name where the module is attached. Cannot be null.
     * @param touchName
     * @param touchPowerName
     * @param baudRate       Baud rate used for the module UART.
     * @param password
     */
    public R300Driver(Context context, String uartName, String touchName, String touchPowerName, int baudRate, int password) throws IOException {
        this(context, uartName, touchName, touchPowerName, baudRate, password, null);
    }

    /**
     * @param context        Current context, used for loading resources
     * @param uartName       UART port name where the module is attached. Cannot be null.
     * @param touchName
     * @param touchPowerName
     * @param baudRate       Baud rate used for the module UART.
     * @param password
     * @param handler        optional {@link Handler} for software polling and callback events.
     */
    public R300Driver(Context context, String uartName, String touchName, String touchPowerName, int baudRate, int password, Handler handler) throws IOException {
        this.module = new R300Module(uartName, baudRate, password, handler);
    }

    @Override
    public void close() throws Exception {
        this.module.close();
    }
}
