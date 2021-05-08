package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainPage extends AppCompatActivity {

    private View loginbutton,logoutbutton; // 로그인 로그아웃 버튼

    ImageButton showMapBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        loginbutton = findViewById(R.id.loginButton); //로그인 버튼
        logoutbutton = findViewById(R.id.logoutButton); //로그아웃버튼
        showMapBtn = findViewById(R.id.mapButton);
        updateKakaoLoginUI();

        showMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        updateKakaoLoginUI();

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인버튼누르면 로그인창으로이동
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 누르면 로그아웃된다.
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUI();
                        Toast.makeText(MainPage.this,"로그아웃되었습니다",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                });
            }
        });
    }
    private void updateKakaoLoginUI(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null){
                    //로그인 되어있으면 로그아웃버튼을 보여줌
                    loginbutton.setVisibility(View.GONE);
                    logoutbutton.setVisibility(View.VISIBLE);
                }
                else {
                    //로그아웃 상태면 로그인 버튼을 보여줌
                    loginbutton.setVisibility(View.VISIBLE);
                    logoutbutton.setVisibility(View.GONE);
                }
                return null;
            }
        });
    }

}