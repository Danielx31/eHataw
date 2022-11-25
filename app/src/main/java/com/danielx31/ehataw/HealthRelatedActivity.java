package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java8.util.Maps;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class HealthRelatedActivity extends AppCompatActivity {

    private ConnectionReceiverPrime connectionReceiverPrime;

    private UserAPI userAPI;

    private Map<String, CheckBox> healthConditionCheckBoxes;
    private Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_related);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        connectionReceiverPrime = new ConnectionReceiverPrime();

        userAPI = new UserAPI();

        healthConditionCheckBoxes = new HashMap<>();

        healthConditionCheckBoxes.put("Asthma", findViewById(R.id.cb_asthma));
        healthConditionCheckBoxes.put("Diabetes", findViewById(R.id.cb_diabetes));
        healthConditionCheckBoxes.put("Heart Diseases", findViewById(R.id.cb_heartdisease));
        healthConditionCheckBoxes.put("High Blood", findViewById(R.id.cb_highbloodpressure));
        healthConditionCheckBoxes.put("Obesity", findViewById(R.id.cb_obesity));

        nextButton = findViewById(R.id.button_next);

        Maps.forEach(healthConditionCheckBoxes, (name, checkBox) -> {
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()) {
                        checkBox.setTextColor(getResources().getColor(R.color.orange));
                        return;
                    }

                    checkBox.setTextColor(getResources().getColor(R.color.white));
                }
            });
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAPI.setHealthConditions(getHealthConditions());

                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

    }

    private List<String> getHealthConditions() {
        if (healthConditionCheckBoxes == null) {
            return new ArrayList<>();
        }

        List<String> healthConditions = new ArrayList<>();
        Maps.forEach(healthConditionCheckBoxes, (name, checkBox) -> {
            if (!checkBox.isChecked()) {
                return;
            }

            healthConditions.add(name);
        });

        return healthConditions;
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiverPrime, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectionReceiverPrime);
    }

}