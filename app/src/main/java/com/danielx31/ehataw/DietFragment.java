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

        mfoodData = new FoodData("Chicken Adobo", "Very Delicious", "Php 300", R.drawable.chicken_adobo);
        foodDataList.add(mfoodData);
        mfoodData = new FoodData("Salad", "Very Healthy", "Php 200", R.drawable.salad);
        foodDataList.add(mfoodData);

        MyAdapter myAdapter = new MyAdapter(getActivity(), foodDataList);
        mRecyclerView.setAdapter(myAdapter);



        return view;
    }
}