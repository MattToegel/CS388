package njit.mt.whackaquack.ui.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import njit.mt.whackaquack.R;
import njit.mt.whackaquack.data.LoginDataSource;
import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.ScoreDataSource;
import njit.mt.whackaquack.data.ScoreRepository;
import njit.mt.whackaquack.data.model.LoggedInUser;
import njit.mt.whackaquack.data.model.ScoreData;

public class GameFragment extends Fragment {

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private GameViewModel gameViewModel;
    List<List<MotionEvent.PointerCoords>> touches = new ArrayList<>();
    private Canvas mCanvas;
    private Paint mPaint = new Paint();
    private Paint mPaintText = new Paint(Paint.UNDERLINE_TEXT_FLAG);
    private Paint duckPaint = new Paint();
    private Paint bodyText = new Paint();
    private Bitmap mBitmap;
    private int mColorBackground;
    private int mBlack;
    private int mYellow;
    private int mWater;
    ImageView mImageView;
    List<Duck> gos = new ArrayList<>();
    List<Duck> ducks = new ArrayList<>();
    Bitmap duck;
    Bitmap startButtonImage;
    Bitmap endButtonImage;
    Bitmap hand;
    private int points = 0;
    private boolean didSpawnDucks = false;
    private GameState gameState = GameState.MENU;
    Rect startMenu;
    Rect endButton;
    private long endGame = 0;
    private int seconds = 0;
    private int gameDuration = 30;
    private int best = 0;

