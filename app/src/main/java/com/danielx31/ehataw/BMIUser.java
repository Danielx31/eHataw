package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class BMIUser extends AppCompatActivity {
    
    android.widget.Button mcalculate;
    EditText mcurrentheight, mcurrentweight;

    int currentprogess;
    String mintprogessheight = "170";
    String mintprogessweight = "70";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_user);

        mcalculate = findViewById(R.id.calculatebmi);
        mcurrentweight = findViewById(R.id.currentweight);
        mcurrentheight = findViewById(R.id.currentHeight);

    }
}