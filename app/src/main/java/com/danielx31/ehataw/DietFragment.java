package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielx31.ehataw.firebase.firestore.model.Food;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.danielx31.ehataw.firebase.firestore.model.api.FoodAPI;
import com.danielx31.ehataw.firebase.firestore.view.FoodPagingAdapter;
import com.danielx31.ehataw.firebase.firestore.view.ZumbaPagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class DietFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;
    private FoodAPI foodAPI;

    private FoodPagingAdapter foodPagingAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diet, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(connectionReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(connectionReceiver);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        connectionReceiver = new ConnectionReceiver();
        foodAPI = new FoodAPI();


        swipeRefreshLayout = getView().findViewById(R.id.diet_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                foodPagingAdapter.refresh();
            }
        });


        buildRecyclerAdapter(foodAPI.queryByDate());
        buildRecyclerView();

    }

    public FirestorePagingOptions<Food> createPagingOptions(Query query) {
        PagingConfig config = new PagingConfig(10, 2, false);

        return new FirestorePagingOptions.Builder<Food>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Food.class)
                .build();
    }

    public void buildRecyclerAdapter(Query query) {
        foodPagingAdapter = new FoodPagingAdapter(createPagingOptions(query));

        foodPagingAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates states) {
                LoadState refresh = states.getRefresh();
                LoadState append = states.getAppend();

                if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
                    // The previous load (either initial or additional) failed. Call
                    // the retry() method in order to retry the load operation.
                    // ...
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (refresh instanceof LoadState.Loading) {
                    // The initial Load has begun
                    // ...
                    swipeRefreshLayout.setRefreshing(true);
                }

                if (append instanceof LoadState.Loading) {
                    // The adapter has started to load an additional page
                    // ...
                    swipeRefreshLayout.setRefreshing(true);
                }

                if (append instanceof LoadState.NotLoading) {
                    LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
                    if (notLoading.getEndOfPaginationReached()) {
                        // The adapter has finished loading all of the data set
                        // ...
                        swipeRefreshLayout.setRefreshing(false);
                        return null;
                    }

                    if (refresh instanceof LoadState.NotLoading) {
                        // The previous load (either initial or additional) completed
                        // ...
                        swipeRefreshLayout.setRefreshing(false);
                        return null;
                    }
                }
                return null;
            }
        });

        foodPagingAdapter.setOnItemClickListener(new FoodPagingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Food food = documentSnapshot.toObject(Food.class);

                Intent intent = new Intent(getContext(), DetailFoodActivity.class);
                Gson gson = new Gson();
                String foodJson = gson.toJson(food);
                intent.putExtra("food", foodJson);
                startActivity(intent);
            }
        });
    }

    public void buildRecyclerView() {
        if (foodPagingAdapter == null) {
            return;
        }

        recyclerView = getView().findViewById(R.id.diet_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(foodPagingAdapter);

        SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(10);
        recyclerView.addItemDecoration(spacingItemDecoration);
    }


}