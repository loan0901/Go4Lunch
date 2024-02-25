package com.example.myapplication.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place {

    @SerializedName("internationalPhoneNumber")
    @Expose
    private String internationalPhoneNumber;
    @SerializedName("formattedAddress")
    @Expose
    private String formattedAddress;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("googleMapsUri")
    @Expose
    private String googleMapsUri;
    @SerializedName("websiteUri")
    @Expose
    private String websiteUri;
    @SerializedName("regularOpeningHours")
    @Expose
    private RegularOpeningHours regularOpeningHours;
    @SerializedName("displayName")
    @Expose
    private DisplayName displayName;

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getGoogleMapsUri() {
        return googleMapsUri;
    }

    public void setGoogleMapsUri(String googleMapsUri) {
        this.googleMapsUri = googleMapsUri;
    }

    public String getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    public RegularOpeningHours getRegularOpeningHours() {
        return regularOpeningHours;
    }

    public void setRegularOpeningHours(RegularOpeningHours regularOpeningHours) {
        this.regularOpeningHours = regularOpeningHours;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

}