package njit.mt.exploring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);

        //Programmatically create a button click event handler
        Button b = (Button)findViewById(R.id.submitButton);
        b.setOnClickListener((View v)->{
            EditText et = (EditText)findViewById(R.id.editText);
            String name = et.getText().toString();
            Intent intent = new Intent();
            intent.putExtra("name", name);
            //set the result code (different from request code) and the intent
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}