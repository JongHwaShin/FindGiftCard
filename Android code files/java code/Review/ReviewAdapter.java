package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {
    //식당 화면에서 보여주는 화면 어뎁터

    //  Adapter에 추가된 데이터를 저장하기 위한 Arraylist
    private ArrayList<Store_rating_item> store_itemArrayList = new ArrayList<Store_rating_item>();

    //reviewAdater의 생성자
    public ReviewAdapter(){

    }
    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public  int getCount(){
        return  store_itemArrayList.size();
    }
    @Override
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_store_review,parent,false);
        }
        TextView user_id= (TextView) convertView.findViewById(R.id.user_id);
        RatingBar review_rating = (RatingBar) convertView.findViewById(R.id.storeRating);
        TextView review_date = (TextView) convertView.findViewById(R.id.review_date);
        TextView review_contents = (TextView) convertView.findViewById(R.id.review_contents);

        Store_rating_item store_rating_item = store_itemArrayList.get(position);

        user_id.setText(store_rating_item.getId());
        review_rating.setRating((float) store_rating_item.getStoreRating());
        review_date.setText(store_rating_item.getStoreRatingDate());
        review_contents.setText(store_rating_item.getStoreReview());

        return convertView;
    }

    @Override
    public long getItemId(int position){
        return position;
    }
    @Override

    public Object getItem(int position){
        return store_itemArrayList.get(position);
    }
    public void addItem(String id,Float star,String date, String contents){
        Store_rating_item item = new Store_rating_item();

        item.setId(id);
        item.setStoreRating(star);
        item.setStoreRatingDate(date);
        item.setStoreReview(contents);

        store_itemArrayList.add(item);
    }
}
