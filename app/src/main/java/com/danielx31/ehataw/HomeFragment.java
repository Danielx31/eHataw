package com.danielx31.ehataw;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danielx31.ehataw.firebase.model.Zumba;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ZumbaAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private List<Zumba> zumbaList = new ArrayList<>();

    public final String sampleUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
    public final String sampleThumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/a/a7/Big_Buck_Bunny_thumbnail_vlc.png";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        zumbaList = new ArrayList<>();
        zumbaList.add(new Zumba(sampleUrl, sampleThumbnailUrl, "Big Buck Bunny", "Category Text"));
        zumbaList.add(new Zumba(sampleUrl, sampleThumbnailUrl, "Big Buck Bunny2", "Category Text2"));
        zumbaList.add(new Zumba(sampleUrl, sampleThumbnailUrl, "Big Buck Bunny3", "Category Text3"));
        zumbaList.add(new Zumba(sampleUrl, sampleThumbnailUrl, "Big Buck Bunny4", "Category Text4"));

        buildRecyclerView(zumbaList);

        PermissionManager permissionManager = new PermissionManager(getContext(), getActivity());
        permissionManager.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 41);

        Spinner categorySpinner = getView().findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> categorySpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categorySpinnerAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void buildRecyclerView(List list) {
        recyclerView = getView().findViewById(R.id.recyclerview_zumba);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewAdapter = new ZumbaAdapter(list);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                DividerItemDecoration.VERTICAL);

        SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(10);
        recyclerView.addItemDecoration(spacingItemDecoration);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClicklistener(new ZumbaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), ZumbaActivity.class);
                intent.putExtra("videoUrl", zumbaList.get(position).getVideoUrl());
                startActivity(intent);
            }

            @Override
            public void onPopupMenuImageButtonClick(View view, int position) {
                showPopup(view, zumbaList.get(position));
            }
        });
    }

    public void showPopup(View view, Zumba zumba) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_save_to_watchlist:
                        return true;
                    case R.id.item_download:
                        startDownload(zumba.getVideoUrl());
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.popupmenu_zumbaitem_option);
        popupMenu.show();
    }

    private void startDownload(String url) {
        Intent intent = new Intent(getContext(), DownloadService.class);
        intent.putExtra("url", url);
        getActivity().startService(intent);
    }


}
