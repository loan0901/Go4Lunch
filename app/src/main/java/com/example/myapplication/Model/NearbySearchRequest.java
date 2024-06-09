package com.example.myapplication.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbySearchRequest {
    @SerializedName("includedTypes")
    List<String> includedTypes;
    @SerializedName("maxResultCount")
    int maxResultCount;
    @SerializedName("locationRestriction")
    LocationRestriction locationRestriction;

    public NearbySearchRequest(List<String> includedTypes, int maxResultCount, LocationRestriction locationRestriction) {
        this.includedTypes = includedTypes;
        this.maxResultCount = maxResultCount;
        this.locationRestriction = locationRestriction;
    }
}

