package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class WeightGoalActivity extends AppCompatActivity {

    private ConnectionReceiverPrime connectionReceiverPrime;

    private UserAPI userAPI;

    private Button nextButton;
    private EditText weightGoalEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_goal);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        connectionReceiverPrime = new ConnectionReceiverPrime();

        userAPI = new UserAPI();

        nextButton = findViewById(R.id.button_next);
        weightGoalEditText = findViewById(R.id.edittext_weightgoal);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weightGoal = weightGoalEditText.getText().toString() + " kg";
                userAPI.setWeightGoal(weightGoal, new UserAPI.OnSetListener() {
                            @Override
                            public void onSetSuccess() {
                                startActivity(new Intent(getApplicationContext(), HealthRelatedActivity.class));
                                finish();
                            }

                            @Override
                            public void onSetError(Exception error) {
                                Toast.makeText(getApplicationContext(), "A Network Error Occurred!\nPlease Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

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