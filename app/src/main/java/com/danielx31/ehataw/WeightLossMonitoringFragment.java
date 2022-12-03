package com.danielx31.ehataw;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.firestore.Transaction;

import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;


public class WeightLossMonitoringFragment extends Fragment {

    private WeightLossMonitorAPI weightLossMonitorAPI;
    private Button datePickerButton;
    private TextView infoTextView;

    public WeightLossMonitoringFragment() {
        // Required empty public constructor
    }

    private void fetchAndSetUI(LocalDate localDate) {
        infoTextView.setText("loading...");
        runFetch(localDate, new OnFetchListener() {
            @Override
            public void onSuccess(WeightLossData fetchedWeightLossData) {
                setInfoText(fetchedWeightLossData);
            }

            @Override
            public void onDataNotFound() {
                setInfoText(null);
            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(getContext(), "A Network Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight_loss_monitoring, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxJavaPlugins.setErrorHandler(e -> {
        });

        weightLossMonitorAPI = new WeightLossMonitorAPI();

        datePickerButton = getView().findViewById(R.id.button_pick_date);
        infoTextView = getView().findViewById(R.id.textview_info);

        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                LocalDate localDate = new LocalDate(i, i1 + 1, i2);
                fetchAndSetUI(localDate);
            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show(getChildFragmentManager(), "datePicker");
            }
        });

        fetchAndSetUI(new LocalDate());
    }

    private void setInfoText(WeightLossData weightLossData) {
        if (weightLossData == null) {
            infoTextView.setText("No Data");
            return;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#0.##");

        DateManager dateManager = new DateManager(weightLossData.getDate());
        LocalDate localDate = dateManager.getLocalDate();
        String monthString = localDate.toString("MMMM");

        String dateString = "Date: " + monthString + " " + localDate.getDayOfMonth() + ", " + localDate.getYear();
        String startWeightString = "Weight Before: " + decimalFormat.format(weightLossData.getStartWeightInKg());
        String endWeightString = "Weight After: " + decimalFormat.format(weightLossData.getEndWeightInKg());

        Double weightDecreasedInKg = weightLossData.getStartWeightInKg() - weightLossData.getEndWeightInKg();

        if (weightDecreasedInKg < 0) {
            weightDecreasedInKg = 0.0;
        }

        String weightDecreasedString = "Weight Loss: " + new DecimalFormat("#0.####").format(weightDecreasedInKg) + " kg";

        infoTextView.setText(dateString + "\n" + startWeightString + "\n" + endWeightString + "\n" + weightDecreasedString);
    }

    private void runFetch(LocalDate localDate, OnFetchListener onFetchListener) {
        weightLossMonitorAPI.fetch(localDate.toString(), "Weight Monitoring Fetch", new WeightLossMonitorAPI.OnFetchListener() {
            @Override
            public void onFetchSuccess(WeightLossData fetchedWeightLossData) {
                onFetchListener.onSuccess(fetchedWeightLossData);
            }

            @Override
            public void onFetchFailed(String message) {
                //On Date not Found
                if (localDate.isBefore(new LocalDate())) {
                    weightLossMonitorAPI.fetchNearestPreviousDate(localDate.toDate(), "Fetch Nearest Previous Date", new WeightLossMonitorAPI.OnFetchListener() {
                        @Override
                        public void onFetchSuccess(WeightLossData fetchedWeightLossData) {
                            WeightLossData prevWeightLossData = new WeightLossData(localDate.toDate(),
                                    fetchedWeightLossData.getEndWeight(),
                                    fetchedWeightLossData.getEndWeight());
                            onFetchListener.onSuccess(prevWeightLossData);
                        }

                        @Override
                        public void onFetchFailed(String message) {
                            //document snapshot is null
                            onFetchListener.onDataNotFound();
                        }

                        @Override
                        public void onFetchError(Exception error) {
                            onFetchListener.onError(error);
                        }
                    });
                    return;
                }

                UserAPI userAPI = new UserAPI();
                userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
                    @Override
                    public void onFetchSuccess(User fetchedUser) {
                        WeightLossData tempWeightLossData = new WeightLossData(localDate.toDate(),
                                fetchedUser.getWeight(),
                                fetchedUser.getWeight());
                        onFetchListener.onSuccess(tempWeightLossData);
                    }

                    @Override
                    public void onFetchNotFound() {
                        onFetchListener.onDataNotFound();
                    }

                    @Override
                    public void onFetchError(Exception e) {
                        onFetchListener.onError(e);
                    }
                });
            }

            @Override
            public void onFetchError(Exception error) {
                onFetchListener.onError(error);
            }
        });
    }

    public interface OnFetchListener {
        void onSuccess(WeightLossData fetchedWeightLossData);
        void onDataNotFound();
        void onError(Exception error);
    }

}