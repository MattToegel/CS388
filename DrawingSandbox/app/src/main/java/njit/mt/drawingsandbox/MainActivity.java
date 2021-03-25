package njit.mt.drawingsandbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private Canvas mCanvas;
    private Paint mPaint = new Paint();
    private Paint mPaintText = new Paint(Paint.UNDERLINE_TEXT_FLAG);
    private Bitmap mBitmap;
    private ImageView mImageView;
    private Rect mRect = new Rect();
    private Rect mBounds = new Rect();
    private static final int OFFSET = 120;
    private int mOffset = OFFSET;
    private static final int MULTIPLIER = 100;
    private int mColorBackground;
    private int mColorRectangle;
    private int mColorAccent;

    ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mColorBackground = ResourcesCompat.getColor(getResources(),
                R.color.colorBackground, null);
        mColorRectangle = ResourcesCompat.getColor(getResources(),
                R.color.colorRectangle, null);
        mColorAccent = ResourcesCompat.getColor(getResources(),
                R.color.colorAccent, null);

        mPaint.setColor(mColorBackground);
        mPaintText.setColor(
                ResourcesCompat.getColor(getResources(),
                        R.color.black, null)
        );
        mPaintText.setTextSize(70);

        mImageView = (ImageView) findViewById(R.id.myimageview);



        ScheduledFuture scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(()->{
                    Log.v("Test", "tick");
                    runOnUiThread(()->{
                        drawSomething(mImageView);
                    });


                }, 250, 16, TimeUnit.MILLISECONDS);

    }
    int x,y, dx = 1, dy = 1, speed = 5;
    public void drawSomething(View view) {
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
        x +=  dx * speed;
        y += dy * speed;

        if(x >= vWidth){
            dx *= -1;
        }
        else if(x <= 0){
            dx *= -1;
        }
        if(y >= vHeight){
            dy *= -1;
        }
        else if(y <= 0){
            dy *= -1;
        }
        mCanvas.drawText(String.format("X,Y  %s,%s", x, y), 100, 100, mPaintText);
        mPaint.setColor(mColorAccent);
        if(mCanvas != null) {
            mCanvas.drawCircle(x, y, 50, mPaint);
        }
        /*if (mOffset == OFFSET) {
            mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
            mImageView.setImageBitmap(mBitmap);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(mColorBackground);
            mCanvas.drawText("test", 100, 100, mPaintText);
            mOffset += OFFSET;
        } else {
            if (mOffset < halfWidth && mOffset < halfHeight) {
                // Change the color by subtracting an integer.
                mPaint.setColor(mColorRectangle - MULTIPLIER*mOffset);
                mRect.set(
                        mOffset, mOffset, vWidth - mOffset, vHeight - mOffset);
                mCanvas.drawRect(mRect, mPaint);
                // Increase the indent.
                mOffset += OFFSET;
            } else {
                mPaint.setColor(mColorAccent);
                mCanvas.drawCircle(halfWidth, halfHeight, halfWidth / 3, mPaint);
                String text = "done";
                // Get bounding box for text to calculate where to draw it.
                mPaintText.getTextBounds(text, 0, text.length(), mBounds);
                // Calculate x and y for text so it's centered.
                int x = halfWidth - mBounds.centerX();
                int y = halfHeight - mBounds.centerY();
                mCanvas.drawText(text, x, y, mPaintText);
            }
        }*/
        view.invalidate();
    }
}