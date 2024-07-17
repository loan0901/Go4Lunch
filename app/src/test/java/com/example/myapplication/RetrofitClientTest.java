package com.example.myapplication;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.myapplication.network.GooglePlacesApi;
import com.example.myapplication.network.RetrofitClient;

import org.junit.Test;

import retrofit2.Retrofit;

// This class contains tests for the RetrofitClient class
public class RetrofitClientTest {

    // Test the getRetrofitInstance method
    @Test
    public void testGetRetrofitInstance() {
        // Call the method to get the Retrofit instance
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();

        // Assert that the Retrofit instance is not null
        assertNotNull(retrofit);

        // Assert that the base URL of the Retrofit instance matches the expected URL
        assertEquals(RetrofitClient.BASE_URL, retrofit.baseUrl().toString());
    }

    // Test the getApiService method
    @Test
    public void testGetApiService() {
        // Call the method to get the GooglePlacesApi service
        GooglePlacesApi apiService = RetrofitClient.getApiService();

        // Assert that the API service is not null
        assertNotNull(apiService);
    }
}
