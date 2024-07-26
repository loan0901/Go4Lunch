package com.example.myapplication.Repository;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;

import java.util.ArrayList;
import java.util.List;

// This class simulates the FirestoreRepository for testing purposes.
public class MockFirestoreRepository implements FirestoreRepositoryInterface{

    private List<User> mockUsers = new ArrayList<>(); // List to store mock users
    private List<Restaurant> mockRestaurants = new ArrayList<>(); // List to store mock restaurants
    private boolean listenerRegistered = false; // Flag to track if listener is registered

    // Method to check if a user exists and create a new user if not.
    @Override
    public void checkAndCreateUser(String uid, String userName, String selectedRestaurantId, String selectedRestaurantName, List<String> favoriteRestaurants, String photoUrl) {
        for (User user : mockUsers) {
            if (user.getUserId().equals(uid)) {
                return; // User already exists, return early
            }
        }
        // Create a new user and add to the mock user list
        User newUser = new User(uid, favoriteRestaurants, selectedRestaurantId, selectedRestaurantName, photoUrl, userName);
        mockUsers.add(newUser);
    }

    // Method to retrieve all users and pass them to the provided listener.
    @Override
    public void getAllUsers(OnUsersRetrievedListener listener) {
        listener.onUsersRetrieved(new ArrayList<>(mockUsers)); // Pass a copy of the mock user list to the listener
    }

    // Method to listen for user updates and pass current users to the provided listener.
    @Override
    public void listenForUsersUpdates(OnUsersRetrievedListener listener) {
        if (!listenerRegistered) {
            listenerRegistered = true; // Register the listener
            listener.onUsersRetrieved(new ArrayList<>(mockUsers)); // Pass a copy of the mock user list to the listener
        }
    }

    // Method to remove the user updates listener.
    @Override
    public void removeUsersListener() {
        listenerRegistered = false; // Unregister the listener
    }

    // Method to check if a restaurant exists and create a new restaurant if not.
    @Override
    public void checkOrCreateRestaurant(String restaurantId, int likeCount, List<String> userIdSelected) {
        for (Restaurant restaurant : mockRestaurants) {
            if (restaurant.getRestaurantId().equals(restaurantId)) {
                return; // Restaurant already exists, return early
            }
        }
        // Create a new restaurant and add to the mock restaurant list
        Restaurant newRestaurant = new Restaurant(restaurantId, likeCount, userIdSelected);
        mockRestaurants.add(newRestaurant);
    }

    // Method to retrieve all restaurants and pass them to the provided listener.
    @Override
    public void getAllRestaurants(OnRestaurantsRetrievedListener listener) {
        listener.onRestaurantsRetrieved(new ArrayList<>(mockRestaurants));
    }

    // Method to listen for restaurant updates and pass current restaurants to the provided listener.
    @Override
    public void listenForRestaurantUpdates(OnRestaurantsRetrievedListener listener) {
        if (!listenerRegistered) {
            listenerRegistered = true; // Register the listener
            listener.onRestaurantsRetrieved(new ArrayList<>(mockRestaurants));
        }
    }

    // Method to remove the restaurant updates listener.
    @Override
    public void removeRestaurantListener() {
        listenerRegistered = false; // Unregister the listener
    }

    // Method to toggle the favorite status of a restaurant for a specific user.
    @Override
    public void toggleFavoriteRestaurant(String uid, String restaurantId) {
        for (User user : mockUsers) {
            if (user.getUserId().equals(uid)) {
                List<String> favoriteRestaurants = user.getFavoriteRestaurants();
                if (favoriteRestaurants.contains(restaurantId)) {
                    favoriteRestaurants.remove(restaurantId);  // Remove from favorites if already present
                } else {
                    favoriteRestaurants.add(restaurantId); // Add to favorites if not present
                }
                user.setFavoriteRestaurants(favoriteRestaurants);
                return;
            }
        }
    }

    // Method to update the selected restaurant for a specific user.
    @Override
    public void updateSelectedRestaurant(String uid, String newRestaurantId, String restaurantName) {
        for (User user : mockUsers) {
            if (user.getUserId().equals(uid)) {
                String oldRestaurantId = user.getSelectedRestaurantId();
                user.setSelectedRestaurantId(newRestaurantId);
                user.setSelectedRestaurantName(restaurantName);

                // Update the user selections in the mock restaurant list
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
