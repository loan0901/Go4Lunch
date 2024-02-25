package com.example.myapplication;

import com.example.myapplication.Model.SearchRequestModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PlacesService {

    @Headers({
            "Content-Type: application/json",
            "X-Goog-Api-Key: AIzaSyAKXaQzBrV6EIHVBliBVVYjUsreEkiY7uc",
            "X-Goog-FieldMask: places.displayName,places.location,places.internationalPhoneNumber,places.googleMapsUri,places.websiteUri,places.formattedAddress,places.rating,places.regularOpeningHours.weekdayDescriptions"
    })
    @POST("v1/places:searchNearby")
    Call<Void> searchNearby(@Body SearchRequestModel request);
}
