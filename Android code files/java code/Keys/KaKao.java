package com.example.myapplication;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KaKao extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this,"YOUR KAKAO API KEY");
    }
}