    private float hx = -1, hy = -1;
    ScoreRepository scoreRepository;
    LoginRepository loginRepository;
    String apiResponse;//just using this since Toast wasn't quite working
    WebView webView;
    ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gameViewModel =
                new ViewModelProvider(this).get(GameViewModel.class);
        View root = inflater.inflate(R.layout.fragment_game, container, false);
        mColorBackground = ResourcesCompat.getColor(getResources(),
                R.color.white, null);
        mBlack = ResourcesCompat.getColor(getResources(),
                R.color.black, null);
        mYellow = ResourcesCompat.getColor(getResources(), R.color.yellow, null);
        mWater = ResourcesCompat.getColor(getResources(), R.color.water, null);
        mPaint.setColor(  mWater);
        duckPaint.setColor(mYellow);
        duckPaint.setAntiAlias(true);
        duckPaint.setFilterBitmap(true);
        duckPaint.setDither(true);
        mPaintText.setColor(mBlack);
        mPaintText.setTextSize(70);
        bodyText.setColor(mBlack);
        bodyText.setTextSize(42);
        bodyText.setTextAlign(Paint.Align.CENTER);
        duck = BitmapFactory.decodeResource(getResources(), R.drawable.duck);
        hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);\
        //Loading gif example
        //https://stackoverflow.com/a/4534886
        webView = (WebView) root.findViewById(R.id.test);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_res/drawable/hand.gif");

        startButtonImage = BitmapFactory.decodeResource(getResources(), R.drawable.start_button);
        endButtonImage = BitmapFactory.decodeResource(getResources(), R.drawable.end_button);

        //https://stackoverflow.com/questions/45054908/how-to-add-a-gesture-detector-to-a-view-in-android
        // get the gesture detector
        mDetector = new GestureDetectorCompat(root.getContext(), new MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        mImageView = root.findViewById(R.id.imageView2);
        mImageView.setOnTouchListener(touchListener);

        final TextView textView = root.findViewById(R.id.text_title);
        gameViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        scoreRepository = ScoreRepository.getInstance(new ScoreDataSource(), getActivity().getApplication());
        loginRepository = LoginRepository.getInstance(new LoginDataSource(), getActivity().getApplication());
        ScheduledFuture scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(()->{
                    //Log.v("Test", "tick");
                    getActivity().runOnUiThread(()->{
                        draw(mImageView);
                    });


                }, 250, 16, TimeUnit.MILLISECONDS);

        return root;
    }
    private void drawEnd(int w, int h, Paint p){
        mCanvas.drawColor(mWater);
       // Paint.Style ps = p.getStyle();
       // p.setStyle(Paint.Style.STROKE);
        if(endButton == null){
            endButton = new Rect((int)(w * .25f), (int)(h*.7f), (int)(w * .75f), (int)(h*.8f));
        }
        else{
            endButton.set((int)(w * .25f), (int)(h*.7f), (int)(w * .75f), (int)(h*.8f));
        }
        //mCanvas.drawRect(endButton, p);
        mCanvas.drawBitmap(endButtonImage, null, endButton, p);
        mCanvas.drawText(String.format("Points: %s", points), 0   , 200, mPaintText);
        mCanvas.drawText(String.format("Best: %s", best), w*.5f   , 200, mPaintText);
        if(apiResponse != null){
            mCanvas.drawText(apiResponse, w*.5f, w*.45f, bodyText);
        }
       // p.setStyle(ps);
    }
    private void drawGame(int w, int h){
        seconds = (int) (endGame - System.currentTimeMillis())/1000;

        mCanvas.drawColor(mWater);
        if(!didSpawnDucks){
            for(int i = 0; i < 10; i++){
                Duck d = new Duck((float)Math.random()*w, (float)Math.random()*h, Math.random()>.5?-1:1, Math.random()>.5?-1:1);
                d.setImage(duck);
                ducks.add(d);
            }
            didSpawnDucks = true;
        }
        mPaint.setColor(mBlack);
        if(mCanvas != null) {
            mCanvas.drawText(String.format("Points: %s", points), 0   , h*.15f, mPaintText);

            mCanvas.drawText(String.format("Time: %s", seconds), w*.5f   , h*.15f, mPaintText);
            ducks.forEach((d)->{
                d.move();;
                d.draw(mCanvas, mPaint);
            });

        }
        if(hand != null && hx > -1 && hy > -1){
            mCanvas.drawBitmap(hand, hx, hy, null);
        }
    }
    private void drawMenu(int w, int h,Paint p){
        mCanvas.drawColor(mColorBackground);
       // p.setColor(mBlack);
       // Paint.Style ps = p.getStyle();
       // p.setStyle(Paint.Style.STROKE);
        if(startMenu == null){
            startMenu = new Rect((int)(w * .25f), (int)(h*.25f), (int)(w * .75f), (int)(h*.35f));
        }
       else{
           startMenu.set((int)(w * .25f), (int)(h*.25f), (int)(w * .75f), (int)(h*.35f));
        }
       // mCanvas.drawRect(startMenu, p);
        mCanvas.drawBitmap(startButtonImage, null, startMenu, p);
       // Paint.Align ta = mPaintText.getTextAlign();
        //mPaintText.setTextAlign(Paint.Align.CENTER);
      //  mCanvas.drawText("Start", w*.5f   , h*.3f, mPaintText);

        //p.setStyle(ps);
        //mPaintText.setTextAlign(ta);


    }
    private void checkGameEnd(){
        if(seconds <= 0){//check < 0 so user sees it hit 0 before ending
            //game over
            LoggedInUser user = loginRepository.getUser();
            if(user != null && user.getUserId() != null){
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                best = sharedPref.getInt("best_"+user.getUserId(), 0);
                if(points > best){
                    best = points;
                    sharedPref.edit().putInt("best_"+user.getUserId(), best).apply();
                }
                final String packageName = getActivity().getApplicationContext().getPackageName() + ".test";
                Log.v("GameFragment", "package name: " + packageName);
                ScoreData score = new ScoreData(packageName, user.getUserId(), points, null, null);
                //going to use a hard coded 100 as default, otherwise their best score so it's progressive
                //Note: user can keep clearing cache to make it easier to hit the "max" points
                //Note2: Also pick the max value so points is always <= maxScore so API doesn't reject
                int maxScore = Math.max(Math.min(100, best),points);
                score.setMaxScore(maxScore);
                scoreRepository.saveScore(score, (String resp)->{
                    Log.v("GameFragment", "Response: " + resp);
                    //Toast wasn't quite working here so using a "global" string instead
                    apiResponse = resp;
                    /*getActivity().runOnUiThread(()->{
                        Log.v("GameFragment", "show toast");
                        Toast.makeText(getActivity(), resp, Toast.LENGTH_LONG).show();
                    });*/


                });
            }
            gameState = GameState.END;
            return;
        }
    }
    public void draw(View view){
        int vWidth = view.getWidth();
        int vHeight = view.getHeight();
        int halfWidth = vWidth / 2;
        int halfHeight = vHeight / 2;
        if(mBitmap == null || mCanvas == null){
            mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
            mImageView.setImageBitmap(mBitmap);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(mColorBackground);
        }
        switch(gameState){
            case MENU:
                drawMenu(vWidth, vHeight, mPaint);
                break;
            case GAME:
                drawGame(vWidth, vHeight);
                checkGameEnd();
                break;
            case END:
                drawEnd(vWidth, vHeight, mPaint);
                break;
            default:
                Log.e("Dummy alert", "You forgot to implement this case " + gameState);
                break;
        }
        view.invalidate();
    }
    private void resetGame(){
        gameState = GameState.GAME;
        endGame = System.currentTimeMillis() + ((gameDuration+1) * 1000);//add + 1 so user sees whole number
        points = 0;
        apiResponse = null;
        //reset the ducks so they aren't immediately eligible to dive on new game
        //we want to have a delay of 10-15 seconds per the logic in Duck.java
        ducks.forEach(d->{
            d.reset();
        });
    }

    // This touch listener passes everything on to the gesture detector.
    // That saves us the trouble of interpreting the raw touch events
    // ourselves.
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            return mDetector.onTouchEvent(event);

        }
    };

    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {

            // don't return false here or else none of the other
            // gestures will work
            MotionEvent.PointerCoords start = new MotionEvent.PointerCoords();
            event.getPointerCoords(0, start);
            switch (gameState){
                case MENU:
                    //clicking the "button" will start the menu
                    if(startMenu.contains((int)start.x, (int)start.y)){
                        resetGame();
                    }
                    break;
                case GAME:
                    //clicking will determine if ducks are hit
                    hx = start.x;
                    hy = start.y;
                    ducks.forEach(d->{
                        boolean hit = d.checkHit(start.x, start.y, 5);
                        if(hit){
                            points++;
                        }
                    });
                    break;
                case END:
                    //clicking the "button" will reset the game
                    if(endButton.contains((int)start.x, (int)start.y)){
                        resetGame();
                    }
                    break;
                default:
                    break;
            }

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
           // Log.i("TAG", "onSingleTapConfirmed: ");
           // dumpTouches(e, null);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
          //  Log.i("TAG", "onLongPress: ");
          //  dumpTouches(e, null);

        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
           // Log.i("TAG", "onDoubleTap: ");
           // dumpTouches(e,null);
            return true;
        }
        @Override
        public boolean onDoubleTapEvent(MotionEvent e){
           // Log.i("TAG", "onDoubleTapEvent: ");
           // dumpTouches(e,null);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
           // Log.i("TAG", "onScroll: ");
           // Log.d("Distance", distanceX + ", " + distanceY);
           // dumpTouches(e1, e2);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
          /*  Log.d("TAG", "onFling: ");
            Log.d("Velocity", velocityX + ", " + velocityY);
            dumpTouches(e1,e2);
            MotionEvent.PointerCoords start = new MotionEvent.PointerCoords();
            e1.getPointerCoords(0, start);
            MotionEvent.PointerCoords end = new MotionEvent.PointerCoords();
            e2.getPointerCoords(0, end);
            float dx = (start.x - end.x);
            float dy = (start.y - end.y);
            Log.d("Direction", dx + ", " + dy);
            GameObject g = new GameObject(start.x, start.y, dx, dy);
            gos.add(g);*/
            return true;
        }
    }
}