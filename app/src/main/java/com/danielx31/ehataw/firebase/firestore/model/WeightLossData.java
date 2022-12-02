package com.danielx31.ehataw.firebase.firestore.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.Calendar;
import java.util.Date;

public class WeightLossData {

    private String id;
    private Date date;
    private String startWeight;
    private String endWeight;

    public WeightLossData() {

    }

    public WeightLossData(Date date, String startWeight, String endWeight) {
        this.date = date;
        this.startWeight = startWeight;
        this.endWeight = endWeight;
    }

    @DocumentId
    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getStartWeight() {
        return startWeight;
    }

    public String getEndWeight() {
        return endWeight;
    }

    @Exclude
    public Double getStartWeightInKg() {
        return getKg(startWeight);
    }

    @Exclude
    public Double getEndWeightInKg() {
        return getKg(endWeight);
    }

    @Exclude
    private Double getKg(String data) {
        String[] parts = data.split(" ");
        return Double.parseDouble(parts[0]);
    }

    public static String check(WeightLossData weightLossData, String tag) {
        if (weightLossData == null) {
            return tag + ": Weight object is Null";
        }

        Date date = weightLossData.getDate();
        if (date == null) {
            return tag + ": date is Null";
        }

        String startWeight = weightLossData.getStartWeight();
        if (startWeight == null || startWeight.isEmpty()) {
            return tag + ": start weight is Null";
        }

        String endWeight = weightLossData.getEndWeight();
        if (endWeight == null || endWeight.isEmpty()) {
            return tag + ": end weight is Null";
        }

        return "";
    }
}
