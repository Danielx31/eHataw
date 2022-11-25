package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danielx31.ehataw.firebase.firestore.model.Food;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class DetailFoodActivity extends AppCompatActivity {

    private Food food;

    private TextView foodDescription;
    private ImageView foodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);

        foodDescription = (TextView) findViewById(R.id.txtDescription);
        foodImage = (ImageView) findViewById(R.id.ivImage2);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            Toast.makeText(this, "Cannot load Data!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String foodJson = bundle.getString("food");
        if (foodJson == null || foodJson.isEmpty()) {
            Toast.makeText(this, "Cannot load Data!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        boolean initializeFood = initializeFood(foodJson);
        if (!initializeFood) {
            Toast.makeText(this, "Cannot load Data!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Glide.with(foodImage)
                .load(food.getThumbnailUrl())
                .into(foodImage);
        foodDescription.setText(food.getFormattedDescription());

    }

    public boolean initializeFood(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }

        Gson gson = new Gson();
        this.food = gson.fromJson(json, new TypeToken<Food>(){}.getType());
        if (food == null) {
            return false;
        }

        return true;
    }
}