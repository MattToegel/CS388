package njit.mt.followme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import njit.mt.followme.db.entity.ScoreEntity;
import njit.mt.followme.db.viewmodel.ScoreViewModel;
import njit.mt.followme.db.viewmodel.ScoreViewModelFactory;

public class MainActivity extends AppCompatActivity {
    TextView scoreText, roundText;
    LinearLayout buttonContainer;
    GridLayout grid;
    final static int totalButtons = 9;
    final static int columns = 3;
    int score = 0;
    int round = 0;
    final int REWARD = 10;
    final String TAG = "FollowMe";
    final int MARGIN = 5;
    final int BEGINNING_PATTERN = 2;
    boolean canPick = false;
    int currentIndex = -1;
    List<Integer> pattern = new ArrayList<Integer>();
    ExecutorService executor = Executors.newFixedThreadPool(10);

    private ScoreViewModel scoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        buttonContainer = findViewById(R.id.buttonContainer);
        grid = findViewById(R.id.grid);
        scoreText = findViewById(R.id.scoreText);
        roundText = findViewById(R.id.roundText);

        roundText.getText().hashCode()
        buildGrid();
        resetButtons();
        updateUI();

        newGame();
    }
    //https://developer.android.com/training/appbar/action-views
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.game:
                // do something
                intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.scores:
                // do something
                intent = new Intent(MainActivity.this, ScoreActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    void resetButtons() {
        for (int i = 0; i < totalButtons; i++) {
            ((Button) findViewById(i)).getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }

    //http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog.html
    void promptUser(String title, String message, boolean expectsInput, Consumer callback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(message);
        EditText input = null;
        if (expectsInput) {
            // Set an EditText view to get user input
            input = new EditText(this);
            alert.setView(input);
        }

        EditText finalInput = input;
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = "-";
                if (expectsInput) {
                    value = finalInput.getText().toString();
                    Log.v(TAG, "WE got it!!! " + value);
                    // Do something with value!
                }
                if (callback != null) {
                    callback.accept(value);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                Log.v(TAG, "They want out");
                if (callback != null) {
                    callback.accept(null);
                }
            }
        });

        alert.show();
    }
    public void onClickStartGame(View view){
        if(round == 0){
            nextRound();
        }
    }
    void newGame() {
        score = 0;
        round = 0;
        pattern.clear();
        for (int i = 0; i < BEGINNING_PATTERN; i++) {
            appendPatternItem();
        }
        updateUI();
        grid.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.VISIBLE);
        /*promptUser("Welcome", "Follow the pattern and repeat until...you can't....ready?", false, (x) -> {
            nextRound();
        });*/
    }

    void nextRound() {
        if(round == 0){
            grid.setVisibility(View.VISIBLE);
            buttonContainer.setVisibility(View.GONE);
        }
        Log.v(TAG, "nextRound()");
        round++;
        currentIndex = 0;
        updateUI();
        appendPatternItem(); // first round this will be number 3
        executor.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < pattern.size(); i++) {
                int finalI = i;

                runOnUiThread(() -> {
                    Log.v(TAG, "checking " + finalI);
                    ((Button) findViewById(pattern.get(finalI))).getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                });
                try {
                    TimeUnit.MILLISECONDS.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    ((Button) findViewById(pattern.get(finalI))).getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                });
            }
            canPick = true;
        });
    }

    void appendPatternItem() {
        pattern.add(getRandom(0, totalButtons - 1));
    }

    //https://mkyong.com/java/java-generate-random-integers-in-a-range/
    private static int getRandom(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    void updateUI() {
        scoreText.setText("Score: " + score);
        roundText.setText("Round: " + round);
    }

    void buildGrid() {
        for (int i = 0; i < totalButtons; i++) {
            ContextThemeWrapper newContext = new ContextThemeWrapper(
                    this,
                    R.style.Button_White
            );

            Button btnTag = new Button(newContext);
            btnTag.setText("Button" + i);
            btnTag.setId(i);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;

            param.rightMargin = MARGIN;
            param.topMargin = MARGIN;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(i / columns);
            param.rowSpec = GridLayout.spec(i % columns);
            btnTag.setLayoutParams(param);
            btnTag.setOnClickListener(this::onClickityClackity);
            //add button to the layout
            grid.addView(btnTag);
        }
    }
    void saveScore(String name, int score){
        if(scoreViewModel == null){
            scoreViewModel = new ViewModelProvider(this, new ScoreViewModelFactory(getApplication())).get(ScoreViewModel.class);
        }
        ScoreEntity user = new ScoreEntity(name, score);
        scoreViewModel.insert(user);
    }
    void onClickityClackity(View view) {
        if (!canPick) {
            return;
        }
        Log.v(TAG, "clicked " + view.getId());
        final int id = view.getId();
        if (currentIndex < pattern.size()) {
            if (pattern.get(currentIndex) == id) {
                Log.v(TAG, "Correct");
                score += REWARD;
            } else {
                Log.v(TAG, "Incorrect");
                canPick = false;
                promptUser("You lose", "Type in your name to record your score", true, (x) -> {
                    //TODO actually save score/name but this happens in the dialog
                    //using null value for a canceled prompt, otherwise will be a string
                    if(x != null) {
                        Log.v(TAG, x.toString());
                        final int nScore = score;
                        saveScore(x.toString(), nScore);
                    }
                    newGame();
                });
            }
            currentIndex++;
        }
        if (currentIndex >= pattern.size()) {
            //next round
            canPick = false;
            nextRound();
        }
        updateUI();
    }
}