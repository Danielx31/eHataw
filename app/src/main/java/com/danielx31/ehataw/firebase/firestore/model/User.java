package com.danielx31.ehataw.firebase.firestore.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;
import java.util.List;

public class User {

    private String id;

    List<String> watchlist;

    public User() {

    }

    public User(List<String> watchlist) {
        this.watchlist = watchlist;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public List<String> getWatchlist() {
        return watchlist;
    }
}
