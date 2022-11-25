package com.danielx31.ehataw.firebase.firestore.model.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FoodAPI {

    private FirebaseFirestore database;
    private final String FOOD_COLLECTION = "food";
    private CollectionReference foodsReference;

    public FoodAPI() {
        database = FirebaseFirestore.getInstance();
        foodsReference = database.collection(FOOD_COLLECTION);
    }

    public Query queryByDate() {
        return foodsReference.orderBy("createdDate", Query.Direction.DESCENDING);
    }

}
