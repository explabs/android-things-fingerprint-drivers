package space.huttka.androidthings.driver.r301t;

import android.content.Context;
import android.os.Handler;

import java.io.IOException;

/**
 *
 *
 * @author leon0399
 */
@SuppressWarnings("WeakerAccess")
public class R301tDriver implements AutoCloseable {

    private final R301tModule module;

    /**
     * @param context Current context, used for loading resources
     * @param uartName UART port name where the module is attached. Cannot be null.
     * @param touchName
     * @param touchPowerName
     * @param baudRate Baud rate used for the module UART.
     * @param password
     */
    public R301tDriver(Context context, String uartName, String touchName, String touchPowerName, int baudRate, int password) throws IOException {
        this(context, uartName, touchName, touchPowerName, baudRate, password,null);
    }

    /**
     * @param context Current context, used for loading resources
     * @param uartName UART port name where the module is attached. Cannot be null.
     * @param touchName
     * @param touchPowerName
     * @param baudRate Baud rate used for the module UART.
     * @param password
     * @param handler optional {@link Handler} for software polling and callback events.
     */
    public R301tDriver(Context context, String uartName, String touchName, String touchPowerName, int baudRate, int password, Handler handler) throws IOException {
        this.module = new R301tModule(uartName, baudRate, password, handler);
    }

    @Override
    public void close() throws Exception {
        this.module.close();
    }
}
