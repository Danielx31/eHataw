package com.danielx31.ehataw.firebase.firestore.model.api;

import androidx.annotation.NonNull;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;

public class UserAPI {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private DocumentReference userReference;
    private final String USERS_COLLECTION = "users";
    private String userId;

    public UserAPI() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        userReference = database.collection(USERS_COLLECTION).document(auth.getCurrentUser().getUid());
        userId = auth.getCurrentUser().getUid();
    }

    public String getUserId() {
        return userId;
    }

    public void onUserCalibrated(OnUserCalibratedListener onUserCalibratedListener) {
        userReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            onUserCalibratedListener.onUserNotCalibrated();
                            return;
                        }

                        User user = documentSnapshot.toObject(User.class);
                        String weight = user.getWeight();
                        String height = user.getHeight();
                        String weightGoal = user.getWeightGoal();
                        List<String> healthConditions = user.getHealthConditions();

                        if (weight == null || weight.isEmpty() ||
                        height == null || height.isEmpty() ||
                        weightGoal == null || weightGoal.isEmpty() ||
                        healthConditions == null) {
                            onUserCalibratedListener.onUserNotCalibrated();
                            return;
                        }

                        onUserCalibratedListener.onUserCalibrated();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onUserCalibratedListener.onValidatingFailed(e);
                    }
                });
    }

    public interface OnUserCalibratedListener {
        void onUserCalibrated();
        void onUserNotCalibrated();
        void onValidatingFailed(Exception e);
    }

    public void setBodySize(Object weight, Object height) {
        HashMap<String, Object> bodySize = new HashMap<>();
        bodySize.put("weight", weight);
        bodySize.put("height", height);
        userReference.set(bodySize, SetOptions.merge());
    }

    public void setWeightGoal(Object weightGoal) {
        HashMap<String, Object> weightGoalMap = new HashMap<>();
        weightGoalMap.put("weightGoal", weightGoal);
        userReference.set(weightGoalMap, SetOptions.merge());
    }

    public void setHealthConditions(Object healthConditions) {
        HashMap<String, Object> healthConditionsMap = new HashMap<>();
        healthConditionsMap.put("healthConditions", healthConditions);
        userReference.set(healthConditionsMap, SetOptions.merge());
    }


}
