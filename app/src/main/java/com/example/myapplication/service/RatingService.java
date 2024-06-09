package com.example.myapplication.service;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;
import com.example.myapplication.SharedPlaceViewModel;

import java.util.List;

public class RatingService {

    private final SharedPlaceViewModel sharedPlaceViewModel;

    // Constructor for RatingService.
    public RatingService(SharedPlaceViewModel sharedPlaceViewModel) {
        this.sharedPlaceViewModel = sharedPlaceViewModel;
    }

    // Interface for a listener to handle the computed rating.
    public interface OnRatingComputedListener {
        void onRatingComputed(int rating);
        void onError(Exception e);
    }

    /**
     * Computes the rating of a restaurant based on the number of likes and total users.
     *
     * @param restaurantId The ID of the restaurant whose rating is to be computed.
     * @param listener The listener to handle the computed rating or any error.
     */
    public void computeRating(String restaurantId, OnRatingComputedListener listener) {
        List<Restaurant> restaurantList = sharedPlaceViewModel.getRestaurantList().getValue();
        List<User> userList = sharedPlaceViewModel.getUserList().getValue();

        if (restaurantList != null && userList != null) {
            Restaurant restaurant = sharedPlaceViewModel.getRestaurantById(restaurantId);
            if (restaurant != null) {
                int likeCount = restaurant.getLikeCount();
                int userCount = userList.size();
                int rating = calculateRating(likeCount, userCount);
                listener.onRatingComputed(rating);
            } else {
                listener.onError(new Exception("Restaurant not found"));
            }
        } else {
            listener.onError(new Exception("Restaurant list or user list is not available"));
        }
    }


    // Calculates the rating based on the number of likes and the total number of users.
    private int calculateRating(int likeCount, int userCount) {
        double percentage = (double) likeCount / userCount * 100;
        if (percentage <= 10) {
            return 0;
        } else if (percentage <= 20) {
            return 1;
        } else if (percentage <= 30) {
            return 2;
        } else {
            return 3;
        }
    }
}
