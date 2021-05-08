package com.example.myapplication;

import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;

public class DataIO extends Thread {
    private boolean isDataLoadFin = false;
    private String url_sql = "YOUR SERVER URL?q=";
    private String calc_loc = "YOUR SERVER URL/loc?";
    private String googlePlaceApi = "https://maps.googleapis.com/maps/api/place/textsearch/json?key="
            + new KEY().getGoogleApiKey()
            + "&query=";
    private String jusoStreetNameApi = "https://www.juso.go.kr/addrlink/addrLinkApi.do?"
            + "confmKey=" + new KEY().getJusoStreetNameApiKey()
            + "&resultType=json"
            + "&keyword=";
    private String jusoLocationApi = "https://www.juso.go.kr/addrlink/addrCoordApi.do?"
            + "confmKey=" + new KEY().getJusoLocationApiKey()
            + "&resultType=json";


    private ArrayList<ArrayList<String>> zeropayData;
    private ArrayList<ArrayList<String>> paperData;
    private ArrayList<ArrayList<String>> reviewData;
    private ArrayList<ArrayList<String>> bankData;

    public boolean getIsDataLoadFin() {
        return isDataLoadFin;
    }
    public ArrayList<ArrayList<String>> getZeropayData(){
        return zeropayData;
    }
    public ArrayList<ArrayList<String>> getpaperData() {
        return paperData;
    }
    public ArrayList<ArrayList<String>> getReviewData() {
        return reviewData;
    }
    public ArrayList<ArrayList<String>> getBankData() {
        return bankData;
    }


    @Override
    public void run() {
        super.run();

        if(zeropayData == null){
            String tablename = "zeropaymobile";
            ArrayList<ArrayList<String>> zero = getTable(tablename);
            zeropayData = zero;
        }

        if (paperData == null) {
            String tablename2 = "nuvisionpaper";
            ArrayList<ArrayList<String>> paper = getTable(tablename2);
            paperData = paper;
        }

        if(reviewData == null) {
            String reviewtable = "reviewtable";
            ArrayList<ArrayList<String>> review = getTable(reviewtable);
            reviewData = review;
        }

        if(bankData == null) {
            String banktable = "bankinfo";
            ArrayList<ArrayList<String>> bankinfo = getTable(banktable);
            bankData = bankinfo;
        }

        //LocationUpdate(bankData, "bankinfo");

        isDataLoadFin = true;



    }

    // 띄워쓰기 단위로 분할 후 idx만큼 다시 재조립
    private String reUnionStr(String strings, int idx){
        String result = "";
        String [] string_split = strings.split(" ");
        if(string_split.length < idx){
            return strings;
        }else {
            for(int i = 0; i < idx; i++) {
                result = result + string_split[i] + " ";
            }
            return result;
        }
    }

