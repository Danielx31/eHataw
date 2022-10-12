package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ZumbaActivity extends AppCompatActivity {

    private StyledPlayerView styledPlayerView;
    private ExoPlayer exoPlayer;
    private String videoUrl;
    private boolean isOnline;

    private static final int BACK_PRESS_TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zumba);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoUrl = extras.getString("videoUrl");
            isOnline = extras.getBoolean("isOnline");
            //The key argument here must match that used in the other activity
        }

        styledPlayerView = findViewById(R.id.styledplayerview_zumba);
        exoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
        styledPlayerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
//        MediaItem mediaItem = new MediaItem.Builder()
//                .setUri(Uri.parse(sampleUrl))
//                .setMimeType(MimeTypes.APPLICATION_M3U8)
//                .build();
//        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
//        HlsMediaSource hlsMediaSource =
//                new HlsMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(mediaItem);
//        exoPlayer.addMediaSource(hlsMediaSource);
        exoPlayer.addMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
    }

    public void addToHistory() {
        if (!isOnline) {
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference userReference = database.collection("users").document(auth.getCurrentUser().getUid());

        userReference.set(new HashMap<>());


    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

    @Override
    public void onBackPressed()
    {
        if (backPressed + BACK_PRESS_TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            finishActivity(0);
            return;
        }
        else { Toast.makeText(getBaseContext(), "Press again to exit!", Toast.LENGTH_SHORT).show(); }

        backPressed = System.currentTimeMillis();
    }
}