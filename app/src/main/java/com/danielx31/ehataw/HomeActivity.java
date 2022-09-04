package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.model.Zumba;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ZumbaAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    public final String sampleUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
    public final String sampleThumbnailUrl = "https://upload.wikimedia.org/wikipedia/commons/a/a7/Big_Buck_Bunny_thumbnail_vlc.png";

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    MenuFragment menuFragment = new MenuFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        List<Zumba> zumbaList = new ArrayList<>();
        zumbaList.add(new Zumba(sampleUrl, sampleThumbnailUrl, "Big Buck Bunny", "This is a Description Text"));
        zumbaList.add(new Zumba(sampleUrl, sampleThumbnailUrl, "Big Buck Bunny2", "2This is a Description Text2"));
        zumbaList.add(new Zumba(sampleUrl, sampleThumbnailUrl, "Big Buck Bunny3", "3This is a Description Text3"));
        zumbaList.add(new Zumba(sampleUrl, sampleThumbnailUrl, "Big Buck Bunny4", "4This is a Description Text4"));

        buildRecyclerView(zumbaList);

        Log.d("EGG", zumbaList.get(0).getTitle());
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
//
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem item) {
//                switch(item.getItemId()){
//                    case R.id.home:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
//                        return true;
//                    case R.id.menu:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.container, menuFragment).commit();
//                        return true;
//                }
//
//                return false;
//            }
//        });

    }

    public void buildRecyclerView(List list) {
        recyclerView = findViewById(R.id.recyclerview_zumba);
        recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewAdapter = new ZumbaAdapter(list);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClicklistener(new ZumbaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }

            @Override
            public void onPopupMenuImageButtonClick(View view, int position) {
                showPopup(view);
            }
        });
    }

    public void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_save_to_watchlist:
                        //Toast.makeText(getApplicationContext(), "Save To Watchlist", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item_download:
                        //Toast.makeText(getApplicationContext(), "Download", Toast.LENGTH_SHORT).show();
                        startDownload();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.popupmenu_zumbaitem_option);
        popupMenu.show();
    }

    private void startDownload() {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("url", sampleUrl);
        startService(intent);
    }

}