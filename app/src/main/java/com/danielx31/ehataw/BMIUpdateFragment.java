package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.WeightLossData;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;
import com.danielx31.ehataw.firebase.firestore.model.api.WeightLossMonitorAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.protobuf.Internal;

import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class BMIUpdateFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;

    private UserAPI userAPI;

    private Button saveButton;
    private EditText heightEditText;
    private EditText weightEditText;

    private TextView bmiTextView;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_bmi_update, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        connectionReceiver = new ConnectionReceiver();

        userAPI = new UserAPI();

        saveButton = getView().findViewById(R.id.button_save);
        heightEditText = getView().findViewById(R.id.edittext_weightgoal);
        weightEditText = getView().findViewById(R.id.edittext_weight);
        bmiTextView = getView().findViewById(R.id.textview_bmi);

        userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
            @Override
            public void onFetchSuccess(User fetchedUser) {
                user = fetchedUser;
                DecimalFormat decimalFormat = new DecimalFormat("##.00");
                String userHeightInCm = decimalFormat.format(fetchedUser.getHeightInCm());
                String userWeightInKg = decimalFormat.format(fetchedUser.getWeightInKg());

                heightEditText.setText(userHeightInCm);
                weightEditText.setText(userWeightInKg);
                BMITracker bmiTracker = new BMITracker(fetchedUser.getWeightInKg(), fetchedUser.getHeightInCm());

                bmiTextView.setText("Your BMI is " + new DecimalFormat("##.00").format(bmiTracker.calculateBMI()) + "\n" + "You are considered " + bmiTracker.classifyBMI().getName().toLowerCase() + ".");
            }

            @Override
            public void onFetchNotFound() {
                Toast.makeText(getContext(), "A Network Error Occurred!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(getContext(), "A Network Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBodySize();
            }
        });

    }

    private void setBodySize() {
        if (user == null) {
            Toast.makeText(getContext(), "Loading... Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (heightEditText.getText().toString().isEmpty()) {
            heightEditText.setError("Please fill out the Input");
            return;
        }

        if (weightEditText.getText().toString().isEmpty()) {
            weightEditText.setError("Please fill out the Input");
            return;
        }

        String height = heightEditText.getText().toString() + " cm";
        String[] heightParts = height.split(" ");
        double heightInCm = Double.parseDouble(heightParts[0]);

        if (heightInCm <= 0) {
            heightEditText.setError("Invalid height");
            return;
        }

        double weightInKg = Double.parseDouble(weightEditText.getText().toString());
        String weight = weightEditText.getText().toString() + " kg";

        if (weightInKg <= 0) {
            weightEditText.setError("Invalid weight");
            return;
        }

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("weight", weight);
        userData.put("height", height);

        BMITracker bmiTracker = new BMITracker(weightInKg, heightInCm);

        if (weightInKg < user.getWeightGoalFromInKg()) {
            Map<String, Object> goals = new HashMap<>();
            goals.put("weightGoal" , weightInKg);
            goals.put("weightGoalFrom" , weightInKg);

            userData.put("goals", goals);
            showWarningDialog(userData, bmiTracker);
            return;
        }

        saveAndUpdateUI(userData, bmiTracker);

    }

    private void saveAndUpdateUI(Map<String, Object> userData, BMITracker bmiTracker) {
        runTransaction(userData, new OnTransactionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();

                bmiTextView.setText("Your BMI is " + new DecimalFormat("##.00").format(bmiTracker.calculateBMI()) + "\n" + "You are considered " + bmiTracker.classifyBMI().getName().toLowerCase());
            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), "A Network Error Occurred! Please Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(connectionReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(connectionReceiver);
    }

    private void showWarningDialog(Map<String, Object> userData, BMITracker bmiTracker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning");
        builder.setMessage("Your new weight is less than the weight goal! We will reset your weight Goal to sync with your new weight! Continue to proceed?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveAndUpdateUI(userData, bmiTracker);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void runTransaction(Map<String, Object> userData, OnTransactionListener onTransactionListener) {
        // userData
        // height (String)
        // weight (String)
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        LocalDate todayDate = new LocalDate(new Date());

        database.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                // Check Monitoring Weight Data
                WeightLossMonitorAPI weightLossMonitorAPI = new WeightLossMonitorAPI();
                DocumentReference weightLossReference = weightLossMonitorAPI.getDocumentReference();
                DocumentSnapshot weightLossSnapshot = transaction.get(weightLossReference);

                //Reads First then Set
                transaction.set(userAPI.getDocumentReference(), userData, SetOptions.merge());

                if (!weightLossSnapshot.exists()) {
                    WeightLossData todayWeightLossData = new WeightLossData(
                            user.getWeight(),
                            (String) userData.get("weight"));

                    transaction.set(weightLossMonitorAPI.getDocumentReference(), todayWeightLossData, SetOptions.merge());
                } else {
                    Map<String, Object> newWeightLossDataMap = new HashMap<>();
                    newWeightLossDataMap.put("endWeight", userData.get("weight"));
                    transaction.set(weightLossMonitorAPI.getDocumentReference(), newWeightLossDataMap, SetOptions.merge());
                }

                return true;
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        onTransactionListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onTransactionListener.onError(e);
                    }
                });
    }

    public interface OnTransactionListener {
        void onSuccess();
        void onError(Exception error);
    }
}