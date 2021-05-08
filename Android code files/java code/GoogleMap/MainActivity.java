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


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    FrameLayout container;
    AutoCompleteTextView getLoc;
    Button btn_search;
    ImageButton btn_filter;
    GoogleMap mMap;
    ImageButton mobileBtn;
    ImageButton papaerBtn;
    ImageButton bankBtn;

    LayoutInflater inflater_filter;

    DataIO dataIO;

    ArrayList<ArrayList<String>> mobData;
    ArrayList<ArrayList<String>> paperData;
    ArrayList<ArrayList<String>> reviewData;
    ArrayList<ArrayList<String>> bankData;

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

//        getSupportActionBar().setIcon(R.drawable.mojitok);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

                if(!mobileBtn.isEnabled()){
                    Add_Filter_AutoComplete(getAddrFilter, mobData.get(1));
                    Add_Filter_AutoComplete(getClassFilter, mobData.get(2));
                    Add_Filter_AutoComplete(getSubClassFilter, mobData.get(3));
                    Add_Filter_AutoComplete(getNameFilter, mobData.get(4));
                }else if(!papaerBtn.isEnabled()){
                    Add_Filter_AutoComplete(getAddrFilter, paperData.get(1));
                    Add_Filter_AutoComplete(getClassFilter, paperData.get(2));
                    Add_Filter_AutoComplete(getSubClassFilter, paperData.get(3));
                    Add_Filter_AutoComplete(getNameFilter, paperData.get(4));
                }else if(!bankBtn.isEnabled()){
                    Add_Filter_AutoComplete(getAddrFilter, bankData.get(1));
                    Add_Filter_AutoComplete(getClassFilter, bankData.get(2));
                    Add_Filter_AutoComplete(getSubClassFilter, bankData.get(3));
                    Add_Filter_AutoComplete(getNameFilter, bankData.get(4));
                }


            }
        });

        mobileBtn = findViewById(R.id.mobileData_btn);
        mobileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileBtn.setEnabled(false);
                papaerBtn.setEnabled(true);
                bankBtn.setEnabled(true);

                clusterManager.getMarkerCollection().clear();
                clusterManager.getClusterMarkerCollection().clear();
                clusterManager.clearItems();

                Add_ClusterItems(mobData, "", "", "", "");
                Add_AutoComplete(mobData, "", "", "", "");
            }
        });

        papaerBtn = findViewById(R.id.paperData_btn);
        papaerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileBtn.setEnabled(true);
                papaerBtn.setEnabled(false);
                bankBtn.setEnabled(true);

                clusterManager.getMarkerCollection().clear();
                clusterManager.getClusterMarkerCollection().clear();
                clusterManager.clearItems();

                Add_ClusterItems(paperData, "", "", "", "");
                Add_AutoComplete(paperData, "", "", "", "");
            }
        });

        bankBtn = findViewById(R.id.bankData_btn);
        bankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileBtn.setEnabled(true);
                papaerBtn.setEnabled(true);
                bankBtn.setEnabled(false);

                clusterManager.getMarkerCollection().clear();
                clusterManager.getClusterMarkerCollection().clear();
                clusterManager.clearItems();

                Add_ClusterItems(bankData, "", "", "", "");
                Add_AutoComplete(bankData, "", "", "", "");
            }
        });
        mobileBtn.setEnabled(false);

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
                }else if(text_idx_info.split(" ")[0].equals("bankinfo")){
                    data = bankData;
                }else {
                    data = mobData;
                }

                storeClass.setText(data.get(2).get(idx));
                storeSubClass.setText(data.get(3).get(idx));
                storeAddr.setText(data.get(1).get(idx));

                final RatingBar ratingBar = view.findViewById(R.id.store_rating_ratingBar);
                final TextView ratings = view.findViewById(R.id.store_ratingTextView);

                if(!data.equals(bankData)){
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
                        ratings.setText(String.format("%.1f", (rating_sum / rating_count)));
                    }

                    Log.d("Cluster", marker.getId());
                }else {
                    LinearLayout ratingLayout = view.findViewById(R.id.store_rating);
                    ratingLayout.setVisibility(View.GONE);

                    TextView banksubjText = view.findViewById(R.id.bankStokeSubj);
                    banksubjText.setVisibility(View.VISIBLE);
                    TextView bankDateText = view.findViewById(R.id.bankStokeDate);
                    bankDateText.setVisibility(View.VISIBLE);
                    String month = data.get(9).get(idx).split(" ")[1];
                    String days = data.get(9).get(idx).split(" ")[2];
                    bankDateText.setText(month + " " + days);

                    LinearLayout bank5000Layout = view.findViewById(R.id.bankStoke5000Layout);
                    bank5000Layout.setVisibility(View.VISIBLE);
                    LinearLayout bank10000Layout = view.findViewById(R.id.bankStoke10000Layout);
                    bank10000Layout.setVisibility(View.VISIBLE);

                    TextView bank5000Text = view.findViewById(R.id.bankStoke5000Text);
                    TextView bank10000Text = view.findViewById(R.id.bankStoke10000Text);

                    bank5000Text.setText(data.get(7).get(idx) + "장");
                    bank10000Text.setText(data.get(8).get(idx) + "장");
                }

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
                }else if(tableName.equals("bankinfo")){
                    intent.putExtra("addr", bankData.get(1).get(tableIdx));
                    intent.putExtra("class", bankData.get(2).get(tableIdx));
                    intent.putExtra("subClass", bankData.get(3).get(tableIdx));
                    intent.putExtra("name", bankData.get(4).get(tableIdx));
                }

                startActivityForResult(intent, 666);
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
                bankData = dataIO.getBankData();

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

                int drowableSettings = R.drawable.store;

                if(data.get(2).get(i).contains("카페") || data.get(2).get(i).contains("커피")){
                    drowableSettings = R.drawable.cafe;
                }else if(data.get(2).get(i).contains("가구")){
                    drowableSettings = R.drawable.furniture;
                }else if(data.get(2).get(i).contains("가발")){
                    drowableSettings = R.drawable.wig;
                }else if(data.get(2).get(i).contains("가전")){
                    drowableSettings = R.drawable.homeapperance;
                }else if(data.get(2).get(i).contains("광고") || data.get(2).get(i).contains("알림판")){
                    drowableSettings = R.drawable.add;
                }else if(data.get(2).get(i).contains("pc") || data.get(2).get(i).contains("컴퓨터")){
                    drowableSettings = R.drawable.pc;
                }else if(data.get(2).get(i).contains("음식") || data.get(2).get(i).contains("요식")){
                    drowableSettings = R.drawable.foodchain;
                }else if(data.get(2).get(i).contains("은행") || data.equals(bankData)){
                    drowableSettings = R.drawable.bank;
                }else if(data.get(2).get(i).contains("생화") || data.get(2).get(i).contains("꽃집")){
                    drowableSettings = R.drawable.flower;
                }else if(data.get(2).get(i).contains("학원") || data.get(2).get(i).contains("교습")){
                    drowableSettings = R.drawable.study;
                }else if(data.get(2).get(i).contains("서점") || data.get(2).get(i).contains("책방")){
                    drowableSettings = R.drawable.book;
                }else if(data.get(2).get(i).contains("치킨") || data.get(2).get(i).contains("통닭")){
                    drowableSettings = R.drawable.chicken;
                }else if(data.get(2).get(i).contains("부동산")){
                    drowableSettings = R.drawable.house;
                }else if(data.get(2).get(i).contains("건설") || data.get(2).get(i).contains("인테리어")){
                    drowableSettings = R.drawable.build;
                }else if(data.get(2).get(i).contains("옷가게") || data.get(2).get(i).contains("옷수선")
                        || data.get(2).get(i).contains("한복")){
                    drowableSettings = R.drawable.cloth;
                }else if(data.get(2).get(i).contains("동물병원")){
                    drowableSettings = R.drawable.pethospital;
                }else if(data.get(2).get(i).contains("병원") || data.get(2).get(i).contains("한의원")){
                    drowableSettings = R.drawable.hospital;
                }else if(data.get(2).get(i).contains("미용실") || data.get(2).get(i).contains("이발소")){
                    drowableSettings = R.drawable.haircut;
                }else if(data.get(2).get(i).contains("도넛") || data.get(2).get(i).contains("분식")){
                    drowableSettings = R.drawable.desert;
                }else if(data.get(2).get(i).contains("수작업") || data.get(2).get(i).contains("공예")){
                    drowableSettings = R.drawable.craft;
                }else if(data.get(2).get(i).contains("과일") || data.get(2).get(i).contains("체소")){
                    drowableSettings = R.drawable.froot;
                }else if(data.get(2).get(i).contains("기계") || data.get(2).get(i).contains("공업")){
                    drowableSettings = R.drawable.muchine;
                }else if(data.get(2).get(i).contains("네일아트") || data.get(2).get(i).contains("손톱")){
                    drowableSettings = R.drawable.nailart;
                }else if(data.get(2).get(i).contains("법률") || data.get(2).get(i).contains("세무")){
                    drowableSettings = R.drawable.law;
                }else if(data.get(2).get(i).contains("노인") || data.get(2).get(i).contains("복지")){
                    drowableSettings = R.drawable.elder;
                }else if(data.get(2).get(i).contains("노래방")){
                    drowableSettings = R.drawable.sing;
                }else if(data.get(2).get(i).contains("뷰티") || data.get(2).get(i).contains("미용")){
                    drowableSettings = R.drawable.beauty;
                }else if(data.get(2).get(i).contains("프린트") || data.get(2).get(i).contains("복사")){
                    drowableSettings = R.drawable.print;
                }else if(data.get(2).get(i).contains("목욕")){
                    drowableSettings = R.drawable.bath;
                }else if(data.get(2).get(i).contains("술집") || data.get(2).get(i).contains("주류")){
                    drowableSettings = R.drawable.beer;
                }else if(data.get(2).get(i).contains("세탁")){
                    drowableSettings = R.drawable.wash;
                }else if(data.get(2).get(i).contains("육류") || data.get(2).get(i).contains("고기")){
                    drowableSettings = R.drawable.meat;
                }else if(data.get(2).get(i).contains("악세서리") || data.get(2).get(i).contains("보석")
                        || data.get(2).get(i).contains("금은")){
                    drowableSettings = R.drawable.accessories;
                }else if(data.get(2).get(i).contains("애완동물") || data.get(2).get(i).contains("반려동물")){
                    drowableSettings = R.drawable.pet;
                }else if(data.get(2).get(i).contains("약국")){
                    drowableSettings = R.drawable.parmacy;
                }else if(data.get(2).get(i).contains("악기") || data.get(2).get(i).contains("피아노")){
                    drowableSettings = R.drawable.instrument;
                }else if(data.get(2).get(i).contains("자동차")){
                    drowableSettings = R.drawable.car;
                }else if(data.get(2).get(i).contains("공구")){
                    drowableSettings = R.drawable.tool;
                }else if(data.get(2).get(i).contains("편의점")){
                    drowableSettings = R.drawable.store;
                }
                customItemList.add(new CustomItem(data.get(4).get(i), data.get(1).get(i), drowableSettings, i));
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

                iconNum = getIconNum(data, i);

                String tablename = "zeropaymobile";

                if(data.equals(mobData)){
                    tablename = "zeropaymobile";
                }else if(data.equals(paperData)){
                    tablename = "nuvisionpaper";
                }else if(data.equals(bankData)){
                    tablename = "bankinfo";
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

        if(!mobileBtn.isEnabled()){
            Add_AutoComplete(mobData, addr, classs, subClass, name);
            Add_ClusterItems(mobData, addr, classs, subClass, name);
        }else if(!papaerBtn.isEnabled()){
            Add_AutoComplete(paperData, addr, classs, subClass, name);
            Add_ClusterItems(paperData, addr, classs, subClass, name);
        }else if(!bankBtn.isEnabled()){
            Add_AutoComplete(bankData, addr, classs, subClass, name);
            Add_ClusterItems(bankData, addr, classs, subClass, name);
        }

        btn_filter.setEnabled(true);
        filterLayout.removeAllViews();
    }

    private int getIconNum(ArrayList<ArrayList<String>> data, int i){
        int iconNum = 0;
        if(data.get(2).get(i).contains("카페") || data.get(2).get(i).contains("커피")){
            iconNum = 21;
        }else if(data.get(2).get(i).contains("가구")){
            iconNum = 22;
        }else if(data.get(2).get(i).contains("가발")){
            iconNum = 23;
        }else if(data.get(2).get(i).contains("가전")){
            iconNum = 24;
        }else if(data.get(2).get(i).contains("광고") || data.get(2).get(i).contains("알림판")){
            iconNum = 25;
        }else if(data.get(2).get(i).contains("pc") || data.get(2).get(i).contains("컴퓨터")){
            iconNum = 26;
        }else if(data.get(2).get(i).contains("음식") || data.get(2).get(i).contains("요식")){
            iconNum = 27;
        }else if(data.get(2).get(i).contains("은행") || data.equals(bankData)){
            iconNum = 28;
        }else if(data.get(2).get(i).contains("생화") || data.get(2).get(i).contains("꽃집")){
            iconNum = 29;
        }else if(data.get(2).get(i).contains("학원") || data.get(2).get(i).contains("교습")){
            iconNum = 30;
        }else if(data.get(2).get(i).contains("서점") || data.get(2).get(i).contains("책방")){
            iconNum = 31;
        }else if(data.get(2).get(i).contains("치킨") || data.get(2).get(i).contains("통닭")){
            iconNum = 32;
        }else if(data.get(2).get(i).contains("부동산")){
            iconNum = 33;
        }else if(data.get(2).get(i).contains("건설") || data.get(2).get(i).contains("인테리어")){
            iconNum = 34;
        }else if(data.get(2).get(i).contains("옷가게") || data.get(2).get(i).contains("옷수선")
                || data.get(2).get(i).contains("한복")){
            iconNum = 35;
        }else if(data.get(2).get(i).contains("동물병원")){
            iconNum = 38;
        }else if(data.get(2).get(i).contains("병원") || data.get(2).get(i).contains("한의원")){
            iconNum = 36;
        }else if(data.get(2).get(i).contains("미용실") || data.get(2).get(i).contains("이발소")){
            iconNum = 37;
        }else if(data.get(2).get(i).contains("도넛") || data.get(2).get(i).contains("분식")){
            iconNum = 39;
        }else if(data.get(2).get(i).contains("수작업") || data.get(2).get(i).contains("공예")){
            iconNum = 40;
        }else if(data.get(2).get(i).contains("과일") || data.get(2).get(i).contains("체소")){
            iconNum = 41;
        }else if(data.get(2).get(i).contains("기계") || data.get(2).get(i).contains("공업")){
            iconNum = 42;
        }else if(data.get(2).get(i).contains("네일아트") || data.get(2).get(i).contains("손톱")){
            iconNum = 43;
        }else if(data.get(2).get(i).contains("법률") || data.get(2).get(i).contains("세무")){
            iconNum = 44;
        }else if(data.get(2).get(i).contains("노인") || data.get(2).get(i).contains("복지")){
            iconNum = 45;
        }else if(data.get(2).get(i).contains("노래방")){
            iconNum = 46;
        }else if(data.get(2).get(i).contains("뷰티") || data.get(2).get(i).contains("미용")){
            iconNum = 47;
        }else if(data.get(2).get(i).contains("프린트") || data.get(2).get(i).contains("복사")){
            iconNum = 48;
        }else if(data.get(2).get(i).contains("목욕")){
            iconNum = 49;
        }else if(data.get(2).get(i).contains("술집") || data.get(2).get(i).contains("주류")){
            iconNum = 50;
        }else if(data.get(2).get(i).contains("세탁")){
            iconNum = 51;
        }else if(data.get(2).get(i).contains("육류") || data.get(2).get(i).contains("고기")){
            iconNum = 52;
        }else if(data.get(2).get(i).contains("악세서리") || data.get(2).get(i).contains("보석")
                || data.get(2).get(i).contains("금은")){
            iconNum = 53;
        }else if(data.get(2).get(i).contains("애완동물") || data.get(2).get(i).contains("반려동물")){
            iconNum = 54;
        }else if(data.get(2).get(i).contains("약국")){
            iconNum = 55;
        }else if(data.get(2).get(i).contains("악기") || data.get(2).get(i).contains("피아노")){
            iconNum = 56;
        }else if(data.get(2).get(i).contains("자동차")){
            iconNum = 59;
        }else if(data.get(2).get(i).contains("공구")){
            iconNum = 60;
        }else if(data.get(2).get(i).contains("편의점")){
            iconNum = 61;
        }

        return iconNum;
    }
}



