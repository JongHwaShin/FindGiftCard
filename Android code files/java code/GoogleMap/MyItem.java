package com.example.myapplication;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

//클러스터 마커 클래스
public class MyItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final int storeIconNum;


    public MyItem(double lat, double lng, String title, String snippet, int storeIconNum) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.storeIconNum = storeIconNum;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
    public int getStoreIconNum(){
        return storeIconNum;
    }

}