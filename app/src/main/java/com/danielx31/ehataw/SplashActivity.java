package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class SplashActivity extends AppCompatActivity {

    private static int SplashTimeOut = 2000;
    private static final String TAG = "SplashActivity";
    private UserAPI userAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        RxJavaPlugins.setErrorHandler(e -> { });


        userAPI = new UserAPI();

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(1800);
        ImageView image = findViewById(R.id.logo);

        image.setAnimation(fadeOut);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startEntryIntent();
                handler.removeCallbacks(this);
            }
        }, SplashTimeOut);

    }

    private void startEntryIntent() {
        if (!userAPI.isUserLoggedIn()) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
            return;
        }

        if (!userAPI.isUserEmailVerified()) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
            return;
        }

        userAPI.onUserCalibrated(new UserAPI.OnUserCalibratedListener() {
            @Override
            public void onUserCalibrated() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onUserNotCalibrated() {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                finish();
            }

            @Override
            public void onValidatingFailed(Exception e) {
                Toast.makeText(SplashActivity.this, "A Network Error Occurred! Please Try Again!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}