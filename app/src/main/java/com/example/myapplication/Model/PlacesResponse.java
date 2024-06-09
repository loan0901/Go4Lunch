package com.example.myapplication.Model;

import com.google.android.libraries.places.api.model.Place;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesResponse {
    @SerializedName("places")
    public List<CustomPlace> places;
}
