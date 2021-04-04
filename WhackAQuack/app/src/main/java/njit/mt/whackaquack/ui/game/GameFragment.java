package njit.mt.whackaquack.ui.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import njit.mt.whackaquack.R;

public class GameFragment extends Fragment {

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private GameViewModel gameViewModel;
    List<List<MotionEvent.PointerCoords>> touches = new ArrayList<>();
    private Canvas mCanvas;
    private Paint mPaint = new Paint();
    private Paint mPaintText = new Paint(Paint.UNDERLINE_TEXT_FLAG);
    private Bitmap mBitmap;
    private int mColorBackground;
    private int mBlack;
    ImageView mImageView;

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

        mPaint.setColor(  mColorBackground);
        mPaintText.setColor(mBlack);
        mPaintText.setTextSize(70);


        //https://stackoverflow.com/questions/45054908/how-to-add-a-gesture-detector-to-a-view-in-android
        // get the gesture detector
        mDetector = new GestureDetectorCompat(root.getContext(), new MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        mImageView = root.findViewById(R.id.imageView2);
        mImageView.setOnTouchListener(touchListener);

        final TextView textView = root.findViewById(R.id.text_title);
        gameViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        ScheduledFuture scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(()->{
                    //Log.v("Test", "tick");
                    getActivity().runOnUiThread(()->{
                        draw(mImageView);
                    });


                }, 250, 16, TimeUnit.MILLISECONDS);

        return root;
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
        mCanvas.drawColor(mColorBackground);


        mCanvas.drawText(String.format("Touches: %s", touches.size()), 0   , 200, mPaintText);
        mPaint.setColor(mBlack);
        if(mCanvas != null) {
            //TODO: Not really an efficient way to do things, just here to provide a
            //minimal visual representation
            Iterator<List<MotionEvent.PointerCoords>> iter = touches.iterator();
            while(iter.hasNext()){
                List<MotionEvent.PointerCoords> pcs = iter.next();
                if(pcs != null){
                    for(int i = 0; i < pcs.size(); i++){
                        MotionEvent.PointerCoords pc = pcs.get(i);
                        mCanvas.drawCircle(pc.x, pc.y, 50 * pc.pressure, mPaint);
                    }

                }
                iter.remove();//comment out to paint :), but would really require some sort of
                //optimization
            }
        }
        view.invalidate();
    }
    public void dumpTouches(MotionEvent event, MotionEvent event2){
        int t = event.getPointerCount();
        List<MotionEvent.PointerCoords> pcs = new ArrayList<>();
        for(int i = 0; i < t; i++){
            MotionEvent.PointerCoords pc = new MotionEvent.PointerCoords();
            event.getPointerCoords(i, pc);
            float size = pc.size;
            float orientation = pc.orientation;
            float pressure = pc.pressure;
            float x = pc.x;
            float y = pc.y;
            Log.v("Touch " + i, String.format("(x,y) %f,%f   Size: %f, Orientation: %f, Pressure: %f", x,y,size,orientation,pressure));
            pcs.add(pc);

        }
        if(event2 != null) {
            t = event2.getPointerCount();
            for (int i = 0; i < t; i++) {
                MotionEvent.PointerCoords pc = new MotionEvent.PointerCoords();
                event2.getPointerCoords(i, pc);
                float size = pc.size;
                float orientation = pc.orientation;
                float pressure = pc.pressure;
                float x = pc.x;
                float y = pc.y;
                Log.v("Touch " + i, String.format("(x,y) %f,%f   Size: %f, Orientation: %f, Pressure: %f", x, y, size, orientation, pressure));
                pcs.add(pc);
            }
        }
        touches.add(pcs);
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
            Log.d("TAG","onDown: ");
            dumpTouches(event, null);
            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            dumpTouches(e, null);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
            dumpTouches(e, null);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            dumpTouches(e,null);
            return true;
        }
        @Override
        public boolean onDoubleTapEvent(MotionEvent e){
            Log.i("TAG", "onDoubleTapEvent: ");
            dumpTouches(e,null);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            Log.d("Distance", distanceX + ", " + distanceY);
            dumpTouches(e1, e2);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            Log.d("Velocity", velocityX + ", " + velocityY);
            dumpTouches(e1,e2);
            return true;
        }
    }
}