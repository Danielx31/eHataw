package com.danielx31.ehataw.firebase.firestore.model.api;

import androidx.annotation.NonNull;

import com.danielx31.ehataw.DateManager;
import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.WeightLossData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WeightLossMonitorAPI {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private CollectionReference weightLossMonitorReference;
    private final String USERS_COLLECTION = "users";
    private final String WEIGHT_LOSS_MONITOR_COLLECTION = "weightLossMonitor";

    public WeightLossMonitorAPI() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() != null) {
            weightLossMonitorReference = database.collection(USERS_COLLECTION)
                    .document(auth.getCurrentUser().getUid())
                    .collection(WEIGHT_LOSS_MONITOR_COLLECTION);
        }
    }

    public interface OnSetListener {
        void onSetSuccess();
        void onSetFailed(String message);
        void onSetError(Exception error);
    }

    public void set(String id, Object data, String tag, OnSetListener onSetListener) {
        if (tag == null || tag.isEmpty()) {
            tag = "Set Weight Loss Data";
        }

        if (id == null || id.isEmpty()) {
            onSetListener.onSetFailed(tag + ": Id is invalid");
            return;
        }

        if (data == null) {
            onSetListener.onSetFailed(tag + ": Data is invalid");
            return;
        }

        weightLossMonitorReference.document(id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onSetListener.onSetSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onSetListener.onSetError(e);
                    }
                });
    }

    public void set(String id, WeightLossData weightLossData, String tag, OnSetListener onSetListener) {
        if (tag == null || tag.isEmpty()) {
            tag = "Set Weight Loss Data - Class";
        }

        String checkData = WeightLossData.check(weightLossData, tag);

        if (!checkData.isEmpty()) {
            onSetListener.onSetFailed(checkData);
            return;
        }

        set(id, weightLossData, tag, onSetListener);
    }

    public void setEndWeight(String endWeight, OnSetListener onSetListener) {
        Map<String, Object> data = new HashMap<>();
        data.put("endWeight", endWeight);

        set(getTodayToString(), endWeight, "Set End Weight", onSetListener);
    }

    public void add(WeightLossData weightLossData, OnSetListener onSetListener) {
        set(getTodayToString(), weightLossData, "Add Weight Loss Data", onSetListener);
    }

    public String getTodayToString() {
        DateManager dateManager = new DateManager(new Date());
        return  dateManager.toString();
    }

    public DocumentReference getDocumentReference(String id) {

        if (id == null || id.isEmpty()) {
            return null;
        }

        return weightLossMonitorReference.document(id);
    }

    public DocumentReference getDocumentReference() {
        DateManager dateManager = new DateManager(new Date());

        return weightLossMonitorReference.document(dateManager.toString());
    }

    public interface OnFetchListener {
        void onFetchSuccess(WeightLossData fetchedWeightLossData);
        void onFetchFailed(String message);
        void onFetchError(Exception error);

    }

    public void fetch(String id, String tag, OnFetchListener onFetchListener) {
        if (tag == null || tag.isEmpty()) {
            tag = "Fetch Weight Loss Data";
        }

        final String tagFinal = tag;

        if (id == null || id.isEmpty()) {
            onFetchListener.onFetchFailed(tag + ": Id is invalid");
            return;
        }

        getDocumentReference(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            onFetchListener.onFetchFailed(tagFinal + ": Document Snapshot not Found");
                            return;
                        }

                        WeightLossData weightLossData = documentSnapshot.toObject(WeightLossData.class);

                        if (weightLossData == null) {
                            onFetchListener.onFetchFailed(tagFinal +": Data is Null");
                            return;
                        }
                        onFetchListener.onFetchSuccess(weightLossData);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFetchListener.onFetchError(e);
                    }
                });

    }


}
