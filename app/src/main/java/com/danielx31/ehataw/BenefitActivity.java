package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class BenefitActivity extends AppCompatActivity {

    private TextView messageTextView, messageTextView_1, messageTextView_2;
    private String benefit,kcal,duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefit);
        messageTextView = findViewById(R.id.textview_message);
        messageTextView_1 = findViewById(R.id.textView2);
        messageTextView_2 = findViewById(R.id.textView7);
        setMessage();

        //Code here
        messageTextView.setText("By Dancing this Zumba, " + benefit);
        messageTextView_1.setText(kcal + "\nCalories");
        messageTextView_2.setText(duration + "\nDuration");

    }

    public void setMessage() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        benefit = extras.getString("message");
        kcal = extras.getString("kcal");
        duration = extras.getString("duration");
        if (benefit == null && kcal == null && duration == null) {
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
}