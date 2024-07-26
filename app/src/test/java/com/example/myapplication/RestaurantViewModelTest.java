package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.viewModel.RestaurantViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

// Unit test class for RestaurantViewModel, testing various scenarios for restaurant operations.
@RunWith(MockitoJUnitRunner.class)
public class RestaurantViewModelTest {

    // Mock object for FirestoreRepository
    @Mock
    private FirestoreRepository firestoreRepository;

    // Inject mocks into RestaurantViewModel
    @InjectMocks
    private RestaurantViewModel restaurantViewModel;

    // Mock observers for LiveData
    @Mock
    private Observer<List<Restaurant>> restaurantListObserver;

    @Mock
    private Observer<String> selectedRestaurantNameObserver;

    // Rule to allow LiveData to execute synchronously
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    // Set up method to initialize mocks and the view model
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantViewModel = new RestaurantViewModel(firestoreRepository);
        restaurantViewModel.getRestaurantList().observeForever(restaurantListObserver);
        restaurantViewModel.getSelectedRestaurantName().observeForever(selectedRestaurantNameObserver);
    }

    // Test case for toggling favorite restaurant
    @Test
    public void testToggleFavoriteRestaurant() {
        // Arrange: Set up test data
        String uid = "user123";
        String restaurantId = "restaurant123";

        // Act: Call the method to be tested
        restaurantViewModel.toggleFavoriteRestaurant(uid, restaurantId);

        // Assert: Verify that the repository method is called with correct parameters
        verify(firestoreRepository).toggleFavoriteRestaurant(uid, restaurantId);
    }

    // Test case for updating selected restaurant
    @Test
    public void testUpdateSelectedRestaurant() {
        // Arrange: Set up test data
        String uid = "user123";
        String newRestaurantId = "restaurant123";
        String restaurantName = "Restaurant Name";

        // Act: Call the method to be tested
        restaurantViewModel.updateSelectedRestaurant(uid, newRestaurantId, restaurantName);

        // Assert: Verify that the repository method is called with correct parameters
        verify(firestoreRepository).updateSelectedRestaurant(uid, newRestaurantId, restaurantName);
    }

    // Test case for updating the restaurant list
    @Test
    public void testUpdateRestaurantList() {
        // Arrange: Set up mock data
        List<Restaurant> mockRestaurants = Arrays.asList(new Restaurant(), new Restaurant());

        // Simulate the callback when the repository calls back
        doAnswer(invocation -> {
            FirestoreRepository.OnRestaurantsRetrievedListener callback = invocation.getArgument(0);
            callback.onRestaurantsRetrieved(mockRestaurants);
            return null;
        }).when(firestoreRepository).getAllRestaurants(any(FirestoreRepository.OnRestaurantsRetrievedListener.class));

        // Act: Call the method to be tested
        restaurantViewModel.updateRestaurantList();

        // Assert: Verify that the observer is called with the correct data
        verify(restaurantListObserver).onChanged(mockRestaurants);
    }

    // Test case for getting a restaurant by ID
    @Test
    public void testGetRestaurantById() {
        // Arrange: Set up test data
        Restaurant restaurant1 = new Restaurant();
        Restaurant restaurant2 = new Restaurant();
        restaurant1.setRestaurantId("1");
        restaurant2.setRestaurantId("2");
        List<Restaurant> mockRestaurants = Arrays.asList(restaurant1, restaurant2);

        // Act: Set the restaurant list in the view model
        restaurantViewModel.setRestaurantList(mockRestaurants);

        // Assert: Verify that the correct restaurant is returned by ID
        assertEquals(restaurant1, restaurantViewModel.getRestaurantById("1"));
        assertNull(restaurantViewModel.getRestaurantById("3"));
    }

    // Test case for listening to restaurant updates
    @Test
    public void testListenForRestaurantUpdates() {
        // Arrange: Set up mock data
        List<Restaurant> mockRestaurants = Arrays.asList(new Restaurant(), new Restaurant());

        // Simulate the callback when the repository calls back
        doAnswer(invocation -> {
            FirestoreRepository.OnRestaurantsRetrievedListener callback = invocation.getArgument(0);
            callback.onRestaurantsRetrieved(mockRestaurants);
            return null;
        }).when(firestoreRepository).listenForRestaurantUpdates(any(FirestoreRepository.OnRestaurantsRetrievedListener.class));

        // Act: Call the method to be tested
        restaurantViewModel.listenForRestaurantUpdates();

        // Assert: Verify that the observer is called with the correct data
        verify(restaurantListObserver).onChanged(mockRestaurants);
    }

    // Test case for removing listeners
    @Test
    public void testRemoveListeners() {
        // Act: Call the method to be tested
        restaurantViewModel.removeListeners();

        // Assert: Verify that the repository method to remove listeners is called
        verify(firestoreRepository).removeRestaurantListener();
    }
}