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

@RunWith(MockitoJUnitRunner.class)
public class RestaurantViewModelTest {

    @Mock
    private FirestoreRepository firestoreRepository;

    @InjectMocks
    private RestaurantViewModel restaurantViewModel;

    @Mock
    private Observer<List<Restaurant>> restaurantListObserver;

    @Mock
    private Observer<String> selectedRestaurantNameObserver;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurantViewModel = new RestaurantViewModel(firestoreRepository);
        restaurantViewModel.getRestaurantList().observeForever(restaurantListObserver);
        restaurantViewModel.getSelectedRestaurantName().observeForever(selectedRestaurantNameObserver);
    }

    @Test
    public void testToggleFavoriteRestaurant() {
        String uid = "user123";
        String restaurantId = "restaurant123";

        restaurantViewModel.toggleFavoriteRestaurant(uid, restaurantId);

        verify(firestoreRepository).toggleFavoriteRestaurant(uid, restaurantId);
    }

    @Test
    public void testUpdateSelectedRestaurant() {
        String uid = "user123";
        String newRestaurantId = "restaurant123";
        String restaurantName = "Restaurant Name";

        restaurantViewModel.updateSelectedRestaurant(uid, newRestaurantId, restaurantName);

        verify(firestoreRepository).updateSelectedRestaurant(uid, newRestaurantId, restaurantName);
    }

    @Test
    public void testUpdateRestaurantList() {
        List<Restaurant> mockRestaurants = Arrays.asList(new Restaurant(), new Restaurant());

        doAnswer(invocation -> {
            FirestoreRepository.OnRestaurantsRetrievedListener callback = invocation.getArgument(0);
            callback.onRestaurantsRetrieved(mockRestaurants);
            return null;
        }).when(firestoreRepository).getAllRestaurants(any(FirestoreRepository.OnRestaurantsRetrievedListener.class));

        restaurantViewModel.updateRestaurantList();

        verify(restaurantListObserver).onChanged(mockRestaurants);
    }

    @Test
    public void testGetRestaurantById() {
        Restaurant restaurant1 = new Restaurant();
        Restaurant restaurant2 = new Restaurant();
        restaurant1.setRestaurantId("1");
        restaurant2.setRestaurantId("2");
        List<Restaurant> mockRestaurants = Arrays.asList(restaurant1, restaurant2);

        restaurantViewModel.setRestaurantList(mockRestaurants);

        assertEquals(restaurant1, restaurantViewModel.getRestaurantById("1"));
        assertNull(restaurantViewModel.getRestaurantById("3"));
    }

    @Test
    public void testListenForRestaurantUpdates() {
        List<Restaurant> mockRestaurants = Arrays.asList(new Restaurant(), new Restaurant());

        doAnswer(invocation -> {
            FirestoreRepository.OnRestaurantsRetrievedListener callback = invocation.getArgument(0);
            callback.onRestaurantsRetrieved(mockRestaurants);
            return null;
        }).when(firestoreRepository).listenForRestaurantUpdates(any(FirestoreRepository.OnRestaurantsRetrievedListener.class));

        restaurantViewModel.listenForRestaurantUpdates();

        verify(restaurantListObserver).onChanged(mockRestaurants);
    }

    @Test
    public void testRemoveListeners() {
        restaurantViewModel.removeListeners();

        verify(firestoreRepository).removeRestaurantListener();
    }
}
