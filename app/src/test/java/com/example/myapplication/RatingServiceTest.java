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

public class RatingServiceTest {

    private RatingService ratingService;

    @Mock
    private Restaurant mockRestaurant;

    @Mock
    private RatingService.OnRatingComputedListener mockListener;

    @Captor
    private ArgumentCaptor<Integer> ratingCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ratingService = new RatingService();
    }

    @Test
    public void testComputeRating_withValidData() {
        // Arrange
        when(mockRestaurant.getLikeCount()).thenReturn(10);
        int userCount = 50;

        // Act
        ratingService.computeRating(mockRestaurant, userCount, mockListener);

        // Assert
        verify(mockListener).onRatingComputed(ratingCaptor.capture());
        int capturedRating = ratingCaptor.getValue();
        assertEquals(1, capturedRating);  // Change expected value based on your logic
    }

    @Test
    public void testComputeRating_withZeroUserCount() {
        // Arrange
        int userCount = 0;

        // Act
        ratingService.computeRating(mockRestaurant, userCount, mockListener);

        // Assert
        verify(mockListener, never()).onRatingComputed(anyInt());
        verify(mockListener).onError(any(Exception.class));
    }

    @Test
    public void testComputeRating_withNullRestaurant() {
        // Arrange
        int userCount = 50;

        // Act
        ratingService.computeRating(null, userCount, mockListener);

        // Assert
        verify(mockListener, never()).onRatingComputed(anyInt());
        verify(mockListener).onError(any(Exception.class));
    }
}
