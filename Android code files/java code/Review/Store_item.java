package com.example.myapplication;

public class Store_item {
    private String store_title;
    private float ratingstar;
    private int storeId;



    public Store_item(String store_title, float ratingstar, int storeId) {
        this.store_title = store_title;
        this.ratingstar = ratingstar;
        this.storeId = storeId;
    }

    public String getStore_title() {
        return store_title;
    }

    public void setStore_title(String store_title) {
        this.store_title = store_title;
    }

    public float getRatingstar() {
        return ratingstar;
    }

    public void setRatingstar(float ratingstar) {
        this.ratingstar = ratingstar;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "store_item{" +
                "store_title='" + store_title + '\'' +
                ", ratingstar=" + ratingstar +
                ", storeId=" + storeId +
                '}';
    }
}
