package njit.mt.roomssandbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

public class AddUserActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "njit.mt.roomssandbox.REPLY";

    private EditText firstNameEditText, lastNameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        firstNameEditText = findViewById(R.id.editFirstName);
        lastNameEditText = findViewById(R.id.editLastName);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(firstNameEditText.getText()) || TextUtils.isEmpty(lastNameEditText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                replyIntent.putExtra(EXTRA_REPLY, new String[]{lastNameEditText.getText().toString(), firstNameEditText.getText().toString()});
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}