package com.example.myapplication;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.service.RatingService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// Unit test class for RatingService
// takes two ints (number of users and number of likes) and outputs a rating by percentage
public class RatingServiceTest {

    // Instance of the service being tested
    private RatingService ratingService;

    // Mock objects for dependencies
    @Mock
    private Restaurant mockRestaurant;

    @Mock
    private RatingService.OnRatingComputedListener mockListener;

    // Captor to capture argument values passed to mock methods
    @Captor
    private ArgumentCaptor<Integer> ratingCaptor;

    // Set up method to initialize mocks and the service instance
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ratingService = new RatingService();
    }

    // Test case for computing rating with valid data
    @Test
    public void testComputeRating_withValidData() {
        // Arrange: Set up mock data
        when(mockRestaurant.getLikeCount()).thenReturn(10);
        int userCount = 50;

        // Act: Call the method to be tested
        ratingService.computeRating(mockRestaurant, userCount, mockListener);

        // Assert: Verify that the listener is called with the correct rating
        verify(mockListener).onRatingComputed(ratingCaptor.capture());
        int capturedRating = ratingCaptor.getValue();
        assertEquals(1, capturedRating);  // Change expected value based on your logic
    }

    // Test case for computing rating with zero user count
    @Test
    public void testComputeRating_withZeroUserCount() {
        // Arrange: Set up test data
        int userCount = 0;

        // Act: Call the method to be tested
        ratingService.computeRating(mockRestaurant, userCount, mockListener);

        // Assert: Verify that onRatingComputed is not called and onError is called
        verify(mockListener, never()).onRatingComputed(anyInt());
        verify(mockListener).onError(any(Exception.class));
    }

    // Test case for computing rating with null restaurant
    @Test
    public void testComputeRating_withNullRestaurant() {
        // Arrange: Set up test data
        int userCount = 50;

        // Act: Call the method to be tested with a null restaurant
        ratingService.computeRating(null, userCount, mockListener);

        // Assert: Verify that onRatingComputed is not called and onError is called
        verify(mockListener, never()).onRatingComputed(anyInt());
        verify(mockListener).onError(any(Exception.class));
    }
}
