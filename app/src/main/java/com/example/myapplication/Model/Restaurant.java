package com.example.myapplication.Model;

import java.util.List;

public class Restaurant {

    private String restaurantId;
    private int likeCount;
    private List<String> userIdSelected;

    // necessary empty constructor for fireStore
    public Restaurant() {
    }

    public Restaurant(String restaurantId, int likeCount, List<String> userIdSelected) {
        this.restaurantId = restaurantId;
        this.likeCount = likeCount;
        this.userIdSelected = userIdSelected;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getUserIdSelected() {
        return userIdSelected;
    }

    public void setUserIdSelected(List<String> userIdSelected) {
        this.userIdSelected = userIdSelected;
    }
}
