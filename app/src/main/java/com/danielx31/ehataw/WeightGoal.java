package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class WeightGoal extends AppCompatActivity {

    TextView mcurrentweight;
    SeekBar mseekbarforweight;
    int currentprogess;
    String minprogess = "55";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_goal);

        mseekbarforweight = findViewById(R.id.seekbarforweightgoal);
        mcurrentweight = findViewById(R.id.currentweightgoal);

        mseekbarforweight.setMax(300);
        mseekbarforweight.setProgress(55);
        mseekbarforweight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentprogess = i;
                minprogess = String.valueOf(currentprogess);
                mcurrentweight.setText(minprogess);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}