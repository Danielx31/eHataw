package com.danielx31.ehataw;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.WeightLossData;
import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.danielx31.ehataw.firebase.firestore.model.api.WeightLossMonitorAPI;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.util.Date;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;


public class WeightLossMonitoringFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private WeightLossData weightLossData;
    private WeightLossMonitorAPI weightLossMonitorAPI;
    private Button datePickerButton;
    private TextView infoTextView;

    public WeightLossMonitoringFragment() {
        // Required empty public constructor
    }

//    public static WeightLossMonitoringFragment newInstance(WeightLossData weightLossData) {
//        WeightLossMonitoringFragment fragment = new WeightLossMonitoringFragment();
//        Bundle args = new Bundle();
//        Gson gson = new Gson();
//        args.putString("weightLossData", gson.toJson(weightLossData));
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//             String weightLossDataJson = getArguments().getString("weightLossData");
//             initializeWeightLossData(weightLossDataJson);
//        }

        RxJavaPlugins.setErrorHandler(e -> {
        });

        datePickerButton = getView().findViewById(R.id.button_pick_date);
        infoTextView = getView().findViewById(R.id.textview_info);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getChildFragmentManager(), "datePicker");
            }
        });

        DateManager dateManager = new DateManager(new Date());
        fetchAndSetUI(dateManager.toString());

    }

    private void fetchAndSetUI(String id) {
        weightLossMonitorAPI.fetch(id, "Monitoring Weight Loss Data", new WeightLossMonitorAPI.OnFetchListener() {
            @Override
            public void onFetchSuccess(WeightLossData fetchedWeightLossData) {
                weightLossData = fetchedWeightLossData;
                setInfoText();
            }

            @Override
            public void onFetchFailed(String message) {
                infoTextView.setText("No Data");
            }

            @Override
            public void onFetchError(Exception error) {
                Toast.makeText(getContext(), "A Network Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean initializeWeightLossData(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }

        Gson gson = new Gson();
        this.weightLossData = gson.fromJson(json, new TypeToken<WeightLossData>(){}.getType());
        if (weightLossData == null) {
            return false;
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight_loss_monitoring, container, false);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        LocalDate localDate = new LocalDate(i, i1, i2);
        infoTextView.setText("Loading...");

        fetchAndSetUI(localDate.toString());
    }

    private void setInfoText() {
        if (weightLossData == null) {
            return;
        }

        DateManager dateManager = new DateManager(weightLossData.getDate());
        LocalDate localDate = dateManager.getLocalDate();
        String monthString = localDate.toString("MMMM");

        String dateString = "Date: " + localDate.toString();
        String startWeightString = "Weight Before: " + weightLossData.getStartWeight();
        String endWeightString = "Weight After: " + weightLossData.getEndWeight();

        Double weightDecreasedInKg = weightLossData.getStartWeightInKg() - weightLossData.getEndWeightInKg();

        if (weightDecreasedInKg < 0) {
            weightDecreasedInKg = 0.0;
        }

        String weightDecreasedString = "Decreased Weight: " + new DecimalFormat("#0.####").format(weightDecreasedInKg) + " kg";

        infoTextView.setText(dateString + "\n" + startWeightString + "\n" + endWeightString + "\n" + weightDecreasedString);
    }

}