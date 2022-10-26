package com.danielx31.ehataw.localData.model;

import com.danielx31.ehataw.firebase.firestore.model.Zumba;

import java.util.List;

public class UserDownloads {

    private String id;
    private List<Zumba> downloadList;

    public UserDownloads(String id, List<Zumba> downloadList) {
        this.id = id;
        this.downloadList = downloadList;
    }

    public String getId() {
        return id;
    }

    public List<Zumba> getDownloadList() {
        return downloadList;
    }

}
