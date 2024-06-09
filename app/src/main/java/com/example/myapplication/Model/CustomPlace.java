package com.example.myapplication.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomPlace {
    @SerializedName("id")
    public String placeId;
    @SerializedName("displayName")
    public DisplayName displayName;
    @SerializedName("formattedAddress")
    public
    String address;
    @SerializedName("location")
    public
    LatLng location;
    @SerializedName("rating")
    public
    float rating;
    @SerializedName("regularOpeningHours")
    public
    CustomOpeningHours openingHours;
    @SerializedName("photos")
    public
    List<CustomPhotoMetadata> photoMetadatas;
    @SerializedName("internationalPhoneNumber")
    public
    String phoneNumber;
    @SerializedName("websiteUri")
    public
    String websiteUri;

    public static class DisplayName {
        @SerializedName("text")
        public String value;
        @SerializedName("languageCode")
        String language;
    }
}
