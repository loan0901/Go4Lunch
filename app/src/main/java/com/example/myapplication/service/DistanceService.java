package com.example.myapplication.service;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class DistanceService {

    /**
     * This method calculates the distance between two geographical locations.
     *
     * @param currentLatLng The current location's latitude and longitude.
     * @param placeLatLng The target place's latitude and longitude.
     * @return The distance between the current location and the target place as a string.
     */
    public static String calculateDistance(LatLng currentLatLng, LatLng placeLatLng) {

        // Create Location objects for the current location and the place location
        Location currentLocation = new Location("currentLocation");
        currentLocation.setLatitude(currentLatLng.latitude);
        currentLocation.setLongitude(currentLatLng.longitude);

        Location placeLocation = new Location("placeLocation");
        placeLocation.setLatitude(placeLatLng.latitude);
        placeLocation.setLongitude(placeLatLng.longitude);

        // Calculate the distance in meters between the two locations
        float distanceInMeters = currentLocation.distanceTo(placeLocation);

        // Round the distance to the nearest ten
        distanceInMeters = Math.round(distanceInMeters / 10) * 10;

        // Convert the distance to kilometers if it is 1000 meters or more
        if (distanceInMeters >= 1000) {
            float distanceInKilometers = distanceInMeters / 1000;
            return String.format("%.1f km", distanceInKilometers);
        } else {
            // Return the distance in meters
            return String.format("%.0f m", distanceInMeters);
        }
    }
}
