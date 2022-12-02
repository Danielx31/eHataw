package com.danielx31.ehataw.firebase.firestore.model;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class User {

    private String id;
    private String weight;
    private String height;
    private List<String> healthConditions;
    private List<String> history;
    private List<String> watchlist;
    private Map<String, Object> goals;
    private Map<String, Object> systemTags;

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

    public Map<String, Object> getGoals() {
        return goals;
    }

    public Map<String, Object> getSystemTags() {
        return systemTags;
    }

    @Exclude
    public String getWeightGoal() { return String.valueOf(goals.get("weightGoal")); }

    @Exclude
    public String getWeightGoalFrom() {
        return String.valueOf(goals.get("weightGoalFrom"));
    }

    @Exclude
    public Double getWeightGoalFromInKg() {
        return getKg(getWeightGoalFrom());
    }

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
        return getKg(getWeightGoal());
    }

    @Exclude
    public long getZumbaCountGoalPerDay() {
        return (Long) goals.get("zumbaCountGoalPerDay");
    }

    @Exclude
    public Date getDailyMonitorDate() {
        return (Date) systemTags.get("dailyMonitorDate");
    }

    @Exclude
    public Double getWeightDecreasedPerDayInKg() {
        if (systemTags == null) {
            return null;
        }

        if (systemTags.get("weightDecreasedPerDay") == null) {
            return null;
        }

        return getKg((String) systemTags.get("weightDecreasedPerDay"));
    }

    @Exclude
    public long getZumbaFollowedCountPerDay() {
        if (systemTags == null) {
            return 0;
        }

        if (systemTags.get("zumbaFollowedCountPerDay") == null) {
            return 0;
        }

        return (long) systemTags.get("zumbaFollowedCountPerDay");
    }

    @Exclude
    public Timestamp getMonitorDate() {
        if (systemTags == null) {
            return null;
        }

        return (Timestamp) systemTags.get("monitorDate");
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
