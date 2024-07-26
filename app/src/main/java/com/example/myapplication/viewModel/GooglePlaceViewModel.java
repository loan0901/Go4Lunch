package com.example.myapplication.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.Repository.FirestoreRepositoryInterface;
import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class GooglePlaceViewModel extends ViewModel {

    private final GooglePlacesRepository googlePlacesRepository;

    private final MutableLiveData<List<CustomPlace>> places = new MutableLiveData<>();
    private final MutableLiveData<CustomPlace> selectedPlace = new MutableLiveData<>();
    private final MutableLiveData<List<CustomPlace>> filteredPlaces = new MutableLiveData<>();

    public GooglePlaceViewModel(GooglePlacesRepository googlePlacesRepository) {
        this.googlePlacesRepository = googlePlacesRepository;
    }

    public LiveData<List<CustomPlace>> getFilteredPlaces() {return filteredPlaces;}

    public void setPlaces(List<CustomPlace> places) {this.places.setValue(places);}

    public LiveData<List<CustomPlace>> getPlaces() {return places;}

    public LiveData<CustomPlace> getSelectedPlace() {return selectedPlace;}

    public void setSelectedPlace(CustomPlace place) {this.selectedPlace.setValue(place);}


    // Load nearby restaurants and add them to the View Model
    public void loadNearbyPlaces(String apiKey, double latitude, double longitude, double radius, int maxResultCount, List<String> includedTypes, String fieldMask, FirestoreRepositoryInterface firestoreRepository) {
        // get restaurants with only the information that is necessary
        googlePlacesRepository.loadNearbyRestaurants(apiKey, latitude, longitude, radius, maxResultCount, includedTypes, fieldMask, new GooglePlacesRepository.GooglePlacesCallback() {
            @Override
            public void onNearbyRestaurantsLoaded(List<CustomPlace> nearbyRestaurants) {
                // add all the CustomPlace to the ViewModel
                setPlaces(nearbyRestaurants);
                if (nearbyRestaurants != null) {
                    for (CustomPlace place : nearbyRestaurants) {
                        //create a restaurant in the database if it does not exist
                        firestoreRepository.checkOrCreateRestaurant(place.placeId, 0, null);
                    }
                }
            }

            @Override
            public void onSuccess() {
                // No-op
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ViewModel", "Error loading nearby restaurants", e);
            }
        });
    }

    // Interface for the callback
    public interface PlaceDetailsCallback {
        void onPlaceDetailsLoaded(CustomPlace placeDetails);
        void onFailure(Exception e);
    }

    // load a particular restaurant via its ID
    public void getPlaceDetails(String placeId, String apiKey, String fieldMask,  PlaceDetailsCallback callback) {
        googlePlacesRepository.getPlaceDetails(placeId, apiKey, fieldMask, new GooglePlacesRepository.PlaceDetailsCallback() {
            @Override
            public void onPlaceDetailsLoaded(CustomPlace placeDetails) {
                // Retrieve the current list of places
                List<CustomPlace> currentPlaces = places.getValue();
                if (currentPlaces == null) {
                    currentPlaces = new ArrayList<>();
                }
                // Add the new place details to the list
                currentPlaces.add(placeDetails);
                // Update the LiveData with the new list
                places.setValue(currentPlaces);
                // Call the callback
                callback.onPlaceDetailsLoaded(placeDetails);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
                Log.e("ViewModel", "Error getting place details", e);
            }
        });
    }

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

    // Methode to check if PlaceList contain a certain restaurant by Id
    public boolean checkIfPlaceListContainsRestaurant(String restaurantId) {
        List<CustomPlace> placeList = places.getValue();
        if (placeList != null) {
            for (CustomPlace place : placeList) {
                if (place.placeId.equals(restaurantId)) {
                    return true;
                }
            }
        }
        return false;
    }


}
