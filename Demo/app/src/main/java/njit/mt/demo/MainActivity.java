package njit.mt.demo;

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
    int clicks = 0;
    TextView text, timeText, bestText;
    Button button;
    ScheduledExecutorService service;
    boolean isPlaying = false;
    ScheduledFuture timer = null;
    int time = 10;
    int startTime = 10;
    int best = 0;


    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById((R.id.text));
        timeText = (TextView) findViewById((R.id.time));
        bestText = (TextView) findViewById(R.id.best);
        button = (Button)findViewById(R.id.button);
        service = Executors.newScheduledThreadPool(1);

        best = sharedPreferences.getInt("best", 0);
        bestText.setText("Best Score: " + best);
    }

    public void onClick(View view) {
        if (!isPlaying) {
            clicks = 0;
            time = startTime;
            startTimer();
            isPlaying = true;
        }
        else {
            clicks++;
            text.setText("Clicks " + clicks);// view in the text
        }
    }

    void startTimer() {
        Log.v("Demo", "start timer called");
        Log.v("Demo", "started counter");
        timer = service.scheduleAtFixedRate(() -> {
            try {
                runOnUiThread(() -> {
                    timeText.setText("Time remaining: " + time + " seconds");
                });

                Log.v("Demo", "Tick: " + time);
                time--;
                if (time <= 0) {
                    Log.v("Demo", "Timer expired");
                    runOnUiThread(() -> {
                        timeText.setText("Time Expired!");
                        if(clicks > best){
                            best = clicks;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("best", best);
                            editor.commit();
                            bestText.setText("Best Score: " + best);
                        }

                    });
                    if (timer != null) {
                        timer.cancel(true);
                    }

                    runOnUiThread(()->{
                        button.setEnabled(false);
                    });
                    service.schedule(()-> runOnUiThread(() -> {
                        isPlaying = false;
                        button.setEnabled(true);
                    }), 3, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                Log.e("Demo", e.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

    }
}