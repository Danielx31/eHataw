package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;


public class SettingsActivity extends Fragment {

    private BroadcastReceiver connectionReceiver;
    private Button ButtonChangePwd, ButtonDeleteAcc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_activity, container, false);
        RxJavaPlugins.setErrorHandler(e -> { });

        connectionReceiver = new ConnectionReceiver();

        ButtonChangePwd = view.findViewById(R.id.btnChangePwd);
        ButtonDeleteAcc = view.findViewById(R.id.btnDeleteAcc);

        ButtonChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment changePass = new ChangePasswordActivity();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, changePass).commit();
            }
        });

        ButtonDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment deleteAcc =  new delete_acc_activity();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, deleteAcc).commit();
            }
        });
        return view;
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