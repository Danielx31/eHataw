package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class BMIUserActivity extends AppCompatActivity {

    private UserAPI userAPI;

    private Button nextButton;
    private EditText heightEditText;
    private EditText weightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_user);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        userAPI = new UserAPI();

        nextButton = findViewById(R.id.button_next);
        heightEditText = findViewById(R.id.inputnumber_height);
        weightEditText = findViewById(R.id.inputnumber_weight);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = weightEditText.getText().toString() + " kg";
                String height = heightEditText.getText().toString() + " cm";
                userAPI.setBodySize(weight, height);

                startActivity(new Intent(getApplicationContext(), WeightGoalActivity.class));
                finish();
            }
        });
    }
}