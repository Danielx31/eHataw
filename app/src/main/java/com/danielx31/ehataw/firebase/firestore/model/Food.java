package com.danielx31.ehataw.firebase.firestore.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Food {

    private String id;
    private Date createdDate;
    private String thumbnailUrl;
    private String name;
    private String description;
    private String category;
    private List<String> nutritionFacts;
    private String source;
    private Map<String, Object> systemTags;

    public Food() {

    }

    @DocumentId
    public String getId() {
        return id;
    }

    @ServerTimestamp
    public Date getCreatedDate() {
        return createdDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Exclude
    public String getFormattedDescription() {
        return description.replace("\\n", "\n");
    }

    public String getCategory() {
        return category;
    }

    public List<String> getNutritionFacts() {
        return nutritionFacts;
    }

    public String getSource() {
        return source;
    }

    public Map<String, Object> getSystemTags() {
        return systemTags;
    }
}
