package njit.mt.sensors;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    List<Sensor> sensorList;
    HashMap<String,String> sensorData = new HashMap<String, String>();
    TextView text;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

         sensorList.forEach((sensor)->{
             Log.v("Sensor",sensor.getName());
             sensorData.put(sensor.getName(), "");
         });
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public final void onSensorChanged(SensorEvent event) {
       // Log.v("Sensor", event.sensor.getName()); //uncomment with caution, it's spammy
        // Do something with this sensor data.
        if(sensorData.containsKey(event.sensor.getName())){
            StringBuilder sb = new StringBuilder();
            for(int i = 0, l = event.values.length; i < l; i++){
                sb.append((i>0?",":"")+String.format("%.2f", event.values[i]));
            }

            sensorData.put(event.sensor.getName(), sb.toString());

            sb = new StringBuilder();
            StringBuilder finalSb = sb;
            sensorData.forEach((t, v)->{
                finalSb.append(t + " " + v + System.lineSeparator());
            });
            text.setText(finalSb.toString());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        if(sensorList != null){
            sensorList.forEach((sensor)->{
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            });
        }

    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}