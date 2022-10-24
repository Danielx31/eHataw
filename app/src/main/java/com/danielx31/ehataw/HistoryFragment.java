package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HistoryFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private CollectionReference zumbasReference;
    private DocumentReference userReference;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ZumbaAdapter zumbaPagingAdapter;

    private final String ZUMBA_COLLECTION = "zumba";
    private final String USERS_COLLECTION = "users";
    private final String WATCHLIST_FIELD = "watchlist";
    private final String HISTORY_FIELD = "history";

    private List<Zumba> zumbaList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxJavaPlugins.setErrorHandler(e -> {
        });

        zumbaList = new ArrayList<>();

        connectionReceiver = new ConnectionReceiver();
        initializeDatabase();

        swipeRefreshLayout = getView().findViewById(R.id.history_swiperefreshlayout_zumba);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        buildRecyclerView(buildRecyclerAdapter());
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
        swipeRefreshLayout.setRefreshing(true);
        zumbaList.clear();
        zumbaPagingAdapter.notifyDataSetChanged();
        userReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isComplete()) {
                            swipeRefreshLayout.setRefreshing(false);

                            if (!task.isSuccessful()) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }

                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (!documentSnapshot.exists()) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }

                            User user = documentSnapshot.toObject(User.class);

                            List<String> history = user.getHistory();

                            if (history == null || history.isEmpty()) {
                                zumbaPagingAdapter.notifyDataSetChanged();
                                return;
                            }

                            Collections.reverse(history);

                            for (int index = 0; index < history.size(); index += 10) {

                                int limitIndex = index + 10;

                                if (limitIndex > history.size()) {
                                    limitIndex = history.size();
                                }

                                List<String> historySubList = history.subList(index, limitIndex);

                                zumbasReference.whereIn(FieldPath.documentId(), historySubList)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot zumbaSnapshot : queryDocumentSnapshots) {
                                                    Zumba zumba = zumbaSnapshot.toObject(Zumba.class);
                                                    zumbaList.add(zumba);
                                                }

                                                if (history.size() == zumbaList.size()) {
                                                    swipeRefreshLayout.setRefreshing(false);
                                                }
                                                zumbaPagingAdapter.notifyDataSetChanged();
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
                    }
                });
    }

    public RecyclerView.Adapter buildRecyclerAdapter() {

        Query query = zumbasReference.whereEqualTo("forEmptyQuery", "");

        zumbaPagingAdapter = new ZumbaAdapter(zumbaList);
        refreshData();

        zumbaPagingAdapter.setOnItemClicklistener(new ZumbaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                watchZumba(zumbaList.get(position));
            }

            @Override
            public void onPopupMenuImageButtonClick(View view, int position) {
                showPopupMenu(view, zumbaList.get(position));
            }
        });

        return zumbaPagingAdapter;
    }

    public void buildRecyclerView(RecyclerView.Adapter adapter) {
        recyclerView = getView().findViewById(R.id.history_recyclerview_zumba);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(10);
        recyclerView.addItemDecoration(spacingItemDecoration);
    }

    public void showPopupMenu(View view, Zumba zumba) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.popupmenu_zumbaitem_option);
        MenuItem saveToWatchlist = popupMenu.getMenu().findItem(R.id.item_save_to_watchlist);
        popupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Remove");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_download:
                        startDownload(zumba);
                        return true;
                    case 2:
                        showRemoveAlertDialog(zumba.getId());
                        return true;
                    default:
                        return false;
                }
            }
        });

        userReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isComplete()) {

                            if (!task.isSuccessful()) {
                                setPopupMenuSelection(popupMenu, zumba, false);
                                return;
                            }

                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (!documentSnapshot.exists()) {
                                setPopupMenuSelection(popupMenu, zumba, false);
                                return;
                            }

                            User user = documentSnapshot.toObject(User.class);

                            if (user.getWatchlist() == null) {
                                setPopupMenuSelection(popupMenu, zumba, false);
                                return;
                            }
                            boolean isWatchlistContains = user.getWatchlist().contains(zumba.getId());

                            if (isWatchlistContains) {
                                saveToWatchlist.setTitle("Unsave");
                            }

                            setPopupMenuSelection(popupMenu, zumba, isWatchlistContains);
                        }

                    }
                });

        popupMenu.show();
    }

    public void setPopupMenuSelection(PopupMenu popupMenu, Zumba zumba, boolean inWatchlist) {
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_save_to_watchlist:
                        if (inWatchlist) {
                            removeFromWatchlist(zumba.getId());
                            return true;
                        }
                        saveToWatchlist(zumba.getId());
                        return true;
                    case R.id.item_download:
                        startDownload(zumba);
                        return true;
                    case 2:
                        showRemoveAlertDialog(zumba.getId());
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void watchZumba(Zumba zumba) {
        if (zumba == null) {
            Toast.makeText(getContext(), "Cannot load zumba!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), ZumbaActivity.class);
        intent.putExtra("isOnline", true);
        intent.putExtra("zumbaId", zumba.getId());
        intent.putExtra("videoPath", zumba.getVideoUrl());
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
        ZumbaListController zumbaListController = new ZumbaListController(sharedPreferences, folder);

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

    public void saveToWatchlist(String zumbaId) {
        userReference.set(new HashMap<>(), SetOptions.merge());

        userReference.update(WATCHLIST_FIELD, FieldValue.arrayUnion(zumbaId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Saved to Watchlist!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to Save!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void removeFromWatchlist(String zumbaId) {
        userReference.update(WATCHLIST_FIELD, FieldValue.arrayRemove(zumbaId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Unsaved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to Unsave!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void removeFromHistory(String zumbaId) {
        userReference.update(HISTORY_FIELD, FieldValue.arrayRemove(zumbaId))
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

    private void showRemoveAlertDialog(String zumbaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Remove History");
        builder.setMessage("Are you sure you want to delete this item?\nThis action cannot be undone");
        builder.setCancelable(false);

        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeFromHistory(zumbaId);
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
