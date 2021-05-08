package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class Store_main extends AppCompatActivity {
    private static final String TAG = "Store_main";
    private TextView storename_TextView;
    private TextView addr_TextView;
    private TextView class_TextView;
    private TextView subClass_TextView;
    private RatingBar store_RatingBar;
    private TextView store_RatingText;

    ReviewAdapter adapter;

    String storeName;
    String storeAddr;
    String storeClass;
    String storeSubClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_main);
        FloatingActionButton review_write_go_button = (FloatingActionButton) findViewById(R.id.mainWriteButton);

        storename_TextView = findViewById(R.id.store_name);
        addr_TextView = findViewById(R.id.store_address);
        class_TextView = findViewById(R.id.store_bigType);
        subClass_TextView = findViewById(R.id.store_smallType);
        store_RatingBar = findViewById(R.id.store_rating_ratingBar);
        store_RatingText = findViewById(R.id.store_ratingTextView);

        Intent getStoreInfo = getIntent();
        storeName = getStoreInfo.getStringExtra("name");
        storeAddr = getStoreInfo.getStringExtra("addr");
        storeClass = getStoreInfo.getStringExtra("class");
        storeSubClass = getStoreInfo.getStringExtra("subClass");

        storename_TextView.setText(storeName);
        addr_TextView.setText(storeAddr);
        class_TextView.setText(storeClass);
        subClass_TextView.setText(storeSubClass);

        review_write_go_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                    @Override
                    public Unit invoke(User user, Throwable throwable) {
                        if (user != null){

                            Intent intent = new Intent(getApplicationContext(),Review_Write_View.class);
                            intent.putExtra("name", storeName);
                            intent.putExtra("addr", storeAddr);
                            startActivityForResult(intent, 444);

                        }
                        else {
                            showAlert();
                        }
                        return null;
                    }
                });
            }
        });

        ListView store_main_review;

        // 어뎁터생성
        adapter = new ReviewAdapter();

        store_main_review = (ListView)findViewById(R.id.storeRatingListView);
        store_main_review.setAdapter(adapter);

        setReview();
        //adapter.addItem("신종화", (float) 3.4,"2020-1-2","맛있어요");

    }
    // 로그인 안돼있으면 로그인하겠습니까 라고 띄워주는 Alert창
    private void showAlert(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Store_main.this)
                .setTitle("로그인이 필요한 서비스 입니다.")
                .setMessage("로그인하시겠습니까?")
                .setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent2 = new Intent(getApplicationContext(),Login.class);
                        startActivity(intent2);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDlg = alertBuilder.create();
        alertDlg.show();
    }
    // 로그아웃해주는 메뉴탭 생성 메뉴xml도 작성되어있음
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.logoutmenu,menu);
        return true;
    }
    //로그아웃 버튼 눌렀을때 실행되는거
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutmenu:
                //로그아웃해주는코드
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Toast.makeText(Store_main.this,"로그아웃되었습니다..",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                });
                return true;
        }
        return false;
    }

    private void setReview(){
        SQLRun getReviewData = new SQLRun("SELECT * FROM reviewtable"
                + " WHERE storename = '" + storeName + "'"
                + " AND addr = '" + storeAddr + "';");
        getReviewData.start();

        String results = "";

        while (true){
            try {
                Thread.sleep(300);
            }catch (InterruptedException e){

            }
            if(getReviewData.getisFin()){
                results = getReviewData.getValues();
                break;
            }

        }

        if(results != null && results.contains("\n")){
            ArrayList<ArrayList<String>> reviewData = new ArrayList<ArrayList<String>>();
            String [] rows = results.split("\n");
            for(int i = 0; i < rows.length; i++) {
                String [] data = rows[i].split(",");
                ArrayList<String> row = new ArrayList<String>();
                for(int j = 0; j < data.length; j++){
                    row.add(data[j]);
                }
                reviewData.add(row);
            }

            float sum = 0f;
            int count = 0;
            for(int i = 1; i < reviewData.get(0).size(); i++){
                adapter.addItem(reviewData.get(6).get(i), Float.valueOf(reviewData.get(4).get(i))
                        , reviewData.get(5).get(i), reviewData.get(3).get(i));

                sum += Float.valueOf(reviewData.get(4).get(i));
                count++;

            }

            if(count != 0){
                store_RatingBar.setRating(sum / count);
                store_RatingText.setText("" + (sum / count));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 444){
            if(resultCode == RESULT_OK){
                Log.d("AdapterTest", "read");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String time = dateFormat.format(date);

                String id = data.getStringExtra("kakaoID");
                float rating = data.getFloatExtra("rating", 0);
                String review = data.getStringExtra("review");

                Log.d("adapterTest", id + rating + review);

                adapter.addItem(id, rating, time, review);

            }
        }
    }



}