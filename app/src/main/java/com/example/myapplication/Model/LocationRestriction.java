package com.example.myapplication.Model;

import com.google.gson.annotations.SerializedName;

public class LocationRestriction {
    @SerializedName("circle")
    Circle circle;

    public LocationRestriction(Circle circle) {
        this.circle = circle;
    }
}
