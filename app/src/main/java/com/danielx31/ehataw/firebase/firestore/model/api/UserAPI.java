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
        if (isUserLoggedIn()) {
            userReference = database.collection(USERS_COLLECTION).document(auth.getCurrentUser().getUid());
            userId = auth.getCurrentUser().getUid();
        }
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public boolean isUserEmailVerified() {
        return auth.getCurrentUser().isEmailVerified();
    }

    public String getUserId() {
        return userId;
    }

    public void onUserCalibrated(OnUserCalibratedListener onUserCalibratedListener) {
        fetchUser(new OnFetchUserListener() {
            @Override
            public void onFetchSuccess(User fetchedUser) {
                User user = fetchedUser;
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

            @Override
            public void onFetchNotFound() {
                onUserCalibratedListener.onUserNotCalibrated();
            }

            @Override
            public void onFetchError(Exception e) {
                onUserCalibratedListener.onValidatingFailed(e);
            }
        });
    }

    public interface OnUserCalibratedListener {
        void onUserCalibrated();
        void onUserNotCalibrated();
        void onValidatingFailed(Exception e);
    }

    public void setWeight(Object weight, OnSetListener onSetListener) {
        HashMap<String, Object> bodySize = new HashMap<>();
        bodySize.put("weight", weight);
        userReference.set(bodySize, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onSetListener.onSetSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onSetListener.onSetError(e);
                    }
                });
    }

    public void setBodySize(Object weight, Object height, OnSetListener onSetListener) {
        HashMap<String, Object> bodySize = new HashMap<>();
        bodySize.put("weight", weight);
        bodySize.put("height", height);
        userReference.set(bodySize, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onSetListener.onSetSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onSetListener.onSetError(e);
                    }
                });
    }

    public void setWeightGoal(Object weightGoal, OnSetListener onSetListener) {
        HashMap<String, Object> weightGoalMap = new HashMap<>();
        weightGoalMap.put("weightGoal", weightGoal);
        userReference.set(weightGoalMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onSetListener.onSetSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onSetListener.onSetError(e);
                    }
                });

    }

    public void setHealthConditions(Object healthConditions, OnSetListener onSetListener) {
        HashMap<String, Object> healthConditionsMap = new HashMap<>();
        healthConditionsMap.put("healthConditions", healthConditions);
        userReference.set(healthConditionsMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onSetListener.onSetSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onSetListener.onSetError(e);
                    }
                });
    }

    public void fetchUser(OnFetchUserListener onFetchUserListener) {
        userReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            onFetchUserListener.onFetchNotFound();
                        }

                        User user = documentSnapshot.toObject(User.class);
                        onFetchUserListener.onFetchSuccess(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFetchUserListener.onFetchError(e);
                    }
                });
    }

    public interface OnSetListener {
        void onSetSuccess();
        void onSetError(Exception error);
    }

    public interface OnFetchUserListener {
        void onFetchSuccess(User fetchedUser);
        void onFetchNotFound();
        void onFetchError(Exception e);
    }


}
