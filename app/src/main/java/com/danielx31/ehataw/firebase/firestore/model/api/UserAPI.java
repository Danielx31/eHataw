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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                Map<String, Object> goals = user.getGoals();
                List<String> healthConditions = user.getHealthConditions();

                if (weight == null || weight.isEmpty() ||
                        height == null || height.isEmpty() ||
                        goals == null || goals.isEmpty() ||
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

    public void setBodySize(Object weight, Object height, OnSetListener onSetListener) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("weight", weight);
        data.put("height", height);

        userReference.set(data, SetOptions.merge())
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

    public void setGoals(Map<String, Object> goals, OnSetListener onSetListener) {
        Map<String, Object> data = new HashMap<>();

        data.put("goals", goals);
        userReference.set(data, SetOptions.merge())
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

    public void followZumba(Object weight, Object systemTags, OnSetListener onSetListener) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("weight", weight);

        //monitorDate
        //weightDecreasedPerDay
        //zumbaFollowedCountPerDay

        data.put("systemTags", systemTags);
        userReference.set(data, SetOptions.merge())
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

    public void setData(Map<String, Object> data, OnSetListener onSetListener) {
        userReference.set(data, SetOptions.merge())
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

    public void setSystemTags(Map<String, Object> systemTags, OnSetListener onSetListener) {
        HashMap<String, Object> data = new HashMap<>();

        data.put("systemTags", systemTags);

        userReference.set(data, SetOptions.merge())
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
