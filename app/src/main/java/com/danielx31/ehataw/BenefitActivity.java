package com.danielx31.ehataw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import java.util.HashMap;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class BenefitActivity extends AppCompatActivity {

    private TextView messageTextView, messageTextView_1, messageTextView_2;
    private String benefit,stats,duration;
    private boolean isOnline;
    private Button saveButton, menuButton;
    private TextView saveInfoTextView;
    private UserAPI userAPI;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefit);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        userAPI = new UserAPI();

        messageTextView = findViewById(R.id.textview_message);
        messageTextView_1 = findViewById(R.id.textView2);
        messageTextView_2 = findViewById(R.id.textView7);
        setMessage();

        //Code here
        messageTextView.setText("By Dancing this Zumba, " + benefit);
        messageTextView_1.setText(stats);
        messageTextView_2.setText(duration + "\nDuration");

        saveInfoTextView = findViewById(R.id.textview_saveinfo);
        saveButton = findViewById(R.id.button_save);
        menuButton = findViewById(R.id.button_menu);

        if (!isOnline) {
            saveButton.setVisibility(View.GONE);
            saveInfoTextView.setVisibility(View.GONE);
        }

        if (isOnline) {
            userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
                @Override
                public void onFetchSuccess(User fetchedUser) {
                    user = fetchedUser;
                }

                @Override
                public void onFetchNotFound() {
                    Toast.makeText(getApplicationContext(), "A Network Error Occurred!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFetchError(Exception e) {
                    Toast.makeText(getApplicationContext(), "A Network Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "A Network Error Occurred! Please try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] statsParts = stats.split("\n");
                double calories = Double.parseDouble(statsParts[0]);

                double weightDecreased = caloriesToKg(calories);

                double newWeightInKg = user.getWeightInKg() - weightDecreased;
                String newWeight = newWeightInKg + " kg";
                userAPI.setWeight(newWeight, new UserAPI.OnSetListener() {
                    @Override
                    public void onSetSuccess() {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onSetError(Exception error) {
                        Toast.makeText(getApplicationContext(), "A Network Error Occurred! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline) {
                    showNotSaveDialog();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private double caloriesToKg(double calories) {
        return calories * 0.00013;
    }

    public void setMessage() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        benefit = extras.getString("message");
        stats = extras.getString("stats");
        duration = extras.getString("duration");
        isOnline = extras.getBoolean("isOnline");
        if (benefit == null && stats == null && duration == null) {
            Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void showNotSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Your data will not be saved!\n Are you sure?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
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
}