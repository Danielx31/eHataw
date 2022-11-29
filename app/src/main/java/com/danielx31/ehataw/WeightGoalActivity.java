package com.danielx31.ehataw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class WeightGoalActivity extends AppCompatActivity {

    private ConnectionReceiverPrime connectionReceiverPrime;

    private UserAPI userAPI;
    private User user;

    private Button nextButton;
    private EditText weightGoalEditText;
    private EditText zumbaCountEditText;
    private LoadingDialog loadingDialog;

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
        zumbaCountEditText = findViewById(R.id.edittext_zumbacountgoal);

        loadingDialog = new LoadingDialog();

        loadingDialog.show(getSupportFragmentManager(), "loadingDialog");

        userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
            @Override
            public void onFetchSuccess(User fetchedUser) {
                user = fetchedUser;
                weightGoalEditText.setText(String.valueOf(user.getWeightInKg()));
                loadingDialog.dismiss();
            }

            @Override
            public void onFetchNotFound() {
                Toast.makeText(getApplicationContext(), "A Network Error Occurred! Please Try Again", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(getApplicationContext(), "A Network Error Occurred! Please Try Again", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "Loading... Please Try Again Later!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (weightGoalEditText.getText().toString().isEmpty()) {
                    weightGoalEditText.setError("Please fill out the Input");
                    return;
                }

                String weightGoal = weightGoalEditText.getText().toString() + " kg";
                String[] weightGoalParts = weightGoal.split(" ");
                double weightGoalInKg = Double.parseDouble(weightGoalParts[0]);

                if (weightGoalInKg <= 0) {
                    weightGoalEditText.setError("Invalid Weight Goal!");
                    return;
                }

                if (weightGoalInKg > user.getWeightInKg()) {
                    weightGoalEditText.setError("Weight Goal must be equal or lower to your current weight!");
                    return;
                }

                BMITracker bmiTracker = new BMITracker(user.getWeightInKg(), user.getHeightInCm());
                BMITracker.BMIClassification classification = bmiTracker.classifyBMI();

                if (classification == BMITracker.BMIClassification.UNDERWEIGHT) {
                    weightGoalEditText.setError("Cannot Proceed!! Underweight Goal");
                    return;
                }

                if (!user.getHealthConditions().isEmpty() &&
                weightGoalInKg < user.getWeightInKg()) {
                    showWarningDialog();
                    return;
                }

                String zumbaCountGoalPerDayString = zumbaCountEditText.getText().toString();

                if (zumbaCountGoalPerDayString == null || zumbaCountGoalPerDayString.isEmpty()) {
                    zumbaCountEditText.setError("Please fill out required input!");
                    return;
                }

                int zumbaCountGoalPerDay = Integer.valueOf(zumbaCountEditText.getText().toString());

                if (zumbaCountGoalPerDay <= 0) {
                    zumbaCountEditText.setError("Goal must at least have one!");
                    return;
                }

                Map<String, Object> goals = new HashMap<>();
                goals.put("weightGoalFrom", user.getWeight());
                goals.put("weightGoal", weightGoal);
                goals.put("zumbaCountGoalPerDay", zumbaCountGoalPerDay);

                loadingDialog.show(getSupportFragmentManager(), "loadingDialog");

                userAPI.setGoals(goals, new UserAPI.OnSetListener() {
                    @Override
                    public void onSetSuccess() {
                        loadingDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onSetError(Exception error) {
                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "A Network Error Occurred!\nPlease Try Again!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }



    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("We recommend you to consult to the doctors if you have some health conditions.\nStill continue?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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