package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.User;
import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import java8.util.Maps;
import java8.util.stream.StreamSupport;

public class HealthConditionUpdateFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;

    private UserAPI userAPI;

    private Map<String, CheckBox> healthConditionCheckBoxes;
    private TextView infoTextView;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_health_condition_update, container, false);

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

        healthConditionCheckBoxes = new HashMap<>();

        healthConditionCheckBoxes.put("Asthma", getView().findViewById(R.id.cb_asthma));
        healthConditionCheckBoxes.put("Diabetes", getView().findViewById(R.id.cb_diabetes));
        healthConditionCheckBoxes.put("Heart Diseases", getView().findViewById(R.id.cb_heartdisease));
        healthConditionCheckBoxes.put("High Blood", getView().findViewById(R.id.cb_highbloodpressure));
        healthConditionCheckBoxes.put("Obesity", getView().findViewById(R.id.cb_obesity));

        infoTextView = getView().findViewById(R.id.textview_zumbagoalpercentage);

        saveButton = getView().findViewById(R.id.button_save);

        Maps.forEach(healthConditionCheckBoxes, (name, checkBox) -> {
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()) {
                        checkBox.setTextColor(getResources().getColor(R.color.orange));
                        return;
                    }

                    checkBox.setTextColor(getResources().getColor(R.color.white));
                }
            });
        });

        setLoading(true);

        userAPI.fetchUser(new UserAPI.OnFetchUserListener() {
            @Override
            public void onFetchSuccess(User fetchedUser) {
                List<String> healthConditions = fetchedUser.getHealthConditions();

                StreamSupport.stream(healthConditions)
                        .forEach((healthCondition) -> {
                            healthConditionCheckBoxes.get(healthCondition).setChecked(true);
                            healthConditionCheckBoxes.get(healthCondition).setTextColor(getResources().getColor(R.color.orange));
                        });
                setLoading(false);
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
                userAPI.setHealthConditions(getHealthConditions(), new UserAPI.OnSetListener() {
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

    private void setLoading(boolean enabled) {
        if (!enabled) {
            infoTextView.setText("Choose below if you have been diagnosed with the sickness or disease.");

            Maps.forEach(healthConditionCheckBoxes, (name, checkBox) -> {
                checkBox.setVisibility(View.VISIBLE);
            });

            saveButton.setVisibility(View.VISIBLE);
            return;
        }

        infoTextView.setText("Loading...");

        Maps.forEach(healthConditionCheckBoxes, (name, checkBox) -> {
            checkBox.setVisibility(View.GONE);
        });

        saveButton.setVisibility(View.GONE);
    }

    private List<String> getHealthConditions() {
        if (healthConditionCheckBoxes == null) {
            return new ArrayList<>();
        }

        List<String> healthConditions = new ArrayList<>();
        Maps.forEach(healthConditionCheckBoxes, (name, checkBox) -> {
            if (!checkBox.isChecked()) {
                return;
            }

            healthConditions.add(name);
        });

        return healthConditions;
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