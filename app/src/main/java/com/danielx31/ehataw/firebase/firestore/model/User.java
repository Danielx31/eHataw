package com.danielx31.ehataw.firebase.firestore.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;
import java.util.List;

public class User {

    private String id;
    private String weight;
    private String height;
    private String weightGoal;
    private List<String> healthConditions;
    private List<String> history;
    private List<String> watchlist;

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

    public String getWeight() { return weight; }

    public String getHeight() {return height; }

    public String getWeightGoal() { return weightGoal; }

    @Exclude
    public Double getWeightInKg() {
        return getKg(weight);
    }

    @Exclude
    public Double getHeightInCm() {
        return getKg(height);
    }

    @Exclude
    public Double getWeightGoalInKg() {
        return getKg(weightGoal);
    }

    @Exclude
    private Double getKg(String data) {
        String[] parts = data.split(" ");
        return Double.parseDouble(parts[0]);
    }

    public List<String> getHealthConditions() { return healthConditions; }

    public List<String> getHistory() { return history; }

    public List<String> getWatchlist() {
        return watchlist;
    }



}
