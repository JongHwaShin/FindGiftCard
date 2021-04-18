package com.example.myapplication;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SQLRun extends Thread{
    private String url = "YOUR SERVER URL?q=";
    private String values = "";
    private boolean isFin = false;

    public SQLRun(String url) {
        this.url += url;
    }

    public String getValues() {
        return values;
    }
    public boolean getisFin(){
        return isFin;
    }

    @Override
    public void run() {
        super.run();

        values = sql_parser(url);
        Log.d("SQL test", "query : " + url);
        Log.d("SQL test", "val : \n" + values);
        isFin = true;
    }

    private String sql_parser(String url){
        try {
            Document doc = Jsoup.connect(url).get();

            String text = "";

            text = doc.body().getElementsByTag("pre").text();

            return text;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}