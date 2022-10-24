package com.danielx31.ehataw.localData.controller;

import android.content.SharedPreferences;

import com.danielx31.ehataw.firebase.firestore.model.Zumba;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZumbaListController {

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private final String DOWNLOADED_VIDEOS_KEY = "downloadVideos";
    private File folder;

    public ZumbaListController(SharedPreferences sharedPreferences, File folder) {
        this.sharedPreferences = sharedPreferences;
        this.gson = new Gson();
        this.folder = folder;
    }

    @Override
    public String toString() {
        return sharedPreferences.getString(DOWNLOADED_VIDEOS_KEY, new ArrayList<>().toString());
    }

    public List<Zumba> getList() {
        String json = toString();
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        return gson.fromJson(json, new TypeToken<List<Zumba>>(){}.getType());
    }

    public JsonArray getJsonArray() {
        return JsonParser.parseString(gson.toJson(getList())).getAsJsonArray();
    }

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
        return save(gson.toJson(zumbaList));
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
        return save(gson.toJson(zumbaList));
    }

    public boolean save(String json) {
        if (json == null) {
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
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
        if (id == null || id.isEmpty()) {
            return -1;
        }

        if (getList() == null || getList().isEmpty()) {
            return -1;
        }

        int index = 0;
        for (Zumba zumba : getList()) {
            String zumbaId = zumba.getId();
            if (zumbaId.equals(id)) {
                return index;
            }
            ++index;
        }

        return -1;
    }

}
