package com.example.myapplication;

import com.example.myapplication.Model.NearbySearchRequest;
import com.example.myapplication.Model.PlacesResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GooglePlacesApi {
    @POST("v1/places:searchNearby")
    Call<PlacesResponse> getNearbyPlaces(
            @Body NearbySearchRequest request,
            @Query("key") String apiKey,
            @Header("X-Goog-FieldMask") String fieldMask
    );
}
