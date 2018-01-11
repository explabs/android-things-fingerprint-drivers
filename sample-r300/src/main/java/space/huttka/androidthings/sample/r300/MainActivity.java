package space.huttka.androidthings.sample.r300;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import space.huttka.androidthings.driver.r300.R300Driver;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    R300Driver r300Driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            r300Driver = new R300Driver(this, BoardDefaults.getUartName(), BoardDefaults.getTouchGpioPin(), BoardDefaults.getTouchPowerGpioPin(), 0xFFFFFFFF);
            Log.d(TAG, "Initialized R300 Driver");


            Log.d(TAG, String.format("Verify password: %s", r300Driver.verifyPassword()));
        } catch (IOException e) {
            throw new RuntimeException("Error initializing R300 Driver", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (r300Driver != null) {
            try {
                r300Driver.close();
            } catch (Exception e) {
                throw new RuntimeException("Error destroying R300 Driver", e);
            }
        }
    }
}
