package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import com.example.myapplication.service.DistanceService;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)  // SDK level can be adjusted as needed
public class DistanceServiceTest {


    @Test
    public void testCalculateDistance_zeroDistance() {
        LatLng currentLatLng = new LatLng(0.0, 0.0);
        LatLng placeLatLng = new LatLng(0.0, 0.0);

        String result = DistanceService.calculateDistance(currentLatLng, placeLatLng);

        assertEquals("0 m", result);
    }

    @Test
    public void testCalculateDistance_inMeters() {
        LatLng currentLatLng = new LatLng(0.0, 0.0);
        LatLng placeLatLng = new LatLng(0.0, 0.001); // Approximately 111 meters apart

        String result = DistanceService.calculateDistance(currentLatLng, placeLatLng);

        assertEquals("110 m", result);  // Rounded to nearest ten
    }

    @Test
    public void testCalculateDistance_inKilometers() {
        LatLng currentLatLng = new LatLng(0.0, 0.0);
        LatLng placeLatLng = new LatLng(0.0, 1.0); // Approximately 111 kilometers apart

        String result = DistanceService.calculateDistance(currentLatLng, placeLatLng);

        assertEquals("111.3 km", result);
    }
}
