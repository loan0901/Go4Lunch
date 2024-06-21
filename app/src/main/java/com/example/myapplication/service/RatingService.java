package com.example.myapplication.service;

import com.example.myapplication.Model.Restaurant;

public class RatingService {

    // Interface for a listener to handle the computed rating.
    public interface OnRatingComputedListener {
        void onRatingComputed(int rating);
        void onError(Exception e);
    }

    /**
     * Computes the rating of a restaurant based on the number of likes and total users.
     *
     * @param restaurant The restaurant whose rating is to be computed.
     * @param userCount The total number of users.
     * @param listener The listener to handle the computed rating or any error.
     */
    public void computeRating(Restaurant restaurant, int userCount, OnRatingComputedListener listener) {
        // Check if the restaurant is not null and the user count is not zero.
        if (restaurant != null && userCount != 0) {
            int likeCount = restaurant.getLikeCount();
            int rating = calculateRating(likeCount, userCount);
            listener.onRatingComputed(rating);
        } else {
            // Return an error through the listener if the restaurant is null or user count is zero.
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
