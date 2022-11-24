package com.danielx31.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

//import com.danielx31.ehataw.firebase.model.Zumba;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class HomeActivity extends AppCompatActivity {

    private static final int BACK_PRESS_TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long backPressed;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        RxJavaPlugins.setErrorHandler(e -> {
        });
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navigationListener);

        Bundle extras = getIntent().getExtras();
        String fragmentString = "";
        if (extras != null) {
            fragmentString = extras.getString("fragment");
        }
        startFragment(fragmentString);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container_fragment);
        if (currentFragment instanceof HomeFragment || currentFragment instanceof MenuFragment || currentFragment instanceof DietFragment) {
            if (backPressed + BACK_PRESS_TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                finishAffinity();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Press again to exit!", Toast.LENGTH_SHORT).show();
            }

            backPressed = System.currentTimeMillis();
            return;
        }

        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new MenuFragment()).commit();
    }

    public void startFragment(String fragmentString) {
        if (fragmentString == null || fragmentString.isEmpty()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new HomeFragment()).commit();
            return;
        }

        Fragment fragment = new HomeFragment();
        if (fragmentString.equals("downloadVideo")) {
            fragment = new OfflineVideosFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, fragment).commit();
    }

    private NavigationBarView.OnItemSelectedListener navigationListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = new HomeFragment();

            switch (item.getItemId()) {
                case R.id.bottom_nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.bottom_nav_menu:
                    selectedFragment = new MenuFragment();
                    break;
                case R.id.bottom_nav_diet:
                    selectedFragment = new DietFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, selectedFragment).commit();

            return true;
        }
    };

}