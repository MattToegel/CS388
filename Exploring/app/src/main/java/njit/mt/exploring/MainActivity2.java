package njit.mt.exploring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    final String TAG = "MainActivity2";
    final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.v(TAG, "onCreate()");
    }

    public void changeActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void doImplicitIntent(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello World");
        sendIntent.setType("text/plain");
        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void doGetResult(View view){
        Intent intent = new Intent(this, EnterNameActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                TextView nameText = (TextView)findViewById(R.id.nameText);
                nameText.setText("Hello, " + name);
            }

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart()");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }
}