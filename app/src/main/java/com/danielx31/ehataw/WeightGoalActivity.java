package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class WeightGoalActivity extends AppCompatActivity {

    private UserAPI userAPI;

    private Button nextButton;
    private EditText weightGoalEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_goal);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        userAPI = new UserAPI();

        nextButton = findViewById(R.id.button_next);
        weightGoalEditText = findViewById(R.id.edittext_weightgoal);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weightGoal = weightGoalEditText.getText().toString() + " kg";
                userAPI.setWeightGoal(weightGoal);

                startActivity(new Intent(getApplicationContext(), HealthRelatedActivity.class));
                finish();
            }
        });

    }
}