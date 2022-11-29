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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;
import com.google.firebase.Timestamp;

import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MonitorGoalsFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;

    private UserAPI userAPI;

    private ProgressBar weightGoalProgressBar;
    private TextView weightGoalPercentageTextView;
    private TextView weightGoalInfoTextView;
    private EditText weightGoalEditText;
    private Button changeWeightGoalButton;

    private ProgressBar zumbaGoalProgressBar;
    private TextView zumbaGoalPercentageTextView;
    private TextView zumbaGoalInfoTextView;
    private EditText zumbaCountGoalEditText;
    private Button changeZumbaGoalButton;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_monitor_goals, container, false);

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

        weightGoalProgressBar = getView().findViewById(R.id.progressbar_weightgoal);
        weightGoalProgressBar.setProgress(0);
        weightGoalPercentageTextView = getView().findViewById(R.id.textview_weightgoalpercentage);
        weightGoalInfoTextView = getView().findViewById(R.id.textview_weightgoalinfo);
        weightGoalEditText = getView().findViewById(R.id.edittext_weightgoal);
        changeWeightGoalButton = getView().findViewById(R.id.button_changeweightgoal);

        zumbaGoalProgressBar = getView().findViewById(R.id.progressbar_zumbagoal);
        zumbaGoalProgressBar.setProgress(0);
        zumbaGoalPercentageTextView = getView().findViewById(R.id.textview_zumbagoalpercentage);
        zumbaGoalInfoTextView = getView().findViewById(R.id.textview_zumbagoalinfo);
        zumbaCountGoalEditText = getView().findViewById(R.id.edittext_zumbacountgoal);
        changeZumbaGoalButton = getView().findViewById(R.id.button_changezumbagoal);

        changeWeightGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeWeightGoalDialog();
            }
        });

        changeZumbaGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showZumbaGoalChangeDialog();
            }
        });

        fetchUserAndSetUI();
    }

    private void fetchUserAndSetUI() {
        if (userAPI == null) {
            return;
        }

        userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
            @Override
            public void onFetchSuccess(User fetchedUser) {
                user = fetchedUser;

                setWeightGoalUI();
                setZumbaGoalUI();
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

    private boolean isSameDateNoTime(Date date1, Date date2) {
        LocalDate localDate1 = new LocalDate(date1);
        LocalDate localDate2 = new LocalDate(date2);

        if (localDate1.compareTo(localDate2) == 0) {
            return true;
        }

        return false;
    }

    private void showWarningDialog(String weightGoal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning");
        builder.setMessage("We recommend you to consult to the doctors if you have some health conditions.\nStill continue?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LoadingDialog loadingDialog = new LoadingDialog();
                loadingDialog.show(getChildFragmentManager(), "loadingDialogChangeWeightGoal");

                Map<String, Object> goals = new HashMap<>();
                goals.put("weightGoal", weightGoal);
                goals.put("weightGoalFrom", user.getWeight());

                userAPI.setGoals(goals, new UserAPI.OnSetListener() {
                    @Override
                    public void onSetSuccess() {
                        fetchUserAndSetUI();
                        loadingDialog.dismiss();
                        Toast.makeText(getContext(), "Weight Goal Updated!", Toast.LENGTH_SHORT).show();
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

    private void changeWeightGoal() {
        if (user == null) {
            Toast.makeText(getContext(), "Loading... Please try again later.", Toast.LENGTH_SHORT).show();
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
            showWarningDialog(weightGoal);
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.show(getChildFragmentManager(), "loadingDialogChangeWeightGoal");

        Map<String, Object> goals = new HashMap<>();
        goals.put("weightGoal", weightGoal);
        goals.put("weightGoalFrom", user.getWeight());

        userAPI.setGoals(goals, new UserAPI.OnSetListener() {
            @Override
            public void onSetSuccess() {
                fetchUserAndSetUI();
                loadingDialog.dismiss();
                Toast.makeText(getContext(), "Weight Goal Updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSetError(Exception error) {
                Toast.makeText(getContext(), "A Network Error Occurred!\nPlease Try Again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangeWeightGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Weight Goal");
        builder.setMessage("All your progress will reset.\nAre you sure you want to change Weight Goal?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeWeightGoal();
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

    private void changeZumbaGoal() {
        if (user == null) {
            Toast.makeText(getContext(), "Loading... Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        int zumbaCountGoalPerDay = Integer.valueOf(zumbaCountGoalEditText.getText().toString());

        if (zumbaCountGoalPerDay <= 0) {
            zumbaCountGoalEditText.setError("Goal must at least have one!");
            return;
        }

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> goals = new HashMap<>();
        goals.put("zumbaCountGoalPerDay", zumbaCountGoalPerDay);

        Map<String, Object> systemTags = new HashMap<>();
        systemTags.put("monitorDate", new Date());
        systemTags.put("weightDecreasedPerDay", "0 kg");
        systemTags.put("zumbaFollowedCountPerDay", 0);

        data.put("goals", goals);
        data.put("systemTags", systemTags);

        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.show(getChildFragmentManager(), "loadingDialogChangeZumbaGoal");

        userAPI.setData(data, new UserAPI.OnSetListener() {
            @Override
            public void onSetSuccess() {
                fetchUserAndSetUI();
                loadingDialog.dismiss();
                Toast.makeText(getContext(), "Zumba Goal Updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSetError(Exception error) {
                loadingDialog.dismiss();
                Toast.makeText(getContext(), "A Network Error Occurred!\nPlease try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showZumbaGoalChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Zumba Goal");
        builder.setMessage("All your progress will reset.\nAre you sure you want to change Zumba Goal?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeZumbaGoal();
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

    private void setWeightGoalUI() {
        if (user == null) {
            return;
        }

        DecimalFormat decimalFormat = new DecimalFormat("##.00");
        BMITracker bmiTracker = new BMITracker(user.getWeightInKg(), user.getHeightInCm());

        int weightGoalPercentage = bmiTracker.getWeightGoalPercentage(user.getWeightGoalFromInKg(), user.getWeightGoalInKg());
        weightGoalPercentageTextView.setText(weightGoalPercentage + "%");
        weightGoalProgressBar.setProgress(weightGoalPercentage);
        weightGoalEditText.setText(decimalFormat.format(user.getWeightGoalInKg()));

        double weightLoss = user.getWeightGoalFromInKg() - user.getWeightInKg();
        if (weightLoss <= -1) {
            weightLoss = 0;
        }
        String totalWeightLoss = "Total Weight Loss: " + new DecimalFormat("##.####").format(weightLoss) + " kg";
        String totalCaloriesBurned = "Total Calories Burned: " + new DecimalFormat("0.##").format(bmiTracker.kgToCalories(weightLoss));
        weightGoalInfoTextView.setText(totalWeightLoss);

        zumbaCountGoalEditText.setText("" + user.getZumbaCountGoalPerDay());
    }

    private void setZumbaGoalUI() {
        if (user == null) {
            return;
        }

        BMITracker bmiTracker = new BMITracker(user.getWeightInKg(), user.getHeightInCm());

        //Zumba Goal
        String zumbaFollowedTodayText = "Zumba Followed Today: 0";
        String weightLossTodayText = "Weight Loss Today: 0 kg";

        Map<String, Object> systemTags = user.getSystemTags();
        if (systemTags == null) {
            zumbaGoalProgressBar.setProgress(0);
            zumbaGoalPercentageTextView.setText("0%");
            zumbaGoalInfoTextView.setText(zumbaFollowedTodayText + "\n" + weightLossTodayText);

            systemTags = new HashMap<>();
            systemTags.put("monitorDate", new Date());
            systemTags.put("weightDecreasedPerDay", "0 kg");
            systemTags.put("zumbaFollowedCountPerDay", 0);

            userAPI.setSystemTags(systemTags, new UserAPI.OnSetListener() {
                @Override
                public void onSetSuccess() {

                }

                @Override
                public void onSetError(Exception error) {
                    Toast.makeText(getContext(), "A Network Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        Timestamp monitorDate = user.getMonitorDate();

        boolean isSameDateNoTime = isSameDateNoTime(monitorDate.toDate(), new Date());

        if (!isSameDateNoTime) {
            zumbaGoalProgressBar.setProgress(0);
            zumbaGoalPercentageTextView.setText("0%");
            zumbaGoalInfoTextView.setText(zumbaFollowedTodayText + "\n" + weightLossTodayText);

            systemTags = new HashMap<>();
            systemTags.put("monitorDate", new Date());
            systemTags.put("weightDecreasedPerDay", "0 kg");
            systemTags.put("zumbaFollowedCountPerDay", 0);

            userAPI.setSystemTags(systemTags, new UserAPI.OnSetListener() {
                @Override
                public void onSetSuccess() {

                }

                @Override
                public void onSetError(Exception error) {
                    Toast.makeText(getContext(), "A Network Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        int zumbaGoalPercentage = bmiTracker.getPercentage(user.getZumbaFollowedCountPerDay(), user.getZumbaCountGoalPerDay());
        Log.d("TEST", "setZumbaGoalUI: " + zumbaGoalPercentage);
        Log.d("TEST", "zumba Followed: " + user.getZumbaFollowedCountPerDay());
        Log.d("TEST", "zumba Goal: " + user.getZumbaCountGoalPerDay());
        zumbaGoalProgressBar.setProgress(zumbaGoalPercentage);
        zumbaGoalPercentageTextView.setText(zumbaGoalPercentage + "%");

        zumbaFollowedTodayText = "Zumba Followed Today: " + user.getZumbaFollowedCountPerDay();
        Double weightLossToday = user.getWeightDecreasedPerDayInKg();
        weightLossTodayText = "Weight Loss Today: " + new DecimalFormat("##.####").format(weightLossToday) + " kg";

        zumbaGoalInfoTextView.setText(zumbaFollowedTodayText + "\n" + weightLossTodayText);
    }

}