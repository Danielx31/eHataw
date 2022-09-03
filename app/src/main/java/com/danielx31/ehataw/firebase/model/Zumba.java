package com.danielx31.ehataw.firebase.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Zumba {
    private String videoUrl;
    private String videoThumbnailUrl;
    private String title;
    private String description;

    public Zumba(String videoUrl, String videoThumbnailUrl, String title, String description) {
        this.videoUrl = videoUrl;
        this.videoThumbnailUrl = videoThumbnailUrl;
        this.title = title;
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
