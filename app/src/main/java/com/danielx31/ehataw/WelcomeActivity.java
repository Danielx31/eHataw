package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class WelcomeActivity extends AppCompatActivity {

    private ConnectionReceiverPrime connectionReceiverPrime;

    private TextView privacyAndPolicyTextView;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        connectionReceiverPrime = new ConnectionReceiverPrime();

        privacyAndPolicyTextView = findViewById(R.id.textview_privacyandpolicy);

        privacyAndPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PolicyDialog policyDialog = new PolicyDialog();
                policyDialog.show(getSupportFragmentManager(), "privacyAndPolicyDialog");
            }
        });

        nextButton = findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), BMIUserActivity.class));
                finish();
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