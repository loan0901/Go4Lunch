package com.example.myapplication.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Circle {
    @SerializedName("center")
    LatLng center;
    @SerializedName("radius")
    double radius;

    public Circle(LatLng center, double radius) {
        this.center = center;
        this.radius = radius;
    }
}
