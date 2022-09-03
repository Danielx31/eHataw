package com.danielx31.ehataw;

public class MyEhatawData {
    private String zumbaName;
    private String zumbaDescription;
    private Integer zumbaImage;

    public MyEhatawData(String zumbaName, String zumbaDescription, Integer zumbaImage) {
        this.zumbaName = zumbaName;
        this.zumbaDescription = zumbaDescription;
        this.zumbaImage = zumbaImage;
    }

    public String getZumbaName() {
        return zumbaName;
    }

    public void setZumbaName(String zumbaName) {
        this.zumbaName = zumbaName;
    }

    public String getZumbaDescription() {
        return zumbaDescription;
    }

    public void setZumbaDescription(String zumbaDescription) {
        this.zumbaDescription = zumbaDescription;
    }

    public Integer getZumbaImage() {
        return zumbaImage;
    }

    public void setZumbaImage(Integer zumbaImage) {
        this.zumbaImage = zumbaImage;
    }
}
