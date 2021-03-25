package njit.mt.broadcastclass;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";
    private static final String CHANNEL_ID = "10005";

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d(TAG, log);
        String oneliner = "";
        final String action = intent.getAction();
        int noteId = 0;
        switch(action){
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                noteId = 1;
                oneliner = "Airplane Mode is " + (intent.getBooleanExtra("state", false)?"On":"Off");
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                noteId = 2;
                oneliner = "Didn't really think of something";
                break;
            default:
                Log.e(TAG, "This isn't supported :(  " + action );
                break;
        }
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("My notification")
                .setContentText(oneliner)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(log))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        createNotificationChannel(context);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(noteId, builder.build());

    }
    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TestNotifs";//getString(R.string.channel_name);
            String description = "Test our various notifications";//getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}