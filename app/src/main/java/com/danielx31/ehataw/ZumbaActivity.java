package com.danielx31.ehataw;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

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

public class ZumbaActivity extends AppCompatActivity {

    private StyledPlayerView styledPlayerView;
    private ExoPlayer exoPlayer;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zumba);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            videoUrl = extras.getString("videoUrl");
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
}