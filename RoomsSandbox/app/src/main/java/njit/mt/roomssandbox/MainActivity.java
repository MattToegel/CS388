package njit.mt.roomssandbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import njit.mt.roomssandbox.db.entity.UserEntity;
import njit.mt.roomssandbox.viewmodel.UserViewModel;
import njit.mt.roomssandbox.viewmodel.UserViewModelFactory;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private UserViewModel userViewModel;
    public static final int NEW_USER_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final UserListAdapter adapter = new UserListAdapter(new UserListAdapter.UserDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Since our model has a 1 parameter constructor we need to use a factory to map it
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(getApplication())).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, users -> {
            // Update the cached copy of the words in the adapter.
            adapter.submitList(users);
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
            startActivityForResult(intent, NEW_USER_ACTIVITY_REQUEST_CODE);
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_USER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String[] n = data.getStringArrayExtra(AddUserActivity.EXTRA_REPLY);
            UserEntity user = new UserEntity(n[1], n[0]);
            userViewModel.insert(user);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}