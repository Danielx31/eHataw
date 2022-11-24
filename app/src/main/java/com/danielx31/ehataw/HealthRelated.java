package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class HealthRelated extends AppCompatActivity {

    CheckBox cbAsthma, cbDiabetes, cbHeartDiseases, cbHighBlood, cbObesity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_related);

        cbAsthma =  findViewById(R.id.cb_asthma);
        cbDiabetes =  findViewById(R.id.cb_diabetes);
        cbHeartDiseases =  findViewById(R.id.cb_heartdisease);
        cbHighBlood =  findViewById(R.id.cb_highbloodpressure);
        cbObesity =  findViewById(R.id.cb_obesity);

        cbAsthma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbAsthma.isChecked()){
                    cbAsthma.setTextColor(getResources().getColor(R.color.orange));
                }else{
                    cbAsthma.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        cbDiabetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbDiabetes.isChecked()){
                    cbDiabetes.setTextColor(getResources().getColor(R.color.orange));
                }else{
                    cbDiabetes.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        cbHeartDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbHeartDiseases.isChecked()){
                    cbHeartDiseases.setTextColor(getResources().getColor(R.color.orange));
                }else{
                    cbHeartDiseases.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        cbHighBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbHighBlood.isChecked()){
                    cbHighBlood.setTextColor(getResources().getColor(R.color.orange));
                }else{
                    cbHighBlood.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        cbObesity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbObesity.isChecked()){
                    cbObesity.setTextColor(getResources().getColor(R.color.orange));
                }else{
                    cbObesity.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

    }
}