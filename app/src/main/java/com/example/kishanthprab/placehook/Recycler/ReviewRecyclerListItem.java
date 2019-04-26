package com.example.kishanthprab.placehook.Recycler;

import android.graphics.Bitmap;

import com.example.kishanthprab.placehook.DataObjects.PlaceModels.Photos;

public class ReviewRecyclerListItem {

    String authorName;
    double givenRating;
    String photoUrl;
    String relativeTime;
    String review_text;

    public String getReviewIconType() {
        return reviewIconType;
    }

    public void setReviewIconType(String reviewIconType) {
        this.reviewIconType = reviewIconType;
    }

    String reviewIconType;

    public ReviewRecyclerListItem(String authorName, double givenRating, String photoUrl, String relativeTime, String review_text) {
        this.authorName = authorName;
        this.givenRating = givenRating;
        this.photoUrl = photoUrl;
        this.relativeTime = relativeTime;
        this.review_text = review_text;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public double getGivenRating() {
        return givenRating;
    }

    public void setGivenRating(double givenRating) {
        this.givenRating = givenRating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(String relativeTime) {
        this.relativeTime = relativeTime;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }
}
