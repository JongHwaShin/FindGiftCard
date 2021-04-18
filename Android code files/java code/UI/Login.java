package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class Login extends AppCompatActivity {

    private View loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.kakaologin);
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if(oAuthToken != null){
                    Toast.makeText(Login.this,"로그인되었습니다.",Toast.LENGTH_SHORT).show(); // 로그인 Toast 메시지
                }
                if(throwable != null){

                }

                Go_review_write(); // 로그인 되면 글쓰기로 넘어감
                return null;
            }
        };
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(Login.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(Login.this,callback);

                }else{
                    UserApiClient.getInstance().loginWithKakaoAccount(Login.this,callback );
                }
            }
        });
    }

    // 로그인 되면 글쓰기로 바로 넘겨주는 인텐트
    private void Go_review_write(){
        Intent intent = new Intent(getApplicationContext(),Review_Write_View.class);
        startActivity(intent);
    }


}