package com.danielx31.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;
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
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;

public class ZumbaActivity extends AppCompatActivity {

    private StyledPlayerView styledPlayerView;
    private ExoPlayer exoPlayer;
    private boolean isOnline;
    private String zumbaId;
    private String videoPath;

    private static final int BACK_PRESS_TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zumba);

        styledPlayerView = findViewById(R.id.styledplayerview_zumba);
        exoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        zumbaId = extras.getString("zumbaId");
        videoPath = extras.getString("videoPath");
        isOnline = extras.getBoolean("isOnline");

        if (videoPath == null || videoPath.isEmpty()) {
            Toast.makeText(this, "Cannot load Video!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (isOnline) {
            if (zumbaId == null || zumbaId.isEmpty()) {
                Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show();
                finishActivity(0);
                return;
            }
            addToHistory();
        }

        styledPlayerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(videoPath);
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

        userReference.set(new HashMap<>(), SetOptions.merge());

        userReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (!documentSnapshot.exists()) {
                                    addToHistory();
                                    return;
                                }

                                User user = documentSnapshot.toObject(User.class);
                                List<String> history = user.getHistory();

                                if (history == null) {
                                    userReference.update("history", FieldValue.arrayUnion(zumbaId));
                                    return;
                                }

                                if (history.contains(zumbaId)) {
                                    userReference.update("history", FieldValue.arrayRemove(zumbaId));
                                }

                                userReference.update("history", FieldValue.arrayUnion(zumbaId));
                            }
                        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
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