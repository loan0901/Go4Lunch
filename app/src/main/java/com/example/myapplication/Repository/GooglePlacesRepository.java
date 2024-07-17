package com.example.myapplication.Repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.Model.Circle;
import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Model.LocationRestriction;
import com.example.myapplication.Model.NearbySearchRequest;
import com.example.myapplication.Model.PlacesResponse;
import com.example.myapplication.network.GooglePlacesApi;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlacesRepository {

    private final GooglePlacesApi googlePlacesApi;

    public GooglePlacesRepository(GooglePlacesApi googlePlacesApi) {
        this.googlePlacesApi = googlePlacesApi;
    }

    /**
     * Loads nearby restaurants using the Google Places API.
     *
     * @param apiKey The API key for authenticating the request.
     * @param latitude The latitude of the user's location.
     * @param longitude The longitude of the user's location.
     * @param radius The radius (in meters) within which to search for places.
     * @param maxResultCount The maximum number of results to return.
     * @param includedTypes A list of place types to include in the search.
     * @param fieldMask The fields to include in the API response.
     * @param callback The callback to handle the API response.
     */
    public void loadNearbyRestaurants(String apiKey, double latitude, double longitude, double radius, int maxResultCount, List<String> includedTypes, String fieldMask, GooglePlacesCallback callback) {
        LatLng center = new LatLng(latitude, longitude);
        Circle circle = new Circle(center, radius);
        LocationRestriction locationRestriction = new LocationRestriction(circle);
        NearbySearchRequest request = new NearbySearchRequest(includedTypes, maxResultCount, locationRestriction);

        // Make the API call to get nearby places
        Call<PlacesResponse> call = googlePlacesApi.getNearbyPlaces(request, apiKey, fieldMask);
        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacesResponse> call, @NonNull Response<PlacesResponse> response) {
                if (response.isSuccessful()) {
                    // If the response is successful, extract the list of places
                    PlacesResponse placesResponse = response.body();
                    Log.d("GooglePlacesRepository", "Received response: " + placesResponse);
                    List<CustomPlace> nearbyRestaurants = placesResponse.places;

                    // Pass the list of places to the callback
                    callback.onNearbyRestaurantsLoaded(nearbyRestaurants);
                } else {
                    // If the response is not successful, log an error and pass the exception to the callback
                    Log.e("GooglePlacesRepository", "Request failed with status code: " + response.code());
                    callback.onFailure(new Exception("Request failed with status code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                // If the call fails, log the exception and pass it to the callback
                Log.e("GooglePlacesRepository", "Request failed with exception: " + t.getMessage());
                callback.onFailure(new Exception(t));
            }
        });
    }

    /**
     * Retrieves detailed information about a specific place using the Google Places API.
     *
     * @param placeId The ID of the place to retrieve details for.
     * @param apiKey The API key for authenticating the request.
     * @param fieldMask The fields to include in the API response.
     * @param callback The callback to handle the API response.
     */
    public void getPlaceDetails(String placeId, String apiKey, String fieldMask, PlaceDetailsCallback callback) {
        // Make the API call to get the details of a specific place
        Call<CustomPlace> call = googlePlacesApi.getPlaceDetails(placeId, apiKey, fieldMask);
        call.enqueue(new Callback<CustomPlace>() {
            @Override
            public void onResponse(Call<CustomPlace> call, Response<CustomPlace> response) {
                if (response.isSuccessful()) {
                    // If the response is successful, extract the place details
                    CustomPlace placeDetails = response.body();
                    // Pass the place details to the callback
                    callback.onPlaceDetailsLoaded(placeDetails);
                } else {
                    // If the response is not successful, try to read the error message
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        callback.onFailure(new Exception("Request failed with status code: " + response.code() + " and message: " + errorMessage));
                    } catch (IOException e) {
                        callback.onFailure(new Exception("Request failed with status code: " + response.code() + " and error reading error body: " + e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomPlace> call, Throwable t) {
                // If the call fails, pass the exception to the callback
                callback.onFailure(new Exception(t));
            }
        });
    }

    // Callback interface for handling the results of loading nearby restaurants
    public interface GooglePlacesCallback {
        void onNearbyRestaurantsLoaded(List<CustomPlace> nearbyRestaurants);
        void onSuccess();
        void onFailure(Exception e);
    }

    // Callback interface for handling the results of retrieving place details
    public interface PlaceDetailsCallback {
        void onPlaceDetailsLoaded(CustomPlace placeDetails);
        void onFailure(Exception e);
    }
}
