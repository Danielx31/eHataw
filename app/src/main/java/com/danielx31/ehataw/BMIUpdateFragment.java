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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import java.text.DecimalFormat;

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
        double heightGoalInCm = Double.parseDouble(heightParts[0]);

        if (heightGoalInCm <= 0) {
            heightEditText.setError("Invalid height");
            return;
        }

        double weightGoalInKg = Double.parseDouble(weightEditText.getText().toString());
        String weight = weightEditText.getText().toString() + " kg";

        if (weightGoalInKg <= 0) {
            weightEditText.setError("Invalid weight");
            return;
        }

        if (weightGoalInKg > user.getWeightGoalFromInKg()) {
            showWarningDialog(weight, height);
            return;
        }

        userAPI.setBodySize(weight, height, new UserAPI.OnSetListener() {
            @Override
            public void onSetSuccess() {
                Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();

                userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
                    @Override
                    public void onFetchSuccess(User fetchedUser) {
                        BMITracker bmiTracker = new BMITracker(fetchedUser.getWeightInKg(), fetchedUser.getHeightInCm());

                        bmiTextView.setText("Your BMI is " + new DecimalFormat("##.00").format(bmiTracker.calculateBMI()) + "\n" + "You are considered " + bmiTracker.classifyBMI().getName().toLowerCase());
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
            }

            @Override
            public void onSetError(Exception error) {
                Toast.makeText(getContext(), "A Network Error Occurred!\nPlease Try Again!", Toast.LENGTH_SHORT).show();
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

    private void showWarningDialog(Object weight, Object height) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning");
        builder.setMessage("Your weight is unmatched with the goals! We will reset your weight Goal to sync with your weight! Continue to proceed?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userAPI.setBodySize(weight, height, new UserAPI.OnSetListener() {
                    @Override
                    public void onSetSuccess() {
                        Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();

                        userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
                            @Override
                            public void onFetchSuccess(User fetchedUser) {
                                BMITracker bmiTracker = new BMITracker(fetchedUser.getWeightInKg(), fetchedUser.getHeightInCm());

                                bmiTextView.setText("Your BMI is " + new DecimalFormat("##.00").format(bmiTracker.calculateBMI()) + "\n" + "You are considered " + bmiTracker.classifyBMI().getName().toLowerCase());
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
                    }

                    @Override
                    public void onSetError(Exception error) {
                        Toast.makeText(getContext(), "A Network Error Occurred!\nPlease Try Again!", Toast.LENGTH_SHORT).show();
                    }
                });
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
}