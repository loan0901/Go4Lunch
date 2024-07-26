package com.example.myapplication.Repository;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;

import java.util.ArrayList;
import java.util.List;

public class MockFirestoreRepository implements FirestoreRepositoryInterface{

    private List<User> mockUsers = new ArrayList<>();
    private List<Restaurant> mockRestaurants = new ArrayList<>();
    private boolean listenerRegistered = false;

    @Override
    public void checkAndCreateUser(String uid, String userName, String selectedRestaurantId, String selectedRestaurantName, List<String> favoriteRestaurants, String photoUrl) {
        for (User user : mockUsers) {
            if (user.getUserId().equals(uid)) {
                return;
            }
        }
        User newUser = new User(uid, favoriteRestaurants, selectedRestaurantId, selectedRestaurantName, photoUrl, userName);
        mockUsers.add(newUser);
    }

    @Override
    public void getAllUsers(OnUsersRetrievedListener listener) {
        listener.onUsersRetrieved(new ArrayList<>(mockUsers));
    }

    @Override
    public void listenForUsersUpdates(OnUsersRetrievedListener listener) {
        if (!listenerRegistered) {
            listenerRegistered = true;
            listener.onUsersRetrieved(new ArrayList<>(mockUsers));
        }
    }

    @Override
    public void removeUsersListener() {
        listenerRegistered = false;
    }

    @Override
    public void checkOrCreateRestaurant(String restaurantId, int likeCount, List<String> userIdSelected) {
        for (Restaurant restaurant : mockRestaurants) {
            if (restaurant.getRestaurantId().equals(restaurantId)) {
                return;
            }
        }
        Restaurant newRestaurant = new Restaurant(restaurantId, likeCount, userIdSelected);
        mockRestaurants.add(newRestaurant);
    }

    @Override
    public void getAllRestaurants(OnRestaurantsRetrievedListener listener) {
        listener.onRestaurantsRetrieved(new ArrayList<>(mockRestaurants));
    }

    @Override
    public void listenForRestaurantUpdates(OnRestaurantsRetrievedListener listener) {
        if (!listenerRegistered) {
            listenerRegistered = true;
            listener.onRestaurantsRetrieved(new ArrayList<>(mockRestaurants));
        }
    }

    @Override
    public void removeRestaurantListener() {
        listenerRegistered = false;
    }

    @Override
    public void toggleFavoriteRestaurant(String uid, String restaurantId) {
        for (User user : mockUsers) {
            if (user.getUserId().equals(uid)) {
                List<String> favoriteRestaurants = user.getFavoriteRestaurants();
                if (favoriteRestaurants.contains(restaurantId)) {
                    favoriteRestaurants.remove(restaurantId);
                } else {
                    favoriteRestaurants.add(restaurantId);
                }
                user.setFavoriteRestaurants(favoriteRestaurants);
                return;
            }
        }
    }

    @Override
    public void updateSelectedRestaurant(String uid, String newRestaurantId, String restaurantName) {
        for (User user : mockUsers) {
            if (user.getUserId().equals(uid)) {
                String oldRestaurantId = user.getSelectedRestaurantId();
                user.setSelectedRestaurantId(newRestaurantId);
                user.setSelectedRestaurantName(restaurantName);

                for (Restaurant restaurant : mockRestaurants) {
                    if (restaurant.getRestaurantId().equals(oldRestaurantId)) {
                        restaurant.getUserIdSelected().remove(uid);
                    }
                    if (restaurant.getRestaurantId().equals(newRestaurantId)) {
                        restaurant.getUserIdSelected().add(uid);
                    }
                }
                return;
            }
        }
    }
}
