package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class Review_Write_View extends AppCompatActivity {

    private static final String TAG = "Review_Write_View";
    ReviewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review__write__view);
        //리뷰치는곳
        EditText review_write = (EditText)findViewById(R.id.store_reviewWrite);
        //별점
        RatingBar review_rating = (RatingBar)findViewById(R.id.giveStar);
        //리뷰작성버튼
        Button reveiw_apply = (Button)findViewById(R.id.reviewApply);
        long nowtime = System.currentTimeMillis();
        Date mDate = new Date(nowtime);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = simpleDate.format(mDate);

        Intent getStoreInfo = getIntent();
        String storeName = getStoreInfo.getStringExtra("name");
        String storeAddr = getStoreInfo.getStringExtra("addr");




        //리뷰작성버튼 클릭했을때
        reveiw_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((review_write.getText().toString()).length() == 0 ){
                    Toast.makeText(Review_Write_View.this,"리뷰내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                }

                else {
                    UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(User user, Throwable throwable) {
                            if(user != null){
                                String sql = "INSERT INTO reviewtable (storename, addr, review, rating, reviewtime, kakaoid) " +
                                        "VALUES ( '" + storeName + "', '" + storeAddr + "', '" + review_write.getText().toString() + "', " +
                                        "'" + review_rating.getRating() + "', " +
                                        "SYSDATE(), " +
                                        "'" + user.getKakaoAccount().getProfile().getNickname() + "' )";

                                SQLRun run = new SQLRun(sql);
                                run.start();
                            }

                            Toast.makeText(Review_Write_View.this,"리뷰가 정상적으로 입력되었습니다.",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.putExtra("review", review_write.getText().toString());
                            intent.putExtra("rating", review_rating.getRating());
                            intent.putExtra("kakaoID", user.getKakaoAccount().getProfile().getNickname());
                            setResult(RESULT_OK, intent);
                            finish();

                            return null;
                        }
                    });

                }

            }
        });




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

            ListView review_write_listView;

            adapter = new ReviewAdapter();

            review_write_listView = (ListView)findViewById(R.id.store_review);
            review_write_listView.setAdapter(adapter);

            for(int i = 1; i < reviewData.get(0).size(); i++){
                adapter.addItem(reviewData.get(6).get(i), Float.valueOf(reviewData.get(4).get(i))
                        , reviewData.get(5).get(i), reviewData.get(3).get(i));

            }
        }

        //adapter.addItem("신종화", (float) 3.4,"2020-1-2","맛있어요");

    }
}