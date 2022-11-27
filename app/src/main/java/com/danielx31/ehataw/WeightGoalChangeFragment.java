package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class WeightGoalChangeFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;

    private UserAPI userAPI;

    private Button saveButton;
    private EditText weightGoalEditText;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_weight_goal_update, container, false);

        // Inflate the layout for this fragment
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
        weightGoalEditText = getView().findViewById(R.id.edittext_weightgoal);

        userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
            @Override
            public void onFetchSuccess(User fetchedUser) {
                user = fetchedUser;
                weightGoalEditText.setText(String.valueOf(user.getWeightInKg()));
            }

            @Override
            public void onFetchNotFound() {
                Toast.makeText(getContext(), "A Network Error Occurred! Please Try Again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(getContext(), "A Network Error Occurred! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    Toast.makeText(getContext(), "Loading... Please Wait!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (weightGoalEditText.getText().toString().isEmpty()) {
                    weightGoalEditText.setError("Please fill out the Input");
                    return;
                }

                String weightGoal = weightGoalEditText.getText().toString() + " kg";
                String[] weightGoalParts = weightGoal.split(" ");
                double weightGoalInKg = Double.parseDouble(weightGoalParts[0]);

                if (weightGoalInKg <= 0) {
                    weightGoalEditText.setError("Invalid Weight Goal!");
                    return;
                }

                if (weightGoalInKg > user.getWeightInKg()) {
                    weightGoalEditText.setError("Weight Goal must be equal or lower to your current weight!");
                    return;
                }

                BMITracker bmiTracker = new BMITracker(user.getWeightInKg(), user.getHeightInCm());
                BMITracker.BMIClassification classification = bmiTracker.classifyBMI();

                if (classification == BMITracker.BMIClassification.UNDERWEIGHT) {
                    weightGoalEditText.setError("Cannot Proceed!! Underweight Goal");
                    return;
                }

                if (!user.getHealthConditions().isEmpty() &&
                        weightGoalInKg < user.getWeightInKg()) {
                    showWarningDialog();
                    return;
                }

                userAPI.setWeightGoal(weightGoal, new UserAPI.OnSetListener() {
                    @Override
                    public void onSetSuccess() {
                        Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSetError(Exception error) {
                        Toast.makeText(getContext(), "A Network Error Occurred!\nPlease Try Again!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning");
        builder.setMessage("We recommend you to consult to the doctors if you have some health conditions.\nStill continue?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();
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

}