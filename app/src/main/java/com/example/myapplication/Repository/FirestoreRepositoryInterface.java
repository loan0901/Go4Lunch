package com.example.myapplication.Repository;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;

import java.util.List;

// Interface of FirestoreRepository for ease of Unit testing.
public interface FirestoreRepositoryInterface {
    void checkAndCreateUser(String uid, String userName, String selectedRestaurantId, String selectedRestaurantName, List<String> favoriteRestaurants, String photoUrl);
    void getAllUsers(FirestoreRepository.OnUsersRetrievedListener listener);
    void listenForUsersUpdates(FirestoreRepository.OnUsersRetrievedListener listener);
    void removeUsersListener();
    void checkOrCreateRestaurant(String restaurantId, int likeCount, List<String> userIdSelected);
    void getAllRestaurants(FirestoreRepository.OnRestaurantsRetrievedListener listener);
    void listenForRestaurantUpdates(FirestoreRepository.OnRestaurantsRetrievedListener listener);
    void removeRestaurantListener();
    void toggleFavoriteRestaurant(String uid, String restaurantId);
    void updateSelectedRestaurant(String uid, String newRestaurantId, String restaurantName);

    interface OnUsersRetrievedListener {
        void onUsersRetrieved(List<User> userList);
        void onError(Exception e);
    }

    interface OnRestaurantsRetrievedListener {
        void onRestaurantsRetrieved(List<Restaurant> restaurantList);
        void onError(Exception e);
    }
}
