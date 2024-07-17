package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import com.example.myapplication.service.DistanceService;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

public class DistanceServiceTest {

    //TODO : erreur mock setLatitude ?

    @Test
    public void testCalculateDistance_lessThan1Km() {
        // Arrange
        LatLng currentLatLng = new LatLng(48.8566, 2.3522); // Paris
        LatLng placeLatLng = new LatLng(48.8570, 2.3530); // Very close to the first location

        // Act
        String distance = DistanceService.calculateDistance(currentLatLng, placeLatLng);

        // Assert
        assertEquals("100 m", distance); // Assuming the distance between the two points is approximately 100 meters
    }

    @Test
    public void testCalculateDistance_moreThan1Km() {
        // Arrange
        LatLng currentLatLng = new LatLng(48.8566, 2.3522); // Paris
        LatLng placeLatLng = new LatLng(48.8566, 2.3622); // Approximately 1 km to the east

        // Act
        String distance = DistanceService.calculateDistance(currentLatLng, placeLatLng);

        // Assert
        assertEquals("1.0 km", distance); // Assuming the distance between the two points is approximately 1 km
    }

    @Test
    public void testCalculateDistance_farDistance() {
        // Arrange
        LatLng currentLatLng = new LatLng(48.8566, 2.3522); // Paris
        LatLng placeLatLng = new LatLng(40.7128, -74.0060); // New York

        // Act
        String distance = DistanceService.calculateDistance(currentLatLng, placeLatLng);

        // Assert
        // The distance between Paris and New York is approximately 5837 km, so we expect it to be rounded to one decimal place
        assertEquals("5837.0 km", distance);
    }

    @Test
    public void testCalculateDistance_zeroDistance() {
        // Arrange
        LatLng currentLatLng = new LatLng(48.8566, 2.3522); // Paris
        LatLng placeLatLng = new LatLng(48.8566, 2.3522); // Same location

        // Act
        String distance = DistanceService.calculateDistance(currentLatLng, placeLatLng);

        // Assert
        assertEquals("0 m", distance);
    }
}
