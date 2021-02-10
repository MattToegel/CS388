package njit.mt.clickity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button myButton;
    TextView myText;
    TextView myTimerText;
    TextView bestText;
    int clicks = 0;
    int time = 0;
    int best = 0;
    ScheduledExecutorService service;
    ScheduledFuture timer = null;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton = (Button)findViewById(R.id.button);
        myText = (TextView)findViewById(R.id.textView);
        myTimerText = (TextView)findViewById(R.id.timerText);
        bestText = (TextView)findViewById(R.id.bestText);
        service = Executors.newScheduledThreadPool(1);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        best = sharedPreferences.getInt("best", 0);
        bestText.setText("Best Score: " + best);

    }
    public void onClick(View view) {
        if(timer == null){
            clicks = 0;
            time = 10;
            startTimer();
        }
        clicks++;
        myText.setText("Clicks " + clicks);
    }
    void startTimer(){
        timer = service.scheduleAtFixedRate(() -> {
            try{

                //reduce our time by 1, then check if we expired
                runOnUiThread(() -> {
                    myTimerText.setText("Time remaining: " + time + " seconds");
                    time--;
                });

                if(time <=0){
                    if(clicks > best){
                        best = clicks;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("best", best);
                        editor.apply();
                        runOnUiThread(()->bestText.setText("Best: " + best));
                    }
                    if (timer != null) {
                        timer.cancel(true);
                        timer = null;
                    }
                    runOnUiThread(()->{
                        myButton.setEnabled(false);
                    });
                    service.schedule(()-> runOnUiThread(() -> {
                        myButton.setEnabled(true);
                    }), 3, TimeUnit.SECONDS);
                }
            }
            catch(Exception e){
                    Log.v("Task", e.getMessage());
                e.printStackTrace();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }
}