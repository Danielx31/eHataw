package com.danielx31.ehataw;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

public class ConnectionReceiverPrime extends BroadcastReceiver {

    private static Dialog dialog;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!isConnected(context)) {
            showAlertDialog(context);
            return;
        }

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
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

    public void showAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_no_network, null);
        Button offlineVideosButton = view.findViewById(R.id.layoutnn_offlinevideos);
        offlineVideosButton.setText("Settings");
        offlineVideosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

}
