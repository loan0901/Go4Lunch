package com.example.myapplication.Model;

import java.util.List;

public class User {

    private String userId;
    private String userName;
    private List<String> favoriteRestaurants;
    private String selectedRestaurantId;
    private String selectedRestaurantName;
    private String photoUrl;

    // necessary empty constructor for fireStore
    public User() {
    }

    public User(String userId, List<String> favoriteRestaurants, String selectedRestaurantId, String selectedRestaurantName, String photoUrl, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.favoriteRestaurants = favoriteRestaurants;
        this.selectedRestaurantId = selectedRestaurantId;
        this.selectedRestaurantName = selectedRestaurantName;
        this.photoUrl = photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public void setFavoriteRestaurants(List<String> favoriteRestaurants) {this.favoriteRestaurants = favoriteRestaurants;}

    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }

    public void setSelectedRestaurantId(String selectedRestaurantId) {this.selectedRestaurantId = selectedRestaurantId;}

    public String getSelectedRestaurantName() {return selectedRestaurantName;}

    public void setSelectedRestaurantName(String selectedRestaurantName) {this.selectedRestaurantName = selectedRestaurantName;}

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
