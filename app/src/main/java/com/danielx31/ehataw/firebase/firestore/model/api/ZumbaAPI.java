package com.danielx31.ehataw.firebase.firestore.model.api;

import androidx.annotation.NonNull;

import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import java8.util.stream.StreamSupport;
import java8.util.Maps;

public class ZumbaAPI {

    private FirebaseFirestore database;
    private final String ZUMBA_COLLECTION = "zumba";
    private CollectionReference zumbasReference;

    public ZumbaAPI() {
        database = FirebaseFirestore.getInstance();
        zumbasReference = database.collection(ZUMBA_COLLECTION);
    }

    public interface OnFetchRecommendationListener {
        void onSuccess(List<Zumba> zumbaList);
        void onError(Exception error);
    }

    public void fetchRecommendation(double userBMI, List<String> healthConditions, OnFetchRecommendationListener onFetchRecommendationListener) {
        zumbasReference.whereLessThanOrEqualTo("systemTags.minBMI", userBMI)
                .orderBy("systemTags.minBMI", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Zumba> zumbaList = new ArrayList<>();
                        for (QueryDocumentSnapshot zumbaSnapshot : queryDocumentSnapshots) {
                            Zumba zumba = zumbaSnapshot.toObject(Zumba.class);

                            Map<String, Object> systemTags = zumba.getSystemTags();
                            Double zumbaMaxBMI = (Double) systemTags.get("maxBMI");

                            if (userBMI > zumbaMaxBMI) {
                                continue;
                            }

                            Map<String, String> zumbaLimitHealthConditions = (Map<String, String>) systemTags.get("limitHealthConditions");

                            boolean containsHealthCondition = false;

                            for (String healthCondition : healthConditions) {
                                Iterator<String> zumbaLimitHealthConditionsIterator = zumbaLimitHealthConditions.keySet().iterator();
                                while (zumbaLimitHealthConditionsIterator.hasNext()) {
                                    String zumbaLimitHealthCondition = zumbaLimitHealthConditionsIterator.next();

                                    if (healthCondition.equals(zumbaLimitHealthCondition)) {
                                        containsHealthCondition = true;
                                        break;
                                    }
                                }

                                if (containsHealthCondition) {
                                    break;
                                }

                            }

                            if (containsHealthCondition) {
                                continue;
                            }

                            zumbaList.add(zumba);
                        }
                        onFetchRecommendationListener.onSuccess(zumbaList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFetchRecommendationListener.onError(e);
                    }
                });

    }


}
