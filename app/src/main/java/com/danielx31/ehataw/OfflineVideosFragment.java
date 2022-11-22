package com.danielx31.ehataw;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.danielx31.ehataw.localData.controller.ZumbaListController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class OfflineVideosFragment extends Fragment {

    private final static String TAG = "DownloadFragment";

    private final String DOWNLOADED_VIDEOS_KEY = "downloadVideos";
    private ZumbaListController zumbaListController;

    private RecyclerView recyclerView;
    private ZumbaAdapter recyclerViewAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offline_videos, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxJavaPlugins.setErrorHandler(e -> {
        });

        initializeController();

        swipeRefreshLayout = getView().findViewById(R.id.offlinevideos_swiperefreshlayout_zumba);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        buildRecyclerAdapter(getList());
        buildRecyclerView();
    }

    public List<Zumba> getList() {
        // get item if video and thumbnail are exists.
        List<Zumba> zumbaList = zumbaListController.getList();

        if (zumbaList == null) {
            return new ArrayList<>();
        }

        Collections.reverse(zumbaList);

        Iterator<Zumba> zumbaIterator = zumbaList.iterator();
        while (zumbaIterator.hasNext()) {
            Zumba zumba = zumbaIterator.next();

            File thumbnail = new File(zumba.getThumbnailUrl());
            File video = new File(zumba.getVideoUrl());
            if (!thumbnail.exists()) {
                zumbaIterator.remove();
                continue;
            }
            if (!video.exists()) {
                zumbaIterator.remove();
                continue;
            }
        }

        return zumbaList;
    }

    public void initializeController() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(DOWNLOADED_VIDEOS_KEY, Context.MODE_PRIVATE);
        File folder = new File(getActivity().getExternalFilesDir("offline").toString());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        this.zumbaListController = new ZumbaListController(sharedPreferences, userId, folder);

    }

    public void buildRecyclerAdapter(List list) {

        if (list == null || list.isEmpty()) {
            recyclerViewAdapter = new ZumbaAdapter(new ArrayList<>());
            return;
        }

        recyclerView = getView().findViewById(R.id.offlinevideos_recyclerview_zumba);
        swipeRefreshLayout.setRefreshing(true);

        recyclerViewAdapter.setOnItemClicklistener(new ZumbaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Zumba zumba = getList().get(position);
                watchZumba(zumba);
            }

            @Override
            public void onPopupMenuImageButtonClick(View view, int position) {
                Zumba zumba = getList().get(position);
                showPopupMenu(view, zumba);
            }
        });

        swipeRefreshLayout.setRefreshing(false);
    }

    public void buildRecyclerView() {
        if (recyclerViewAdapter == null) {
            return;
        }

        recyclerView = getView().findViewById(R.id.offlinevideos_recyclerview_zumba);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

        SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(10);
        recyclerView.addItemDecoration(spacingItemDecoration);
    }

    public void watchZumba(Zumba zumba) {
        if (zumba == null) {
            Toast.makeText(getContext(), "Cannot load zumba!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), ZumbaActivity.class);
        intent.putExtra("isOnline", false);

        Gson gson = new Gson();
        String zumbaJson = gson.toJson(zumba);
        intent.putExtra("zumba", zumbaJson);
        startActivity(intent);
    }

    public void showPopupMenu(View view, Zumba zumba) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, "Delete");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        showRemoveAlertDialog(zumba.getId());
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    public void delete(String zumbaId) {
        if (zumbaId == null || zumbaId.isEmpty()) {
            return;
        }

        zumbaListController.remove(zumbaId);
        updateRecyclerView();
    }

    public void updateRecyclerView() {
        List<Zumba> zumbaList = getList();
        if (zumbaList == null || zumbaList.isEmpty()) {
            zumbaList = new ArrayList<>();
        }

        recyclerViewAdapter.setZumbaList(zumbaList);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void showRemoveAlertDialog(String zumbaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Video");
        builder.setMessage("Are you sure you want to delete this item?\nThis action cannot be undone");
        builder.setCancelable(false);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete(zumbaId);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}