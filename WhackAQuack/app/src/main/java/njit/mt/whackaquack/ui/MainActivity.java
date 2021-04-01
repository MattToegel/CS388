package njit.mt.whackaquack.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import njit.mt.whackaquack.R;//Will need to specify this one
import njit.mt.whackaquack.data.LoginDataSource;
import njit.mt.whackaquack.data.LoginRepository;
/* Related Reading

https://developer.android.com/guide/navigation/navigation-migrate
https://guides.codepath.com/android/creating-and-using-fragments#navigating-between-fragments
https://developer.android.com/guide/fragments/create
https://developer.android.com/guide/fragments/fragmentmanager
https://developer.android.com/guide/navigation

 */

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    LoginRepository loginRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_login,R.id.nav_home,R.id.nav_profile, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        loginRepository =  LoginRepository.getInstance(new LoginDataSource(), getApplicationContext());
        loginRepository.isLoggedIn().observe(this,(isLoggedIn)->{
            Log.v("isLoggedIn", isLoggedIn?"true":"false");
                Menu nav = navigationView.getMenu();
                nav.findItem(R.id.nav_login).setVisible(!isLoggedIn);
                nav.findItem(R.id.nav_profile).setVisible(isLoggedIn);
                nav.findItem(R.id.nav_logout).setVisible(isLoggedIn);
                if(isLoggedIn) {
                    ((MenuItem) nav.findItem(R.id.nav_logout)).setOnMenuItemClickListener((menuItem) -> {
                        Log.v("Logout button", "pressed");
                        loginRepository.logout();
                        return true;
                    });
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}