    // 웹 파싱, 출처: https://digitalbourgeois.tistory.com/57 [IT 글자국]
    private String parser(String _url){

        try {
            URL url = new URL(_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000); //서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정

            con.setRequestMethod("GET");

            con.setDoOutput(false);

            StringBuilder sb = new StringBuilder();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //Stream을 처리해줘야 하는 귀찮음이 있음.
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                return sb.toString();

            } else {
                Log.d("parse Err", "" + con.getResponseMessage());
                return null;
            }

        } catch (Exception e) {
            Log.d("parse Err", "" + e.toString());
            return null;
        }
    }

    // mysql 데이터 파싱
    private String sql_parser(String url){
        try {
            Log.d("jsuo API", url);
            Document doc = Jsoup.connect(url).get();

            String text = "";

            text = doc.body().getElementsByTag("pre").text();

            return text;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // mysql 데이터를 배열 형식으로 저장
    private ArrayList<ArrayList<String>> getTable(String table) {
        ArrayList<ArrayList<String>> zero = new ArrayList<ArrayList<String>>();

        // 데이터 수 파악
        String maxIDX = sql_parser(url_sql + "SELECT MAX(idx) FROM " + table + ";");
        int maxIdx = Integer.valueOf(maxIDX.split(",")[1]);

        // 컬럼 수 파악
        String cout_col = sql_parser(url_sql + "SELECT * FROM " + table + " WHERE idx < 2;");
        int colIdx = cout_col.split("\n").length;

        for(int i = 0; i < colIdx; i++) {
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(cout_col.split("\n")[i].split(",")[0]);
            zero.add(temp);
        }

        // 한번에 하면 좋겠지만 한번에 다 배열에 저장 시 에러나서 1000번씩 끊어 넣음
        for(int i = 0; i < maxIdx / 1000; i++){

            String sql = url_sql + "SELECT * FROM " + table + " " +
                    "WHERE idx >= " + (1000 * i) + " " +
                    "AND idx < " + (1000 * (i + 1));

            String zeroPayData = sql_parser(sql);
            String [] col = zeroPayData.split("\n");
            for(int j =0; j < col.length; j++) {
                String [] temp = col[j].split(",");
                for(int k = 1; k < temp.length; k++) {
                    zero.get(j).add(temp[k]);
                }
            }
        }

        String sql = url_sql + "SELECT * FROM " + table + " " +
                "WHERE idx >= " + (1000 * (maxIdx / 1000)) +  " " +
                "AND idx <= " + maxIdx;
        Log.d("ZeropayMobile", sql);

        String zeroPayData = sql_parser(sql);
        String [] col = zeroPayData.split("\n");
        for(int j =0; j < col.length; j++) {
            String [] temp = col[j].split(",");
            for(int k = 1; k < temp.length; k++) {
                zero.get(j).add(temp[k]);
            }
        }

        return zero;
    }

    private void LocationUpdate(ArrayList<ArrayList<String>> data, String tablename) {

        int control_str_length = 6;
        String update_query = "UPDATE " + tablename + " SET %0A";
        String lat_condition = "";
        String lng_condition = "";
        String WHERE_condition = " WHERE idx > 0;";

        for(int i = 1; i < data.get(0).size(); i++){

            double lat = Double.valueOf(data.get(5).get(i));
            double lng = Double.valueOf(data.get(6).get(i));

            if(!data.get(5).get(i).equals("1000") && !data.get(5).get(i).equals("1000")){
                continue;
            }

            try {
                sleep(300);
            }catch (InterruptedException e){

            }

            String keywords = reUnionStr(data.get(1).get(i), control_str_length);
            Log.d("juso API", "idx : " + i + " keyword : " + keywords);

            String admCd = "&admCd=";           // 행정구역코드
            String rnMgtSn = "&rnMgtSn=";       // 도로명코드
            String udrtYn = "&udrtYn=";         // 지하여부
            String buldMnnm = "&buldMnnm=";   // 건물본번
            String buldSlno = "&buldSlno=";     // 건물부번
            String entX = "x=";                 // x좌표
            String entY = "y=";                 // y좌표

            String json = parser(jusoStreetNameApi + keywords);
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject results = jsonObject.getJSONObject("results");
                String jusoStr = results.getString("juso");
                JSONArray jusos = new JSONArray(jusoStr);
                JSONObject juso = jusos.getJSONObject(0);

                admCd = admCd + juso.getString("admCd");
                rnMgtSn = rnMgtSn + juso.getString("rnMgtSn");
                udrtYn = udrtYn + juso.getString("udrtYn");
                buldMnnm = buldMnnm + juso.getString("buldMnnm");
                buldSlno = buldSlno + juso.getString("buldSlno");

                control_str_length = 6;

            }catch (JSONException e){
                Log.d("juso Api", "can not find juso idx : " + control_str_length + " keywords : " + keywords);

                if(control_str_length >= 4) {
                    i = i - 1;
                    control_str_length = control_str_length - 1;
                }
                continue;
            }

//            Log.d("juso API", "admCd : " + admCd +  "\nrnMgtSn : " + rnMgtSn
//                    + "\nudrtYn : " +udrtYn + "\nbuldMnnm : " + buldMnnm
//                    + "\nbuldSlno : " + buldSlno);
//            Log.d("juso API", jusoLocationApi + admCd + rnMgtSn + udrtYn + buldMnnm + buldSlno);

            String LocJson = parser(jusoLocationApi + admCd + rnMgtSn + udrtYn + buldMnnm + buldSlno);
            try {
                JSONObject jsonObject = new JSONObject(LocJson);
                JSONObject results = jsonObject.getJSONObject("results");
                String jusoStr = results.getString("juso");
                JSONArray jusos = new JSONArray(jusoStr);
                JSONObject juso = jusos.getJSONObject(0);

                entX = entX + juso.getString("entX");
                entY = entY + juso.getString("entY");

            }catch (JSONException e){
                Log.d("juso API", "can not find Location keywords : " + keywords);
                continue;
            }
            //Log.d("juso API", "entX : " + entX + " entY : " + entY);

            String getLoc = sql_parser(new KEY().getMYNodeServer("loc") + entX + "&" + entY);


            if(getLoc == null){
                Log.d("juso API", "can not get Location");
                continue;
            }
            if(getLoc.split(",")[1] == null || getLoc.split(",")[0] == null){
                Log.d("juso API", "can not get Location");
                continue;
            }
//            Log.d("juso Api", calc_loc + entX + "&" + entY);
//            Log.d("juso Api", getLoc);
            data.get(5).set(i, getLoc.split(",")[1]);
            data.get(6).set(i, getLoc.split(",")[0]);

            lat = Double.valueOf(getLoc.split(",")[1]);
            lng = Double.valueOf(getLoc.split(",")[0]);

//            Log.d("juso Api", "lat = " + lat + " lng = " + lng);
//
//            Log.d("juso API", url_sql + "UPDATE " + tablename
//                                                + " SET lat = " + lat + ", lng = " + lng
//                                                + " WHERE idx = " + data.get(0).get(i) + ";");
//            sql_parser(url_sql + "UPDATE " + tablename
//                                + " SET lat = " + lat + ", lng = " + lng
//                                + " WHERE idx = " + data.get(0).get(i) + ";");

            lat_condition = " WHEN " + i + " THEN " + lat + " %0A ";
            sql_parser(new KEY().getMYNodeServer("lat") + lat_condition);
            lng_condition = " WHEN " + i + " THEN " + lng + " %0A ";
            sql_parser(new KEY().getMYNodeServer("lng") + lng_condition);
        }




    }

}
