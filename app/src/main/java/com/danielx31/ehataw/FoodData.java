package com.danielx31.ehataw;

public class FoodData {
    
    private String itemName;
    private String itemDescription;
    private String itemprice;
    private int itemImage;

    public FoodData(String itemName, String itemDescription, String itemprice, int itemImage) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemprice = itemprice;
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemprice() {
        return itemprice;
    }

    public int getItemImage() {
        return itemImage;
    }
}
