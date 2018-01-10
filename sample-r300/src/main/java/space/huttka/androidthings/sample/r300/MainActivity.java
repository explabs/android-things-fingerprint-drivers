package space.huttka.androidthings.sample.r300;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import space.huttka.androidthings.driver.r300.R300Driver;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    R300Driver r300Driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            r300Driver = new R300Driver(this, BoardDefaults.getUartName(), BoardDefaults.getTouchGpioPin(), BoardDefaults.getTouchPowerGpioPin(), 115200, 0x000000);
            Log.d(TAG, "Initialized R300 Driver");
        } catch (IOException e) {
            throw new RuntimeException("Error initializing R300 Driver", e);
        }
    }
}
