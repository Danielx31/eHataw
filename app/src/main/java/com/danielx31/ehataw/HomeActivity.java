package com.danielx31.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navigationListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new HomeFragment()).commit();

    }

    private NavigationBarView.OnItemSelectedListener navigationListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.bottom_nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.bottom_nav_menu:
                    selectedFragment = new MenuFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, selectedFragment).commit();

            return true;
        }
    };

}