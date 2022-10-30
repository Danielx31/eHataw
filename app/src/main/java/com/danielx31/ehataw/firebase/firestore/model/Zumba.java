package com.danielx31.ehataw.firebase.firestore.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Zumba {

    private String id;
    private Date createdDate;
    private String videoUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private String category;
    private String benefit;
    private String calories;
    private String duration;

    public String getDuration() {
        return duration;
    }

    public String getCalories() {
        return calories;
    }

    private long viewCount;

    public Zumba() {

    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @DocumentId
    public String getId() {
        return id;
    }

    @ServerTimestamp
    public Date getCreatedDate() {
        return createdDate;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getBenefit() {
        return benefit;
    }

    public long getViewCount() {
        return viewCount;
    }

    @Override
    public String toString() {
        return "Zumba Id: " + id + "\n" +
                "Created Date: " + createdDate.toString() + "\n" +
                "Video Url: " + videoUrl + "\n" +
                "Thumbnail Url: " + thumbnailUrl + "\n" +
                "Title: " + title + "\n" +
                "Description: " + description + "\n" +
                "Category: " + category + "\n" +
                "Benefit: " + benefit + "\n" +
                "View Count: " + viewCount + "\n";
    }

}
