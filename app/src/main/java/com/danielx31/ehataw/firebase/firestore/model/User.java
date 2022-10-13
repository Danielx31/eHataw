package com.danielx31.ehataw.firebase.firestore.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;
import java.util.List;

public class User {

    private String id;

    private List<String> watchlist;

    private List<String> history;

    public User() {

    }

    public User(List<String> watchlist, List<String> history) {
        this.watchlist = watchlist;
        this.history = history;
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

    public List<String> getHistory() { return history; }
}
