package njit.mt.whackaquack.ui.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

public class Duck {

    private float x;
    private float y;
    private float speed = 3;
    private float dx;
    private float dy;
    private float radius = 50;
    private boolean isActive = false;
    private long nextDirChange = 0;
    private long delay = 500;
    private Random r = new Random();
    private Bitmap image;
    private Matrix matrix;
    private boolean isDiving = false;
    private long nextSurface = 0;
    private long nextDive = 0;
    private long nextSpawn = 0;

    public Duck(float x, float y, float dx, float dy){
        this.x = x;
        this.y = y;
        this.setDirection(dx, dy);
        this.isActive = true;
        reset();
    }
    public void reset(){
        nextDive = System.currentTimeMillis() + (long)randomRange(10000, 15000);
        isDiving = false;

    }
    public boolean checkHit(float x, float y, float radius){
        if(isDiving || !isActive){
            return false;
        }
        float dpx = this.x - this.radius/2;
        float dpy = this.y - this.radius/2;
        float tpx = x - radius/2;
        float tpy = y - radius/2;
        float a = (float)Math.pow(dpx - tpx, 2);
        float b = (float)Math.pow(dpy - tpy, 2);
        float c=  this.radius + radius;
        boolean hit =  (a+b) <= (Math.pow(c,2));
        if(hit){
            Log.v("Duck", "hit");
            this.isActive = false;
            nextSpawn = System.currentTimeMillis() + (long)randomRange(500, 5000);
        }
        return hit;
    }
    public void setImage(Bitmap img){
        image = img;
        matrix = new Matrix();
        // Flip
        matrix.setScale(-1, 1);
    }
    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }
    public void setDirection(float x, float y){
        dx = clamp(x, -1.0f, 1.0f);
        dy = clamp(y, -1.0f,1.0f);

    }
    public static float clamp(float value, float min, float max){
        return Math.min(Math.max(value, min), max);
    }
    public static float randomRange(float min, float max){
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }
    private void doRandom(){
        long time = System.currentTimeMillis();
        if(time > nextDirChange){

            setDirection(randomRange(-1,1), randomRange(-1,1));
            nextDirChange =time + delay;
            speed = randomRange(1, 10);
            delay = (long)randomRange(300, 3000);
            if(time > nextDive) {
                if (!isDiving && Math.random() > -.9999) {
                    nextSurface = time + (long) randomRange(250, 2000);
                    isDiving = true;
                }
                nextDive = time + (long)randomRange(1000, 5000);
            }
        }
        if(isDiving && time > nextSurface){
            isDiving = false;
        }
    }
    public void move(){
        if(!isActive){
            if(System.currentTimeMillis() > nextSpawn){
                this.isActive = true;
            }
            return;
        }
        float s = speed;
        if(isDiving){
            s *= .35;
        }
        x += dx * s;
        y += dy * s;
        doRandom();
    }
    public void draw(Canvas c, Paint p){
        if(!isActive){
            return;
        }
        if(isDiving) {
            c.drawCircle(x, y + (radius / 2), radius * .75f, p);
        }
        else {
            if (image != null) {
                //matrix.reset();
                // if(pdx != dx) {
                matrix.setScale(-(dx > 0 ? 1 : -1), 1, image.getWidth() / 2, image.getHeight() / 2);
                //  pdx = dx;
                // }
                matrix.postTranslate(x - image.getWidth() / 2, y - image.getHeight() / 2);
                c.drawBitmap(image, matrix, null);

            }
        }
        /*if(x < 0 || x > c.getWidth() || y < 0 || y > c.getHeight()){
            isActive = false;
        }*/
        if(x < 0){
            x = c.getWidth();
        }
        else if(x > c.getWidth()){
            x = 0;
        }
        if(y < 0){
            y = c.getHeight();
        }
        else if(y > c.getHeight()){
            y = 0;
        }
    }
}
