package com.example.myapplication.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Repository.FirestoreRepository;

import java.util.List;

public class RestaurantViewModel extends ViewModel {

    private final FirestoreRepository firestoreRepository;

    private final MutableLiveData<List<Restaurant>> restaurantList = new MutableLiveData<>();
    private final MutableLiveData<String> selectedRestaurantName = new MutableLiveData<>();

    public RestaurantViewModel(FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    public FirestoreRepository getFirestoreRepository() {
        return firestoreRepository;
    }

    public LiveData<List<Restaurant>> getRestaurantList() {return restaurantList;}

    public void setRestaurantList(List<Restaurant> restaurants) {this.restaurantList.setValue(restaurants);}

    public void setSelectedRestaurantName(String name) {selectedRestaurantName.setValue(name);}

    public LiveData<String> getSelectedRestaurantName() {return selectedRestaurantName;}


    // Toggle favorite restaurant
    public void toggleFavoriteRestaurant(String uid, String restaurantId) {
        firestoreRepository.toggleFavoriteRestaurant(uid, restaurantId);
    }

    // Update selected restaurant
    public void updateSelectedRestaurant(String uid, String newRestaurantId, String restaurantName) {
        firestoreRepository.updateSelectedRestaurant(uid, newRestaurantId, restaurantName);
    }

    public void updateRestaurantList() {
        // Retrieve all restaurants from Firestore
        firestoreRepository.getAllRestaurants(new FirestoreRepository.OnRestaurantsRetrievedListener() {
            @Override
            public void onRestaurantsRetrieved(List<Restaurant> restaurantList) {
                // Update the restaurant list in the ViewModel
                setRestaurantList(restaurantList);
                // Set up listener for real-time updates
                listenForRestaurantUpdates();
            }

            @Override
            public void onError(Exception e) {
                // Set up listener for real-time updates even in case of initial error
                listenForRestaurantUpdates();
                Log.w("Firestore", "Error getting restaurants.", e);
            }
        });
    }

    public void listenForRestaurantUpdates() {
        // Listen for real-time updates to the restaurant list
        firestoreRepository.listenForRestaurantUpdates(new FirestoreRepository.OnRestaurantsRetrievedListener() {
            @Override
            public void onRestaurantsRetrieved(List<Restaurant> restaurantList) {
                // Update the restaurant list in the ViewModel
                setRestaurantList(restaurantList);
            }

            @Override
            public void onError(Exception e) {
                Log.w("Firestore", "Error getting restaurants.", e);
            }
        });
    }

    public void removeListeners() {
        firestoreRepository.removeRestaurantListener();
    }

    // Method to get a restaurant by its ID
    public Restaurant getRestaurantById(String restaurantId) {
        List<Restaurant> restaurants = restaurantList.getValue();
        if (restaurants != null) {
            for (Restaurant restaurant : restaurants) {
                if (restaurant.getRestaurantId().equals(restaurantId)) {
                    return restaurant;
                }
            }
        }
        return null; // Restaurant not found
    }

}
