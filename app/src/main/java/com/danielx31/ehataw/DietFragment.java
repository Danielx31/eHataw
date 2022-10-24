package com.danielx31.ehataw;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;


public class DietFragment extends Fragment {

    RecyclerView mRecyclerView;
    List<FoodData> foodDataList;
    FoodData mfoodData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet, container, false);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        mRecyclerView = view.findViewById(R.id.recycler_view);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(DietFragment.this, 1);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(10);
        mRecyclerView.addItemDecoration(spacingItemDecoration);

        foodDataList = new ArrayList<>();

        mfoodData = new FoodData("Vegetables Salad", "A salad is a dish consisting of mixed, mostly natural ingredients with at least one raw ingredient.\n" +
                "\n" +
                "★Ingredients★\n" +
                "•5 romaine lettuce leaves, torn into bite size pieces\n" +
                "•5 radishes, chopped\n" +
                "•2 fresh tomatoes, chopped\n" +
                "•2 green onion, minced\n" +
                "•1 small jicama, peeled and julienned\n" +
                "•1 cucumber, peeled and chopped\n" +
                "•1 red bell pepper, chopped\n" +
                "•2 tablespoons olive oil\n" +
                "•1 ½ tablespoons lemon juice\n" +
                "•1 tablespoon pomegranate juice\n" +
                "•1 ½ teaspoons water\n" +
                "•1 clove garlic, minced\n" +
                "•1 teaspoon chopped fresh dill\n" +
                "•1 teaspoon chopped fresh basil\n" +
                "•1 teaspoon salt\n" +
                "•1 teaspoon ground black pepper\n" +
                "\n" +
                "★Directions★\n" +
                "Step 1\n" +
                "Place lettuce, radishes, tomatoes, green onion, jicama, cucumber, and bell pepper in a large salad bowl.\n" +
                "\n" +
                "Step 2\n" +
                "Whisk olive oil, lemon juice, pomegranate juice, water, garlic, dill, basil, salt, and black pepper in a small bowl. Drizzle dressing over the salad just before serving.\n" +
                "\n" +
                "Image:loveandlemons.com\n" +
                "Reference: https://www.allrecipes.com/recipe/141314/the-best-vegetable-salad/", "13 Calories", R.drawable.salad);
        foodDataList.add(mfoodData);

        mfoodData = new FoodData("Boiled Egg", "Boiled eggs are eggs, typically from a chicken, cooked with their shells unbroken, usually by immersion in boiling water.\n" +
                "\n" +
                "Instructions\n" +
                "Step 1\n" +
                "Place your eggs in a single layer on the bottom of your pot and cover with cold water. The water should be about an inch or so higher than the eggs. Cover the pot with a lid.\n" +
                "\n" +
                "Step 2\n" +
                "Over high heat, bring your eggs to a rolling boil.\n" +
                "\n" +
                "Step 3\n" +
                "Remove from heat and let stand in water for 10-12 minutes for large eggs. Reduce the time slightly for smaller eggs, and increase the standing time for extra-large eggs.\n" +
                "\n" +
                "Step 4\n" +
                "Drain water and immediately run cold water over eggs until cooled. Rapid cooling helps prevent a green ring from forming around the yolks.\n" +
                "\n" +
                "\n" +
                "Reference https://www.eggs.ca/eggs101/view/6/how-to-make-the-perfect-hard-boiled-egg\n" +
                "Image: Serious Eats / Julia Estrada", "74 Calories", R.drawable.boiled_egg);
        foodDataList.add(mfoodData);


        MyAdapter myAdapter = new MyAdapter(getActivity(), foodDataList);
        mRecyclerView.setAdapter(myAdapter);



        return view;
    }
}