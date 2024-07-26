package com.example.myapplication;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Model.PlacesResponse;
import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.network.GooglePlacesApi;
import com.example.myapplication.Repository.GooglePlacesRepository.GooglePlacesCallback;
import com.example.myapplication.Repository.GooglePlacesRepository.PlaceDetailsCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Unit test class for GooglePlacesRepository, testing the loadNearbyRestaurants and getPlaceDetails methods.
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, shadows = {ShadowLog.class})
public class GooglePlacesRepositoryTest {

    // Mock dependencies for the test
    @Mock
    private GooglePlacesApi googlePlacesApi;

    @Mock
    private Call<PlacesResponse> mockPlacesCall;

    @Mock
    private Call<CustomPlace> mockPlaceDetailsCall;

    // The repository to be tested
    private GooglePlacesRepository repository;

    // Set up method to initialize mocks and the repository
    @Before
    public void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);
        // Initialize the repository with the mocked GooglePlacesApi
        repository = new GooglePlacesRepository(googlePlacesApi);
    }

    // Test the loadNearbyRestaurants method for a successful response
    @Test
    public void testLoadNearbyRestaurants_Success() {

        // Arrange: Set up test data and mock behavior
        String apiKey = "testApiKey";
        double latitude = 0.0;
        double longitude = 0.0;
        double radius = 1000.0;
        int maxResultCount = 10;
        List<String> includedTypes = new ArrayList<>();
        String fieldMask = "testFieldMask";

        // Mock response data
        List<CustomPlace> mockPlaces = new ArrayList<>();
        CustomPlace place = new CustomPlace();
        mockPlaces.add(place);

        PlacesResponse mockResponse = new PlacesResponse();
        mockResponse.places = mockPlaces;

        // Mock callback
        GooglePlacesCallback mockCallback = mock(GooglePlacesCallback.class);

        // Mock the API call and response
        when(googlePlacesApi.getNearbyPlaces(any(), anyString(), anyString())).thenReturn(mockPlacesCall);
        doAnswer(invocation -> {
            Callback<PlacesResponse> callback = invocation.getArgument(0);
            callback.onResponse(mockPlacesCall, Response.success(mockResponse));
            return null;
        }).when(mockPlacesCall).enqueue(any());

        // Act: Call the method to be tested
        repository.loadNearbyRestaurants(apiKey, latitude, longitude, radius, maxResultCount, includedTypes, fieldMask, mockCallback);

        // Assert: Verify the callback is called with the expected data
        verify(mockCallback).onNearbyRestaurantsLoaded(mockPlaces);
    }

    // Test the loadNearbyRestaurants method for a failed response
    @Test
    public void testLoadNearbyRestaurants_Failure() {
        // Arrange: Set up test data and mock behavior
        String apiKey = "testApiKey";
        double latitude = 0.0;
        double longitude = 0.0;
        double radius = 1000.0;
        int maxResultCount = 10;
        List<String> includedTypes = new ArrayList<>();
        String fieldMask = "testFieldMask";

        // Mock callback
        GooglePlacesCallback mockCallback = mock(GooglePlacesCallback.class);

        // Mock the API call and response
        when(googlePlacesApi.getNearbyPlaces(any(), anyString(), anyString())).thenReturn(mockPlacesCall);
        doAnswer(invocation -> {
            Callback<PlacesResponse> callback = invocation.getArgument(0);
            callback.onFailure(mockPlacesCall, new Throwable("Request failed"));
            return null;
        }).when(mockPlacesCall).enqueue(any());

        // Act: Call the method to be tested
        repository.loadNearbyRestaurants(apiKey, latitude, longitude, radius, maxResultCount, includedTypes, fieldMask, mockCallback);

        // Assert: Verify the callback is called with an error
        verify(mockCallback).onFailure(any(Exception.class));
    }

    // Test the getPlaceDetails method for a successful response
    @Test
    public void testGetPlaceDetails_Success() {
        // Arrange: Set up test data and mock behavior
        String placeId = "testPlaceId";
        String apiKey = "testApiKey";
        String fieldMask = "testFieldMask";

        // Mock response data
        CustomPlace mockPlaceDetails = new CustomPlace();
        // Mock callback
        PlaceDetailsCallback mockCallback = mock(PlaceDetailsCallback.class);

        // Mock the API call and response
        when(googlePlacesApi.getPlaceDetails(anyString(), anyString(), anyString())).thenReturn(mockPlaceDetailsCall);
        doAnswer(invocation -> {
            Callback<CustomPlace> callback = invocation.getArgument(0);
            callback.onResponse(mockPlaceDetailsCall, Response.success(mockPlaceDetails));
            return null;
        }).when(mockPlaceDetailsCall).enqueue(any());

        // Act: Call the method to be tested
        repository.getPlaceDetails(placeId, apiKey, fieldMask, mockCallback);

        // Assert: Verify the callback is called with the expected data
        verify(mockCallback).onPlaceDetailsLoaded(mockPlaceDetails);
    }

    // Test the getPlaceDetails method for a failed response
    @Test
    public void testGetPlaceDetails_Failure() {
        // Arrange: Set up test data and mock behavior
        String placeId = "testPlaceId";
        String apiKey = "testApiKey";
        String fieldMask = "testFieldMask";

        // Mock callback
        PlaceDetailsCallback mockCallback = mock(PlaceDetailsCallback.class);

        // Mock the API call and response
        when(googlePlacesApi.getPlaceDetails(anyString(), anyString(), anyString())).thenReturn(mockPlaceDetailsCall);
        doAnswer(invocation -> {
            Callback<CustomPlace> callback = invocation.getArgument(0);
            callback.onFailure(mockPlaceDetailsCall, new Throwable("Request failed"));
            return null;
        }).when(mockPlaceDetailsCall).enqueue(any());

        // Act: Call the method to be tested
        repository.getPlaceDetails(placeId, apiKey, fieldMask, mockCallback);

        // Assert: Verify the callback is called with an error
        verify(mockCallback).onFailure(any(Exception.class));
    }
}
