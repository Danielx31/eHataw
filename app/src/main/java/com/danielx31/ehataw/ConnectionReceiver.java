package com.danielx31.ehataw;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!isConnected(context)) {
            showAlertDialog(context);
        }

    }

    public boolean isConnected(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (Exception error) {
            error.printStackTrace();
            return false;
        }
    }

    private void showAlertDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_no_network, null);
        Button offlineVideosButton = view.findViewById(R.id.layoutnn_offlinevideos);

        offlineVideosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("fragment", "downloadVideo");
                context.startActivity(intent);
            }
        });

        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}
