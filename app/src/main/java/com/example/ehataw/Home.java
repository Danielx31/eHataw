package com.example.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    MenuFragment menuFragment = new MenuFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyEhatawData[] myEhatawData = new MyEhatawData[]{
                new MyEhatawData("Arms", "Exercise Arms", R.drawable.daniel_picture),
                new MyEhatawData("Ham & Cheese", "Pizza", R.drawable.ham_cheese),
                new MyEhatawData("Zumba Dance", "Exercise Zumba", R.drawable.ehataw),
                new MyEhatawData("Hawaiian", "Pizzarap", R.drawable.hawaiian),
                new MyEhatawData("Meeting", "Office Meeting", R.drawable.istockphoto_1369207279_170667a),
                new MyEhatawData("Peperoni", "Pizzarap you", R.drawable.pepperoni),
                new MyEhatawData("Strategy", "Random Pic", R.drawable.strategy),
        };

        MyEhatawAdapter myEhatawAdapter =  new MyEhatawAdapter(myEhatawData, Home.this);
        recyclerView.setAdapter(myEhatawAdapter);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case R.id.menu:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, menuFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }
}