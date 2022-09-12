package com.danielx31.ehataw;

import static com.danielx31.ehataw.App.CHANNEL_1_ID;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.danielx31.ehataw.retrofit.api.FileDownloadClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownloadService extends IntentService {

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private final String TAG = "Download Service";
    private String url;
    private String filename;

    private final String PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public DownloadService() {
        super("Download Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(!PermissionManager.isAllowPermission(getApplicationContext(), PERMISSION)) {
            return;
        }

        url = intent.getStringExtra("url");
        filename = url.substring(url.lastIndexOf('/') + 1);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("")
                .setContentTitle("Save Video")
                .setContentText(filename)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setProgress(100, 0, false);
        notificationManager.notify(0, notificationBuilder.build());

        downloadFileStream();
    }

    public void downloadFileStream() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://google.com/")
                .build();

        FileDownloadClient fileDownloadClient = retrofit.create(FileDownloadClient.class);

        Call<ResponseBody> call = fileDownloadClient.downloadFileStream(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    //Background work here
                    boolean success = writeResponseBodyToDisk(response.body(), filename);
                    if (!success) {
                        onSaveFailedNotification();
                        return;
                    }
                    onSaveCompleteNotification();
                    //Toast.makeText(getApplicationContext(), "Stream Success! = " + success, Toast.LENGTH_SHORT).show();
                    handler.post(() -> {
                        //UI Thread work here
                    });
                });

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean writeResponseBodyToDisk(@NonNull ResponseBody body, String fileName) {
        try {
            // todo change the file location/name according to your needs
            File file = new File(getExternalFilesDir("Videos") + File.separator + fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            long startTime = System.currentTimeMillis();

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);



                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Long progressLong = (fileSizeDownloaded * 100) / fileSize;
                    int progress = progressLong.intValue();
                    sendNotification(progress);
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void sendNotification(int progress) {
        sendIntent();
        notificationBuilder.setProgress(100, progress, false);
        notificationBuilder.setContentText(filename);
        if (isVersionNougatAndUp()) {
            notificationBuilder.setSubText(progress + "%");
        } else {
            notificationBuilder.setContentInfo(progress + "%");
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendIntent() {
        Intent intent = new Intent();
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onSaveCompleteNotification() {
        notificationManager.cancel(0);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentTitle("Save Done");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void onSaveFailedNotification() {
        notificationManager.cancel(0);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        notificationBuilder.setProgress(0,0,false);
        notificationBuilder.setContentTitle("Save Failed");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private boolean isVersionNougatAndUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
