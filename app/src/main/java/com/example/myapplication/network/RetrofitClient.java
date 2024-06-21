package com.example.myapplication.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Retrofit instance for making network requests
    private static Retrofit retrofit;
    // Base URL for the Google Places API
    private static final String BASE_URL = "https://places.googleapis.com/";

    // Method to get the Retrofit instance
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Method to get the API service for making requests to the Google Places API
    public static GooglePlacesApi getApiService() {
        // Create and return the GooglePlacesApi service using the Retrofit instance
        return getRetrofitInstance().create(GooglePlacesApi.class);
    }
}
