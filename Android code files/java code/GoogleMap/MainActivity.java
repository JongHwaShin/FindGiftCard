package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;


import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    FrameLayout container;
    AutoCompleteTextView getLoc;
    Button btn_search;
    ImageButton btn_filter;
    GoogleMap mMap;
    Switch switch_mNp;
    Switch switch_OnOff_Seller;

    LayoutInflater inflater_filter;

    DataIO dataIO;

    ArrayList<ArrayList<String>> mobData;
    ArrayList<ArrayList<String>> paperData;
    ArrayList<ArrayList<String>> reviewData;

    CustomAutoCompleteAdapter myAdapter;
    List<CustomItem> customItemList;


    ClusterManager<MyItem> clusterManager;

    LinearLayout filterLayout;
    View filterView;
    ImageButton filterClose_btn;
    Button filterSet_btn;
    AutoCompleteTextView getAddrFilter;
    AutoCompleteTextView getClassFilter;
    AutoCompleteTextView getSubClassFilter;
    AutoCompleteTextView getNameFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        container = findViewById(R.id.container);
        filterLayout = findViewById(R.id.filterLayout);
        getLoc = findViewById(R.id.getLoc);
        btn_search = findViewById(R.id.findLoc);
        btn_filter = findViewById(R.id.btn_filter);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clusterManager.getMarkerCollection().clear();
                clusterManager.getClusterMarkerCollection().clear();
                clusterManager.clearItems();

                MyItem offsetItem = new MyItem(35.20098208286911, 128.5671314909584, "이노티안경 경남데파트점",  "zeropaymobile" + " " + 293, 21);
                clusterManager.addItem(offsetItem);

            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflater_filter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                filterView = inflater_filter.inflate(R.layout.marker_filter, filterLayout, true);

                btn_filter.setEnabled(false);

                filterClose_btn = filterView.findViewById(R.id.filterClose_btn);
                filterSet_btn = filterView.findViewById(R.id.filterSet_btn);

                getAddrFilter = filterView.findViewById(R.id.filter_addr);
                getClassFilter = filterView.findViewById(R.id.filter_class);
                getSubClassFilter = filterView.findViewById(R.id.filter_subClass);
                getNameFilter = filterView.findViewById(R.id.filter_name);

                if(switch_mNp.isChecked()){
                    Add_Filter_AutoComplete(getAddrFilter, paperData.get(1));
                    Add_Filter_AutoComplete(getClassFilter, paperData.get(2));
                    Add_Filter_AutoComplete(getSubClassFilter, paperData.get(3));
                    Add_Filter_AutoComplete(getNameFilter, paperData.get(4));
                }else {
                    Add_Filter_AutoComplete(getAddrFilter, mobData.get(1));
                    Add_Filter_AutoComplete(getClassFilter, mobData.get(2));
                    Add_Filter_AutoComplete(getSubClassFilter, mobData.get(3));
                    Add_Filter_AutoComplete(getNameFilter, mobData.get(4));
                }


            }
        });

        switch_mNp = findViewById(R.id.switch_mobNp);
        switch_mNp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    switch_OnOff_Seller.setEnabled(true);
                    switch_OnOff_Seller.setChecked(false);

                    clusterManager.getMarkerCollection().clear();
                    clusterManager.getClusterMarkerCollection().clear();
                    clusterManager.clearItems();

                    Add_ClusterItems(paperData, "", "", "", "");
                    Add_AutoComplete(paperData, "", "", "", "");

                }else {
                    switch_OnOff_Seller.setEnabled(false);
                    switch_OnOff_Seller.setChecked(false);

                    clusterManager.getMarkerCollection().clear();
                    clusterManager.getClusterMarkerCollection().clear();
                    clusterManager.clearItems();

                    Add_ClusterItems(mobData, "", "", "", "");
                    Add_AutoComplete(mobData, "", "", "", "");

                }
            }
        });


        switch_OnOff_Seller = findViewById(R.id.switch_showSell);
        switch_OnOff_Seller.setEnabled(false);

        dataIO = new DataIO();
        dataIO.start();




    }

    public void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            Toast.makeText(getApplicationContext(), "내 위치 확인 요청함", Toast.LENGTH_SHORT).show();

        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //구글맵 켤 시 창원시청으로 화면 갈 것.
        LatLng cw = new LatLng(35.2279868, 128.6818143);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cw));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cw, 15);
        mMap.animateCamera(cameraUpdate);

        clusterManager = new ClusterManager<MyItem>(this, mMap);
        MarkerRenderer markerRenderer = new MarkerRenderer(getApplicationContext(), mMap, clusterManager);
        clusterManager.setRenderer(markerRenderer);

        // 마커 클릭시 우측 하단 네비게이션 기능 제공
        // 마커 커스터마이징
        clusterManager.getMarkerCollection().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View view = inflater.inflate(R.layout.short_store, null);
                final TextView storename = view.findViewById(R.id.store_name);
                String text_storename = (marker.getTitle() != null) ? marker.getTitle() : "Cluster Item";
                storename.setText(text_storename);

                String text_idx_info = (marker.getSnippet() != null) ? marker.getSnippet() : "Cluster Item";
                int idx = Integer.valueOf(text_idx_info.split(" ")[1]);

                final TextView storeClass = view.findViewById(R.id.store_bigType);
                final TextView storeSubClass = view.findViewById(R.id.store_smallType);
                final TextView storeAddr = view.findViewById(R.id.store_address);

                ArrayList<ArrayList<String>> data;
                if(text_idx_info.split(" ")[0].equals("zeropaymobile")){
                    data = mobData;
                }else if(text_idx_info.split(" ")[0].equals("nuvisionpaper")){
                    data = paperData;
                }else {
                    data = mobData;
                }



                storeClass.setText(data.get(2).get(idx));
                storeSubClass.setText(data.get(3).get(idx));
                storeAddr.setText(data.get(1).get(idx));

                final RatingBar ratingBar = view.findViewById(R.id.store_rating_ratingBar);
                final TextView ratings = view.findViewById(R.id.store_ratingTextView);

                float rating_sum = 0;
                int rating_count = 0;
                for(int i = 1; i < reviewData.get(0).size(); i++){
                    if(reviewData.get(1).get(i).equals(data.get(4).get(idx))
                        && reviewData.get(2).get(i).equals(data.get(1).get(idx))){
                        rating_sum += Float.valueOf(reviewData.get(4).get(i));
                        rating_count++;
                    }
                }
                if(rating_count == 0){
                    ratingBar.setRating(0f);
                }else {
                    ratingBar.setRating( rating_sum / rating_count);
                    ratings.setText("" + (rating_sum / rating_count));
                }

                Log.d("Cluster", marker.getId());
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Log.d("Cluster", "test");

                return null;
            }
        });

        clusterManager.getMarkerCollection().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d("Cluster", " is click");
                Intent intent  = new Intent(MainActivity.this, Store_main.class);

                String storeInfo = marker.getSnippet();
                String tableName = storeInfo.split(" ")[0];
                int tableIdx = Integer.valueOf(storeInfo.split(" ")[1]);
                if(tableName.equals("zeropaymobile")){
                    intent.putExtra("addr", mobData.get(1).get(tableIdx));
                    intent.putExtra("class", mobData.get(2).get(tableIdx));
                    intent.putExtra("subClass", mobData.get(3).get(tableIdx));
                    intent.putExtra("name", mobData.get(4).get(tableIdx));
                }else if(tableName.equals("nuvisionpaper")){
                    intent.putExtra("addr", paperData.get(1).get(tableIdx));
                    intent.putExtra("class", paperData.get(2).get(tableIdx));
                    intent.putExtra("subClass", paperData.get(3).get(tableIdx));
                    intent.putExtra("name", paperData.get(4).get(tableIdx));
                }

                startActivity(intent);
            }
        });

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);


        while (true) {

            // 데이터 전송 완료 까지 대기
            if(dataIO.getIsDataLoadFin()){
                ArrayList<ArrayList<String>> data = dataIO.getZeropayData();

                mobData = data;
                paperData = dataIO.getpaperData();
                reviewData = dataIO.getReviewData();

                // 자동 완성기능 붙이기
                Add_AutoComplete(mobData, "", "", "", "");
                // 클러스터 입력
                Add_ClusterItems(mobData, "", "", "", "");

                break;
            }

            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){}
        }


    }

    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String message = "내 위치 -> Latitude : " + latitude + "\nLongitude : " + longitude;

        }
    }

    private void Add_Filter_AutoComplete(AutoCompleteTextView auto, ArrayList<String> data){
        List<String> mylist = new ArrayList<>();

        auto.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mylist));

        ArrayList<String> temp = new ArrayList<String>();

        for(int i = 1; i < data.size(); i++){
            boolean IsExist = false;
            for(int j = 0; j < temp.size(); j++) {
                if (temp.get(j).equals(data.get(i))){
                    IsExist = true;
                    break;
                }
            }
            if(!IsExist){
                mylist.add(data.get(i));
                temp.add(data.get(i));
            }

        }

    }

    private void Add_AutoComplete(ArrayList<ArrayList<String>> data, String filter_addr,
                                  String filter_class, String filter_subClass, String filter_storename) {

        customItemList = new ArrayList<>();

        for (int i = 1; i < data.get(0).size(); i++){
            if(data.get(5).get(i).equals("1000") || data.get(6).get(i).equals("1000")){
                continue;
            }
            if(data.get(1).get(i).replace(" ", "").contains(filter_addr.replace(" ", "")) &&
                    data.get(2).get(i).replace(" ", "").contains(filter_class.replace(" ", "")) &&
                    data.get(3).get(i).replace(" ", "").contains(filter_subClass.replace(" ", "")) &&
                    data.get(4).get(i).replace(" ", "").contains(filter_storename.replace(" ", ""))){
                customItemList.add(new CustomItem(data.get(4).get(i), data.get(1).get(i), R.drawable.cafe, i));
            }

        }

        myAdapter = new CustomAutoCompleteAdapter(this, customItemList);

        getLoc.setAdapter(myAdapter);
        getLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView_idx = view.findViewById(R.id.customL_idx);
                int idx = Integer.valueOf(textView_idx.getText().toString());
                LatLng mv = new LatLng(Double.valueOf(data.get(5).get(idx)), Double.valueOf(data.get(6).get(idx)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mv));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mv, 20);
                mMap.animateCamera(cameraUpdate);
            }
        });
    }

    private void Add_ClusterItems(ArrayList<ArrayList<String>> data, String filter_addr,
                                  String filter_class, String filter_subClass, String filter_storename){
        for(int i = 1; i < data.get(0).size(); i++) {

            double lat = Double.valueOf(data.get(5).get(i));
            double lng = Double.valueOf(data.get(6).get(i));

            if(lat == 1000 || lng == 1000){
                continue;
            }

            if(data.get(1).get(i).replace(" ", "").contains(filter_addr.replace(" ", "")) &&
                    data.get(2).get(i).replace(" ", "").contains(filter_class.replace(" ", "")) &&
                    data.get(3).get(i).replace(" ", "").contains(filter_subClass.replace(" ", "")) &&
                    data.get(4).get(i).replace(" ", "").contains(filter_storename.replace(" ", ""))){

                int iconNum = 0;
                if(data.get(2).get(i).contains("카페") || data.get(2).get(i).contains("커피")){
                    iconNum = 21;
                }

                String tablename = "zeropaymobile";

                if(data.equals(mobData)){
                    tablename = "zeropaymobile";
                }else if(data.equals(paperData)){
                    tablename = "nuvisionpaper";
                }


                MyItem offsetItem = new MyItem(lat, lng, data.get(4).get(i),  tablename + " " + i, iconNum);

                clusterManager.addItem(offsetItem);
            }

        }
    }

    public void FilterClose(View v) {
        btn_filter.setEnabled(true);
        filterLayout.removeAllViews();
        Log.d("filter Close", "clicked");
    }
    public void FilterOn(View v) {

        String addr = getAddrFilter.getText().toString();
        String classs = getClassFilter.getText().toString();
        String subClass = getSubClassFilter.getText().toString();
        String name = getNameFilter.getText().toString();

        clusterManager.getMarkerCollection().clear();
        clusterManager.getClusterMarkerCollection().clear();
        clusterManager.clearItems();

        if(switch_mNp.isChecked()){
            Add_AutoComplete(paperData, addr, classs, subClass, name);
            Add_ClusterItems(paperData, addr, classs, subClass, name);
        }else {
            Add_AutoComplete(mobData, addr, classs, subClass, name);
            Add_ClusterItems(mobData, addr, classs, subClass, name);
        }

        btn_filter.setEnabled(true);
        filterLayout.removeAllViews();
    }
}



