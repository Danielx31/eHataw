package com.danielx31.ehataw;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    private Context context;
    private Activity activity;

    public PermissionManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setPermission(String permission, int requestCode)
    {
        if (!isAllowPermission(context, permission)) {
            ActivityCompat.requestPermissions(activity, new String[] { permission }, requestCode);
        }

    }

    public static boolean isAllowPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

}
