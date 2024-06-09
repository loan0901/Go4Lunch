package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class SharedPlaceViewModel extends ViewModel {

    private final MutableLiveData<List<CustomPlace>> places = new MutableLiveData<>();
    private final MutableLiveData<LatLng> currentUserLatLng = new MutableLiveData<>();
    private final MutableLiveData<CustomPlace> selectedPlace = new MutableLiveData<>();
    private final MutableLiveData<List<User>> userList = new MutableLiveData<>();
    private final MutableLiveData<List<Restaurant>> restaurantList = new MutableLiveData<>();
    private final MutableLiveData<String> selectedRestaurantName = new MutableLiveData<>();
    private final MutableLiveData<List<CustomPlace>> filteredPlaces = new MutableLiveData<>();
    private final MutableLiveData<List<User>> filteredUser = new MutableLiveData<>();

    private final MediatorLiveData<List<CustomPlace>> combinedPlaces = new MediatorLiveData<>();

    public SharedPlaceViewModel() {
        combinedPlaces.addSource(places, customPlaces -> combinedPlaces.setValue(customPlaces));
        combinedPlaces.addSource(restaurantList, restaurants -> combinedPlaces.setValue(places.getValue()));
    }

    public LiveData<List<CustomPlace>> getCombinedPlaces() {
        return combinedPlaces;
    }

    public MutableLiveData<List<CustomPlace>> getFilteredPlaces() {return filteredPlaces;}

    public void setPlaces(List<CustomPlace> places) {this.places.setValue(places);}

    public LiveData<List<CustomPlace>> getPlaces() {return places;}

    public void setCurrentUserLatLng(LatLng latLng) {this.currentUserLatLng.setValue(latLng);}

    public LiveData<LatLng> getCurrentUserLatLng() {return currentUserLatLng;}

    public LiveData<CustomPlace> getSelectedPlace() {return selectedPlace;}

    public void setSelectedPlace(CustomPlace place) {this.selectedPlace.setValue(place);}

    public void setUserList(List<User> userList) {this.userList.setValue(userList);}

    public LiveData<List<User>> getUserList() {return userList;}

    public LiveData<List<Restaurant>> getRestaurantList() {return restaurantList;}

    public void setRestaurantList(List<Restaurant> restaurants) {this.restaurantList.setValue(restaurants);}

    public void setSelectedRestaurantName(String name) {selectedRestaurantName.setValue(name);}

    public LiveData<String> getSelectedRestaurantName() {return selectedRestaurantName;}

    public void setFilteredUser(List<User> filteredUsers) {this.filteredUser.setValue(filteredUsers);}

    public LiveData<List<User>> getFilteredUsers() {return filteredUser;}


    // Method to set the selected place by its ID
    public void setPlaceForDetailFragmentById(String placeId) {
        List<CustomPlace> placeList = places.getValue();
        if (placeList != null) {
            for (CustomPlace place : placeList) {
                if (place.placeId.equals(placeId)) {
                    setSelectedPlace(place);  // Set the selected place
                    return;
                }
            }
        }
        setSelectedPlace(null); // If the placeId is not found, reset selectedPlace to null
    }

    // Method to get users by selected restaurant ID
    public List<User> getUsersBySelectedRestaurantId(String restaurantId) {
        List<User> allUsers = userList.getValue();
        List<User> filteredUsers = new ArrayList<>();
        if (allUsers != null) {
            for (User user : allUsers) {
                if (restaurantId.equals(user.getSelectedRestaurantId())) {
                    filteredUsers.add(user);
                }
            }
        }
        return filteredUsers;
    }

    // Method to get all custom place names
    public List<String> getAllCustomPlaceName(){
        List<CustomPlace> placeList = places.getValue();
        List<String> placesNameList = new ArrayList<>();
        if (placeList != null) {
            for (CustomPlace place : placeList) {
                placesNameList.add(place.displayName.value);
            }
        }
        return placesNameList;
    }

    // Method to filter places based on a current searchBar text
    public void filterPlaces(String query) {
        List<CustomPlace> allPlaces = places.getValue();
        if (allPlaces != null) {
            List<CustomPlace> filteredList = new ArrayList<>();
            for (CustomPlace place : allPlaces) {
                if (place.displayName.value.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(place);
                }
            }
            filteredPlaces.setValue(filteredList);
        }
    }

    // Method to get users by restaurant name
    public void getUsersByRestaurantName(String restaurantName) {
        List<User> allUsers = userList.getValue();
        List<User> filteredUsers = new ArrayList<>();
        if (allUsers != null) {
            if (restaurantName.isEmpty()) {
                filteredUsers.addAll(allUsers);
            } else {
                for (User user : allUsers) {
                    if (user.getSelectedRestaurantName() != null && user.getSelectedRestaurantName().toLowerCase().contains(restaurantName.toLowerCase())) {
                        filteredUsers.add(user);
                    }
                }
            }
        }
        setFilteredUser(filteredUsers);
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

    // Method to get a user by their ID
    public User getUserById(String userUid){
        List<User> allUsers = userList.getValue();
        if (allUsers != null){
            for (User user : allUsers){
                if (user.getUserId().equals(userUid)){
                    return user;
                }
            }
        }
        return null; //user not found
    }
}
