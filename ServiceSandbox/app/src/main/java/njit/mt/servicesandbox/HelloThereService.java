package njit.mt.servicesandbox;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.os.Process; //May not auto import or hint at import, needed for the Process constant

public class HelloThereService extends Service {
    int startMode;       // indicates how to behave if the service is killed
    IBinder binder;      // interface for clients that bind
    boolean allowRebind; // indicates whether onRebind should be used
    final String TAG = "HelloThereService";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    HelloThereService self;
    HandlerThread thread;
    Handler mHandler = new Handler(Looper.getMainLooper());
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            // Normally we would do some work here, like download a file.
            Log.v(TAG, "handling message"+msg);
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(3000);
                    //https://stackoverflow.com/questions/12618038/why-to-use-handlers-while-runonuithread-does-the-same
                    if(Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted()) {
                        if(Thread.currentThread() != getMainLooper().getThread()){
                            mHandler.post(()->{
                                Toast.makeText(self, "Hello There", Toast.LENGTH_SHORT).show();
                            });
                        }
                        else{
                            Toast.makeText(self, "Hello There", Toast.LENGTH_SHORT).show();
                        }
                        Log.v(TAG, "showing message");
                    }
                    else{
                        break;
                    }
                }
            } catch (InterruptedException e) {
                // Restore interrupt status.
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
           // stopSelf(msg.arg1);
        }
    }
    @Override
    public void onCreate() {
        self = this;
        // The service is being created
        Log.v(TAG, "onCreate()");
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand()");
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        // The service is starting, due to a call to startService()
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        return startMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind()");
        // A client is binding to the service with bindService()
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind()");
        // All clients have unbound with unbindService()
        return allowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "onRebind()");
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy()");
        if(thread != null){
            thread.interrupt();
            thread.quit();
        }
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        // The service is no longer used and is being destroyed
    }
}