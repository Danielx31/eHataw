package com.danielx31.ehataw.localData.controller;

import android.content.SharedPreferences;
import android.util.Log;

import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.danielx31.ehataw.localData.model.UserDownloads;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ZumbaListController {

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private final String DOWNLOADED_VIDEOS_KEY = "downloadVideos";
    private File folder;
    private String userId;

    public ZumbaListController(SharedPreferences sharedPreferences, String userId, File folder) {
        this.sharedPreferences = sharedPreferences;
        this.gson = new Gson();
        this.userId = userId;
        this.folder = folder;
    }

    @Override
    public String toString() {
        return sharedPreferences.getString(DOWNLOADED_VIDEOS_KEY, getEmptyUserDownloadList());
    }

    public String getEmptyUserDownloadList() {
        List<UserDownloads> userDownloadsList = new ArrayList<>();
        userDownloadsList.add(new UserDownloads(userId, new ArrayList<>()));
        return gson.toJson(userDownloadsList);
    }

    public List<UserDownloads> getUserDownloadsList() {
        String userDownloadListJson = toString();
        return gson.fromJson(userDownloadListJson, new TypeToken<List<UserDownloads>>(){}.getType());
    }

    public List<Zumba> getList() {
        List<UserDownloads> userDownloadsList = getUserDownloadsList();
        int userDownloadsIndex = getIndexById(userDownloadsList, userId);
        if (userDownloadsIndex == -1) {
            return new ArrayList<>();
        }

        List<Zumba> zumbaDownloads = userDownloadsList.get(userDownloadsIndex).getDownloadList();
        if (zumbaDownloads == null) {
            return new ArrayList<>();
        }

        String zumbaDownloadsJson = gson.toJson(zumbaDownloads);
        return gson.fromJson(zumbaDownloadsJson, new TypeToken<List<Zumba>>(){}.getType());
    }

//    public JsonArray getJsonArray() {
//        return JsonParser.parseString(gson.toJson(getList())).getAsJsonArray();
//    }

    public String getZumbaFolder(String zumbaId) {
        return folder.toString() + File.separator + zumbaId;
    }

    public boolean remove(String zumbaId, int index) {
        if (index < 0) {
            return false;
        }

        File zumbaFolder = new File(getZumbaFolder(zumbaId));
        if (zumbaFolder.exists()) {
            zumbaFolder.delete();
        }

        List<Zumba> zumbaList = getList();
        zumbaList.remove(index);
        return saveByZumbaList(zumbaList);
    }

    public boolean remove(String zumbaId) {
        int index = getWhereIdEquals(zumbaId);
        if (index == -1) {
            return false;
        }

        return remove(zumbaId, index);
    }

    public boolean add(Zumba zumba) {
        if (zumba == null) {
            return false;
        }

        List<Zumba> zumbaList = getList();
        zumbaList.add(zumba);

        return saveByZumbaList(zumbaList);
    }

    public boolean saveByZumbaList(List<Zumba> zumbaList) {
        if (zumbaList == null) {
            return false;
        }

        List<UserDownloads> userDownloadsList = getUserDownloadsList();
        int userDownloadsIndex = getIndexById(userDownloadsList, userId);
        if (userDownloadsIndex == -1) {
            return false;
        }

        UserDownloads userDownloads = new UserDownloads(userId, zumbaList);
        userDownloadsList.set(userDownloadsIndex, userDownloads);

        save(userDownloadsList);
        return true;
    }

    public boolean save(List<UserDownloads> userDownloadsList) {
        if (userDownloadsList == null) {
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(userDownloadsList);
        editor.putString(DOWNLOADED_VIDEOS_KEY, json);
        editor.apply();
        return true;

    }

    public boolean contains(String id) {
        int index = getWhereIdEquals(id);
        if (index == -1) {
            return false;
        }
        return true;
    }

    public int getWhereIdEquals(String id) {
        List<Zumba> zumbaList = getList();
        if (id == null || id.isEmpty()) {
            return -1;
        }

        if (zumbaList == null || zumbaList.isEmpty()) {
            return -1;
        }

        int index = 0;
        for (Zumba zumba : zumbaList) {
            String zumbaId = zumba.getId();
            if (zumbaId.equals(id)) {
                return index;
            }
            ++index;
        }

        return -1;
    }

    private int getIndexById(List<UserDownloads> userDownloadsList, String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }

        if (userDownloadsList == null || userDownloadsList.isEmpty()) {
            return -1;
        }

        int index = 0;
        for (UserDownloads userDownloads : userDownloadsList) {
            String userId = userDownloads.getId();
            if (userId.equals(value)) {
                return index;
            }
            ++index;
        }

        return -1;

    }



}
