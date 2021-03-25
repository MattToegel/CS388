package njit.mt.followmesandbox;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    GridLayout grid;
    List<Integer> pattern = new ArrayList<Integer>();
    int currentIndex = 0;
    int currentRound = -1;
    final int totalButtons = 9;
    final int reward = 10;
    final static String TAG = "MainActivity";
    boolean canPick = false;
    int score = 0;
    ExecutorService executor = Executors.newFixedThreadPool(10);
    TextView scoreText, roundText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid = findViewById(R.id.grid);
        scoreText = findViewById(R.id.scoretext);
        roundText = findViewById(R.id.roundText);
        buildGrid();
        pattern.add(getRandom(0, totalButtons-1));
        pattern.add(getRandom(0, totalButtons-1));
        pattern.add(getRandom(0, totalButtons-1));
        nextRound();
    }
    void updateUI(){
        scoreText.setText("Score: " + score);
        roundText.setText("Round: " + (currentRound+1));
    }
    void buildGrid(){
        for(int i = 0; i < totalButtons; i++){
            Button btnTag = new Button(this);
           // btnTag.setLayoutParams(new GridLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText("Button" + i);
            btnTag.setId(i);
            btnTag.setLayoutParams(new GridLayout.LayoutParams());
            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rightMargin = 5;
            param.topMargin = 5;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(i/3);
            param.rowSpec = GridLayout.spec(i%3);
            btnTag.setLayoutParams (param);
            btnTag.setOnClickListener(this::onClickityClackity);
            //add button to the layout
            grid.addView(btnTag);
        }
    }
    void nextRound(){
        Log.v(TAG, "nextRound()");
        currentRound++;
        updateUI();
        int next = getRandom(0, totalButtons-1);
        pattern.add(next);
        executor.submit(()->{
            for(int i = 0; i < pattern.size(); i++){
                int finalI = i;
                runOnUiThread(()->{
                    Log.v(TAG, "checking " + finalI);
                    ((Button)findViewById(pattern.get(finalI))).getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                });
                try {
                    TimeUnit.MILLISECONDS.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(()->{
                    ((Button)findViewById(pattern.get(finalI))).getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                });
            }
            canPick = true;
        });
    }
    //https://mkyong.com/java/java-generate-random-integers-in-a-range/
    private static int getRandom(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return (int)(Math.random() * ((max - min) + 1)) + min;
    }
    void onClickityClackity(View view){
        if(!canPick){
            return;
        }
        Log.v(TAG, "clicked " + view.getId());
        final int id = view.getId();
        if(currentIndex < pattern.size()){
            if(pattern.get(currentIndex) == id){
                Log.v(TAG, "Correct");
                score += reward;
            }
            else{
                Log.v(TAG, "Incorrect");
                canPick = false;
            }
        }
        else{
            //next round
            canPick = false;
        }
        updateUI();
        currentIndex++;
    }
}