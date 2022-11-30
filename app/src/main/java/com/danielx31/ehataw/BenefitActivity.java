package com.danielx31.ehataw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ServerValue;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.sql.Time;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

                LoadingDialog loadingDialog = new LoadingDialog();
                loadingDialog.show(getSupportFragmentManager(), "loadingDialog");

                String[] statsParts = stats.split("\n");
                double calories = Double.parseDouble(statsParts[0]);

                double weightDecreasedInKg = caloriesToKg(calories);

                double newWeightInKg = user.getWeightInKg() - weightDecreasedInKg;
                String newWeight = newWeightInKg + " kg";

                Map<String, Object> systemTags = new HashMap<>();

                systemTags.put("monitorDate", new Date());

                Timestamp monitorDate = user.getMonitorDate();

                boolean isSameDateNoTime = false;

                if (monitorDate != null) {
                    isSameDateNoTime = isSameDateNoTime(monitorDate.toDate(), new Date());
                    Log.d("Test", "is Same Date No Time = " + isSameDateNoTime);
                }

                systemTags.put("weightDecreasedPerDay", weightDecreasedInKg + " kg");

                if (user.getWeightDecreasedPerDayInKg() != null && isSameDateNoTime) {
                    Double weightDecreasedPerDay = user.getWeightDecreasedPerDayInKg();
                    systemTags.put("weightDecreasedPerDay", (weightDecreasedPerDay + weightDecreasedInKg) + " kg");
                }

                systemTags.put("zumbaFollowedCountPerDay", 1);
                if (user.getZumbaFollowedCountPerDay() > 0 && isSameDateNoTime) {
                    Long zumbaFollowedCountPerDay = user.getZumbaFollowedCountPerDay();
                    systemTags.put("zumbaFollowedCountPerDay", zumbaFollowedCountPerDay + 1);
                }

                userAPI.followZumba(newWeight, systemTags, new UserAPI.OnSetListener() {
                    @Override
                    public void onSetSuccess() {
                        loadingDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onSetError(Exception error) {
                        loadingDialog.dismiss();
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


    private boolean isSameDateNoTime(Date date1, Date date2) {
        LocalDate localDate1 = new LocalDate(date1);
        LocalDate localDate2 = new LocalDate(date2);

        if (localDate1.compareTo(localDate2) == 0) {
            return true;
        }

        return false;
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
        builder.setMessage("Your data will not be saved!\nAre you sure?");
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