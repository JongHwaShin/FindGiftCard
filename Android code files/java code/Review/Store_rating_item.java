package com.example.myapplication;

public class Store_rating_item {
    private String userid; //사용자id
    private float storeRating; //별점
    private  String storeRatingText; //별점문자화 ex(3.5)
    private String storeRatingDate; //평가날짜
    private String storeReview; //리뷰



    public String getId() {
        return this.userid;
    }

    public void setId(String id) {
        this.userid = id;
    }

    public double getStoreRating() {
        return this.storeRating;
    }

    public void setStoreRating(float rate) {
        this.storeRating = rate;
    }

    public String getStoreRatingText() {
        return this.storeRatingText;
    }

    public void setStoreRatingText(String ratingText) {
        this.storeRatingText = ratingText;
    }

    public String getStoreRatingDate() {
        return this.storeRatingDate;
    }

    public void setStoreRatingDate(String Date) {
        this.storeRatingDate = Date;
    }

    public String getStoreReview() {
        return storeReview;
    }

    public void setStoreReview(String Review) {
        this.storeReview = Review;
    }
}
