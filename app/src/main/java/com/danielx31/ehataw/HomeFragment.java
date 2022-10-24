package com.danielx31.ehataw;

import android.Manifest;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";

    private BroadcastReceiver connectionReceiver;

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private CollectionReference zumbasReference;
    private DocumentReference userReference;

    private final String ZUMBA_COLLECTION = "zumba";
    private final String USERS_COLLECTION = "users";
    private final String WATCHLIST_FIELD = "watchlist";
    private final String TITLE_FIELD = "title";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ZumbaPagingAdapter zumbaPagingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxJavaPlugins.setErrorHandler(e -> {
        });

        connectionReceiver = new ConnectionReceiver();
        initializeDatabase();

        swipeRefreshLayout = getView().findViewById(R.id.watchlist_swiperefreshlayout_zumba);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                zumbaPagingAdapter.refresh();
            }
        });

        PermissionManager permissionManager = new PermissionManager(getContext(), getActivity());
        permissionManager.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 41);

        Spinner categorySpinner = getView().findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> categorySpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categorySpinnerAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category = adapterView.getItemAtPosition(i).toString();

                Query query = zumbasReference.orderBy("createdDate", Query.Direction.DESCENDING);

                switch (category) {
                    case "All":
                        query = zumbasReference.orderBy("createdDate", Query.Direction.DESCENDING);
                        break;
                    case "Whole Body":
                        query = zumbasReference.whereEqualTo("category", "Whole Body")
                                .orderBy("createdDate", Query.Direction.DESCENDING);
                        break;
                    case "Arms":
                        query = zumbasReference.whereEqualTo("category", "Arms")
                                .orderBy("createdDate", Query.Direction.DESCENDING);
                        break;
                    case "Belly":
                        query = zumbasReference.whereEqualTo("category", "Belly")
                                .orderBy("createdDate", Query.Direction.DESCENDING);
                        break;
                    case "Legs":
                        query = zumbasReference.whereEqualTo("category", "Legs")
                                .orderBy("createdDate", Query.Direction.DESCENDING);
                        break;
                }

                zumbaPagingAdapter.updateOptions(createPagingOptions(query));
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buildRecyclerView(buildRecyclerAdapter());

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(connectionReceiver, filter);
        zumbaPagingAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(connectionReceiver);
        zumbaPagingAdapter.stopListening();
    }

    public void initializeDatabase() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        zumbasReference = database.collection(ZUMBA_COLLECTION);
        userReference = database.collection(USERS_COLLECTION).document(auth.getCurrentUser().getUid());
    }

    public FirestorePagingOptions<Zumba> createPagingOptions(Query query) {
        PagingConfig config = new PagingConfig(10, 2, false);

        return new FirestorePagingOptions.Builder<Zumba>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Zumba.class)
                .build();
    }

    public RecyclerView.Adapter buildRecyclerAdapter() {
        Query query = zumbasReference.orderBy("createdDate", Query.Direction.DESCENDING);

        zumbaPagingAdapter = new ZumbaPagingAdapter(createPagingOptions(query));

        zumbaPagingAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
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

        zumbaPagingAdapter.setOnItemClickListener(new ZumbaPagingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Zumba zumba = documentSnapshot.toObject(Zumba.class);

                watchZumba(zumba);
            }

            @Override
            public void onPopupMenuClick(View view, DocumentSnapshot documentSnapshot, int position) {
                Zumba zumba = documentSnapshot.toObject(Zumba.class);
                showPopupMenu(view, zumba);
            }

        });
        return zumbaPagingAdapter;
    }

    public void buildRecyclerView(RecyclerView.Adapter adapter) {
        recyclerView = getView().findViewById(R.id.watchlist_recyclerview_zumba);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(10);
        recyclerView.addItemDecoration(spacingItemDecoration);
    }

    public void showPopupMenu(View view, Zumba zumba) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.popupmenu_zumbaitem_option);
        MenuItem saveToWatchlist = popupMenu.getMenu().findItem(R.id.item_save_to_watchlist);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_download:
                        startDownload(zumba);
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
                        Toast.makeText(getContext(), "Removed!", Toast.LENGTH_SHORT).show();
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
