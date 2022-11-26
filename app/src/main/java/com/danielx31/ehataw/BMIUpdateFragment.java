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
import android.widget.EditText;
import android.widget.Toast;

import com.danielx31.ehataw.firebase.firestore.model.api.UserAPI;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class BMIUpdateFragment extends Fragment {

    private BroadcastReceiver connectionReceiver;

    private UserAPI userAPI;

    private Button saveButton;
    private EditText heightEditText;
    private EditText weightEditText;

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = weightEditText.getText().toString() + " kg";
                String height = heightEditText.getText().toString() + " cm";
                userAPI.setBodySize(weight, height, new UserAPI.OnSetListener() {
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