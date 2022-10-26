package com.danielx31.ehataw;

import static com.danielx31.ehataw.App.CHANNEL_1_ID;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.danielx31.ehataw.localData.controller.ZumbaListController;
import com.danielx31.ehataw.retrofit.api.FileDownloadClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.FilenameUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownloadService extends IntentService {

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private final static String TAG = "Download Service";

    private final String DOWNLOADED_VIDEOS_KEY = "downloadVideos";
    private ZumbaListController zumbaListController;

    private Zumba zumba;
    private long totalSize;

    private final String PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private Handler handler;
    private int progress;
    private long delayUpdateNotificationTime = 1800;

    private static boolean downloading;

    public DownloadService() {
        super("Download Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (!PermissionManager.isAllowPermission(getApplicationContext(), PERMISSION)) {
            return;
        }

        handler = new Handler(Looper.getMainLooper());
        initializeController();

        String zumbaString = intent.getStringExtra("zumba");
        Gson gson = new Gson();
        zumba = gson.fromJson(zumbaString, Zumba.class);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("")
                .setContentTitle("Download")
                .setContentText(zumba.getTitle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setProgress(100, 0, false)
                .setOngoing(false)
                .setOnlyAlertOnce(true);
        notificationManager.notify(0, notificationBuilder.build());
        progress = 0;

        if (zumbaListController.contains(zumba.getId())) {
            zumbaListController.remove(zumba.getId());
        }

        startDownload();
    }

    public void initializeController() {
        SharedPreferences sharedPreferences = getSharedPreferences(DOWNLOADED_VIDEOS_KEY, Context.MODE_PRIVATE);
        File folder = new File(getExternalFilesDir("offline").toString());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        this.zumbaListController = new ZumbaListController(sharedPreferences, userId, folder);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!downloading) {
            return;
        }

        File file = new File(getExternalFilesDir("offline") + File.separator + zumba.getId());
        if (file == null || !file.exists()) {
            return;
        }
        file.delete();
    }


    public static String getUrlFile(String url) {
        try {
            URI newUri = new URI(url);
            return FilenameUtils.getName(newUri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return "";
    }

    private Runnable notificationRunnable = new Runnable() {
        public void run() {
            sendNotification(progress);
            handler.postDelayed(this, delayUpdateNotificationTime); // Run again after time ms
        }
    };

    public void startDownload() {
        downloading = true;
        handler.postDelayed(notificationRunnable, delayUpdateNotificationTime);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://google.com/")
                .build();

        FileDownloadClient fileDownloadClient = retrofit.create(FileDownloadClient.class);

        String thumbnailFileName = getUrlFile(zumba.getThumbnailUrl());
        String videoFileName = getUrlFile(zumba.getVideoUrl());

        Call<ResponseBody> callThumbnail = fileDownloadClient.downloadFileStream(zumba.getThumbnailUrl());
        Call<ResponseBody> callVideo = fileDownloadClient.downloadFileStream(zumba.getVideoUrl());

        callThumbnail.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() == null) {
                    onSaveFailedNotification();
                    return;
                }

                ExecutorService executor = Executors.newSingleThreadExecutor();
                //Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    totalSize += getFileSize(response.body());

                    //Background work here
                    boolean success = writeResponseBodyToDisk(response.body(), thumbnailFileName);
                    if (!success) {
                        onSaveFailedNotification();
                        return;
                    }

                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onSaveFailedNotification();
                return;
            }
        });

        callVideo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() == null) {
                    onSaveFailedNotification();
                    return;
                }

                ExecutorService executor = Executors.newSingleThreadExecutor();
                //Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    totalSize += getFileSize(response.body());

                    boolean success = writeResponseBodyToDisk(response.body(), videoFileName);
                    if (!success) {
                        onSaveFailedNotification();
                        return;
                    }
                    onCompleteFilesDownload();
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onSaveFailedNotification();
                return;
            }
        });
    }

    public static boolean isDownloading() {
        return downloading;
    }

    private long getFileSize(@NonNull ResponseBody body) {
        return body.contentLength();
    }

    private boolean writeResponseBodyToDisk(@NonNull ResponseBody body, String fileName) {
        try {
            // todo change the file location/name according to your needs
            File file = new File(getExternalFilesDir("offline" + File.separator + zumba.getId()) + File.separator + fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            long startTime = System.currentTimeMillis();

            try {
                byte[] fileReader = new byte[4096];

                //long fileSize = body.contentLength();
                long fileSize = totalSize;
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    if (!downloading) {
                        break;
                    }
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Long progressLong = (fileSizeDownloaded * 100) / fileSize;
                    this.progress = progressLong.intValue();
                    //Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                if (!downloading) {
                    return false;
                }

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
        if (isVersionNougatAndUp()) {
            notificationBuilder.setSubText(progress + "%");
        } else {
            notificationBuilder.setContentInfo(progress + "%");
        }
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(0, notification);
    }

    private void sendIntent() {
        Intent intent = new Intent();
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void onCompleteFilesDownload() {
        Zumba offlineZumba = zumba;
        //change zumba video url and thumbnail url
        String thumbnailFileName = getUrlFile(zumba.getThumbnailUrl());
        String videoFileName = getUrlFile(zumba.getVideoUrl());
        String zumbaFolder = zumbaListController.getZumbaFolder(zumba.getId());

        offlineZumba.setThumbnailUrl(zumbaFolder + File.separator + thumbnailFileName);
        offlineZumba.setVideoUrl(zumbaFolder  + File.separator + videoFileName);

        boolean isAddSuccess = zumbaListController.add(offlineZumba);
        if (!isAddSuccess) {
            onSaveFailedNotification();
            return;
        }

        onSaveCompleteNotification();
        downloading = false;
    }

    private void onSaveCompleteNotification() {
        handler.removeCallbacks(notificationRunnable);
        if (isVersionNougatAndUp()) {
            notificationBuilder.setSubText("Completed");
        } else {
            notificationBuilder.setContentInfo("Completed");
        }
        notificationManager.cancel(0);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        notificationBuilder.setProgress(0, 0, false);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void onSaveFailedNotification() {
        onSaveFailedNotification("Download Failed");
    }

    private void onSaveFailedNotification(String contentTitle) {
        handler.removeCallbacks(notificationRunnable);
        if (isVersionNougatAndUp()) {
            notificationBuilder.setSubText("Failed");
        } else {
            notificationBuilder.setContentInfo("Failed");
        }
        notificationManager.cancel(0);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentTitle(contentTitle);
        notificationManager.notify(0, notificationBuilder.build());
        downloading = false;
        stopSelf();
    }

    private boolean isVersionNougatAndUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
