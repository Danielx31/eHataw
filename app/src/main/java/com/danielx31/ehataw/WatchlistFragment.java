package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.danielx31.ehataw.firebase.firestore.view.ZumbaPagingAdapter;
import com.danielx31.ehataw.localData.controller.ZumbaListController;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import java8.util.Comparators;
import java8.util.Lists;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class WatchlistFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private CollectionReference zumbasReference;
    private DocumentReference userReference;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ZumbaAdapter zumbaAdapter;

    private final String ZUMBA_COLLECTION = "zumba";
    private final String USERS_COLLECTION = "users";
    private final String WATCHLIST_FIELD = "watchlist";

    private List<Zumba> zumbaList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxJavaPlugins.setErrorHandler(e -> {
        });

        zumbaList = new ArrayList<>();

        connectionReceiver = new ConnectionReceiver();
        initializeDatabase();

        swipeRefreshLayout = getView().findViewById(R.id.watchlist_swiperefreshlayout_zumba);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
            }
        });

        buildRecyclerAdapter();
        buildRecyclerView();
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

    public void initializeDatabase() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        zumbasReference = database.collection(ZUMBA_COLLECTION);
        userReference = database.collection(USERS_COLLECTION).document(auth.getCurrentUser().getUid());
    }

    public void refreshData() {
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(true);
        zumbaList.clear();
        zumbaAdapter.notifyDataSetChanged();
        userReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isComplete()) {

                            if (!task.isSuccessful()) {
                                swipeRefreshLayout.setRefreshing(false);
                                swipeRefreshLayout.setEnabled(false);
                                return;
                            }

                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (!documentSnapshot.exists()) {
                                swipeRefreshLayout.setRefreshing(false);
                                swipeRefreshLayout.setEnabled(false);
                                return;
                            }

                            User user = documentSnapshot.toObject(User.class);

                            List<String> watchlist = user.getWatchlist();

                            if (watchlist == null || watchlist.isEmpty()) {
                                swipeRefreshLayout.setRefreshing(false);
                                swipeRefreshLayout.setEnabled(false);
                                return;
                            }

                            Collections.reverse(watchlist);

                            for (int index = 0; index < watchlist.size(); index += 10) {

                                int limitIndex = index + 10;

                                if (limitIndex > watchlist.size()) {
                                    limitIndex = watchlist.size();
                                }

                                List<String> watchlistSubList = watchlist.subList(index, limitIndex);

                                zumbasReference.whereIn(FieldPath.documentId(), watchlistSubList)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot zumbaSnapshot : queryDocumentSnapshots) {
                                                    Zumba zumba = zumbaSnapshot.toObject(Zumba.class);
                                                    zumbaList.add(zumba);
                                                }
                                                sortListByList(watchlist);
                                                zumbaAdapter.notifyDataSetChanged();
                                                swipeRefreshLayout.setRefreshing(false);
                                                swipeRefreshLayout.setEnabled(false);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                swipeRefreshLayout.setRefreshing(false);
                                                swipeRefreshLayout.setEnabled(false);
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(false);
                    }
                });
    }

    public void sortListByList(List<String> list) {
        Lists.sort(zumbaList, Comparators.comparing(z->list.indexOf(z.getId())));
    }

    public void buildRecyclerAdapter() {
        zumbaAdapter = new ZumbaAdapter(zumbaList);
        refreshData();

        zumbaAdapter.setOnItemClicklistener(new ZumbaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                watchZumba(zumbaList.get(position));
            }

            @Override
            public void onPopupMenuImageButtonClick(View view, int position) {
                showPopupMenu(view, zumbaList.get(position));
            }
        });

    }

    public void buildRecyclerView() {
        if (zumbaAdapter == null) {
            return;
        }

        recyclerView = getView().findViewById(R.id.watchlist_recyclerview_zumba);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(zumbaAdapter);

        SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(10);
        recyclerView.addItemDecoration(spacingItemDecoration);
    }

    public void showPopupMenu(View view, Zumba zumba) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.popupmenu_zumbaitem_option);
        MenuItem saveToWatchlist = popupMenu.getMenu().findItem(R.id.item_save_to_watchlist);

        saveToWatchlist.setTitle("Unsave");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_save_to_watchlist:
                        removeFromWatchlist(zumba.getId());
                        return true;
                    case R.id.item_download:
                        startDownload(zumba);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    public void watchZumba(Zumba zumba) {
        if (zumba == null) {
            Toast.makeText(getContext(), "Cannot load zumba!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), ZumbaActivity.class);
        intent.putExtra("isOnline", true);

        Gson gson = new Gson();
        String zumbaJson = gson.toJson(zumba);
        intent.putExtra("zumba", zumbaJson);
        startActivity(intent);
    }

    public void startDownload(Zumba zumba) {
        if (zumba == null) {
            Toast.makeText(getContext(), "Cannot download video!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String DOWNLOADED_VIDEOS_KEY = "downloadVideos";
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(DOWNLOADED_VIDEOS_KEY, Context.MODE_PRIVATE);
        File folder = new File(getActivity().getExternalFilesDir("offline").toString());
        String userId = auth.getCurrentUser().getUid();
        ZumbaListController zumbaListController = new ZumbaListController(sharedPreferences, userId, folder);

        if (DownloadService.isDownloading()) {
            showDownloadExistsAlertDialog();
            return;
        }

        if (zumbaListController.contains(zumba.getId())) {
            showReplaceAlertDialog(zumba);
            return;
        }

        Intent intent = new Intent(getContext(), DownloadService.class);
        Gson gson = new Gson();
        intent.putExtra("zumba", gson.toJson(zumba));
        getActivity().startService(intent);
        Toast.makeText(getContext(), "Download Started", Toast.LENGTH_SHORT).show();
    }

    public void removeFromWatchlist(String zumbaId) {
        userReference.update(WATCHLIST_FIELD, FieldValue.arrayRemove(zumbaId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Removed!", Toast.LENGTH_SHORT).show();
                        refreshData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to Remove!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showReplaceAlertDialog(Zumba zumba) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Replace Offline Video");
        builder.setMessage("Video is already exists! Are you sure you want to replace?");
        builder.setCancelable(false);

        builder.setPositiveButton("Replace", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getContext(), DownloadService.class);
                Gson gson = new Gson();
                intent.putExtra("zumba", gson.toJson(zumba));
                getActivity().startService(intent);
                Toast.makeText(getContext(), "Download Started", Toast.LENGTH_SHORT).show();
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

    public void showDownloadExistsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Download Failed");
        builder.setMessage("Download in progress! Please Try again later.");
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}