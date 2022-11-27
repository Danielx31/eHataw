package com.danielx31.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class ZumbaActivity extends AppCompatActivity {

    private StyledPlayerView styledPlayerView;
    private ExoPlayer exoPlayer;
    private boolean isOnline;
    private Zumba zumba;
    private UserAPI userAPI;
    private User user;

    private static final int BACK_PRESS_TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long backPressed;

    private final String TAG = "ZumbaActivity";
    private final String USERS_COLLECTION = "users";
    private final String HISTORY_FIELD = "history";

    private GestureDetector gestureDetector;
    private ZumbaDescriptionView zumbaDescriptionView;
    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintLayout bottomSheetLayout;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zumba);
        RxJavaPlugins.setErrorHandler(e -> { });

        styledPlayerView = findViewById(R.id.styledplayerview_zumba);
        bottomSheetLayout = findViewById(R.id.zumbavideo_bottomsheet);

        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(this);
        exoPlayer = new ExoPlayer.Builder(getApplicationContext())
                .setTrackSelector(defaultTrackSelector)
                .build();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        isOnline = extras.getBoolean("isOnline");
        String zumbaJson = extras.getString("zumba");

        if (zumbaJson == null || zumbaJson.isEmpty()) {
            Toast.makeText(this, "Cannot load video!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        boolean initializeZumba = initializeZumba(zumbaJson);

        if (!initializeZumba) {
            Toast.makeText(this, "Cannot load video!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (isOnline) {
            addViewCount();
            addToHistory();
        }



        styledPlayerView.setControllerShowTimeoutMs(2000);
        styledPlayerView.setPlayer(exoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(zumba.getVideoUrl());
//        MediaItem mediaItem = new MediaItem.Builder()
//                .setUri(Uri.parse(sampleUrl))
//                .setMimeType(MimeTypes.APPLICATION_M3U8)
//                .build();
//        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
//        HlsMediaSource hlsMediaSource =
//                new HlsMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(mediaItem);
//        exoPlayer.addMediaSource(hlsMediaSource);

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);

                if (playbackState == ExoPlayer.STATE_ENDED) {
                    // Code Pag Tapos na yung video
                    // pwede ka mag start ng activity
                    onVideoEnds();
                }
            }
        });

        exoPlayer.addMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);


        zumbaDescriptionView = new ZumbaDescriptionView(bottomSheetLayout, zumba, null);

        bottomSheetBehavior = BottomSheetBehavior.from(zumbaDescriptionView.getLayout());

        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        gestureDetector = new GestureDetector(this,new OnSwipeListener(){
            @Override
            public boolean onSwipe(Direction direction) {
                if (direction==Direction.up){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                }
                return true;
            }
        });

        styledPlayerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                styledPlayerView.showController();
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        setUser();

    }

    public void setUser() {
        userAPI = new UserAPI();

        userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
            @Override
            public void onFetchSuccess(User fetchedUser) {
                user = fetchedUser;
                zumbaDescriptionView = new ZumbaDescriptionView(bottomSheetLayout, zumba, user);
                bottomSheetBehavior = BottomSheetBehavior.from(zumbaDescriptionView.getLayout());
            }

            @Override
            public void onFetchNotFound() {
                Toast.makeText(getApplicationContext(), "A Network Error Occurred! Please Try Again", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(getApplicationContext(), "A Network Error Occurred! Please Try Again", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void onVideoEnds() {
        // Code Pag Tapos na yung video
        // pwede ka mag start ng activity or
        // gamitin mo description(message) mula sa zumba firebase

        Map<String, Object> systemTags = zumba.getSystemTags();

        Double MET = (Double) systemTags.get("MET");
        String kcal = MET + "\nMET";
        String benefit = zumba.getBenefit();
        String duration = zumba.getDuration();

        if (isOnline && user != null) {
            String[] durationParts = zumba.getDuration().split("");
            double minute = Double.parseDouble(durationParts[0]);
            kcal = calorieBurned(MET, user.getWeightInKg(), minute) + "\nCalories";
        }


        Intent intent = new Intent(this, BenefitActivity.class);
        intent.putExtra("message", benefit);
        intent.putExtra("kcal", kcal);
        intent.putExtra("duration", duration);
        startActivity(intent);
        finish();
    }

    private double calorieBurned(double MET, double weightInKg, double minute) {
        double calorieBurnedPerMinute = (MET * weightInKg * 3.5) / 200;
        return calorieBurnedPerMinute * minute;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Code kapag binack yung app kahit di pa tapos
        if (exoPlayer != null) {
            exoPlayer.stop();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Kapag finish activity na
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }

    public boolean initializeZumba(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }

        Gson gson = new Gson();
        this.zumba = gson.fromJson(json, new TypeToken<Zumba>(){}.getType());
        if (zumba == null) {
            return false;
        }

        return true;
    }

    public boolean initializeUser(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }

        Gson gson = new Gson();
        this.user = gson.fromJson(json, new TypeToken<User>(){}.getType());
        if (user == null) {
            return false;
        }

        return true;
    }

    public void addViewCount() {
        if (!isOnline) {
            return;
        }

        String zumbaId = zumba.getId();
        if (zumbaId == null || zumbaId.isEmpty()) {
            return;
        }

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final String ZUMBA_COLLECTION = "zumba";
        final String VIEW_COUNT_FIELD = "viewCount";

        DocumentReference zumbaReference = database.collection(ZUMBA_COLLECTION).document(zumbaId);
        zumbaReference.update(VIEW_COUNT_FIELD, FieldValue.increment(1));
    }

    public void addToHistory() {
        if (!isOnline) {
            return;
        }

        String zumbaId = zumba.getId();
        if (zumbaId == null || zumbaId.isEmpty()) {
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference userReference = database.collection(USERS_COLLECTION).document(auth.getCurrentUser().getUid());

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