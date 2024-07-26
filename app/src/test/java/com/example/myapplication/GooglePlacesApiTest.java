package com.example.myapplication;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.myapplication.Model.Circle;
import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Model.LocationRestriction;
import com.example.myapplication.Model.NearbySearchRequest;
import com.example.myapplication.Model.PlacesResponse;
import com.example.myapplication.network.GooglePlacesApi;
import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GooglePlacesApiTest {

    private MockWebServer mockWebServer;
    private GooglePlacesApi apiService;

    // Set up MockWebServer and Retrofit before each test
    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(GooglePlacesApi.class);
    }

    // Shut down MockWebServer after each test
    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    // Test the getNearbyPlaces method of the GooglePlacesApi interface
    @Test
    public void testGetNearbyPlaces() throws Exception {
        MockResponse response = new MockResponse()
                .setBody("{\"places\":[]}")
                .addHeader("Content-Type", "application/json");
        mockWebServer.enqueue(response);

        // Set up the request parameters
        List<String> includedTypes = Collections.singletonList("restaurant");
        LatLng center = new LatLng( 49, 1.85);
        double radius = 500.0;
        Circle circle = new Circle(center, radius);
        LocationRestriction locationRestriction = new LocationRestriction(circle);

        // Make the API call
        Call<PlacesResponse> call = apiService.getNearbyPlaces(
                new NearbySearchRequest(includedTypes, 5, locationRestriction),
                "fakeApiKey",
                "name,rating"
        );

        // Execute the call and assert the response
        Response<PlacesResponse> responseCall = call.execute();
        assertTrue(responseCall.isSuccessful());
        assertNotNull(responseCall.body());
        assertEquals(0, responseCall.body().places.size());

        // Verify the request details
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/v1/places:searchNearby?key=fakeApiKey", request.getPath());
        assertEquals("POST", request.getMethod());
    }

    // Test the getPlaceDetails method of the GooglePlacesApi interface
    @Test
    public void testGetPlaceDetails() throws Exception {
        // Create a mock response for the API call
        MockResponse response = new MockResponse()
                .setBody("{\"displayName\":{\"text\":\"Test Place\"}}")
                .addHeader("Content-Type", "application/json");
        mockWebServer.enqueue(response);

        // Make the API call
        Call<CustomPlace> call = apiService.getPlaceDetails(
                "place_id",
                "fakeApiKey",
                "name,rating"
        );

        // Execute the call and assert the response
        Response<CustomPlace> responseCall = call.execute();
        assertTrue(responseCall.isSuccessful());
        assertNotNull(responseCall.body());
        assertEquals("Test Place", responseCall.body().displayName.value);

        // Verify the request details
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/v1/places/place_id?key=fakeApiKey", request.getPath());
        assertEquals("GET", request.getMethod());
    }
}
