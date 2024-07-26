package com.example.myapplication;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.myapplication.Repository.FirestoreRepositoryInterface;
import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.viewModel.GooglePlaceViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Unit test class for GooglePlaceViewModel, testing various methods related to managing places.
@RunWith(MockitoJUnitRunner.class)
public class GooglePlaceViewModelTest {

    // Mock dependencies for the test
    @Mock
    private GooglePlacesRepository googlePlacesRepository;

    @Mock
    private FirestoreRepositoryInterface firestoreRepositoryInterface;

    // Inject mocks into the ViewModel instance
    @InjectMocks
    private GooglePlaceViewModel googlePlaceViewModel;

    // Mock observers for LiveData
    @Mock
    private Observer<List<CustomPlace>> placesObserver;

    @Mock
    private Observer<List<CustomPlace>> filteredPlacesObserver;

    // Rule to ensure LiveData updates happen instantly during tests
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    // Set up method to initialize mocks and observers
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        googlePlaceViewModel = new GooglePlaceViewModel(googlePlacesRepository);
        googlePlaceViewModel.getPlaces().observeForever(placesObserver);
        googlePlaceViewModel.getFilteredPlaces().observeForever(filteredPlacesObserver);
    }

    // Test the loadNearbyPlaces method for a successful response
    @Test
    public void testLoadNearbyPlaces_success() {
        // Arrange: Set up test data and mock behavior
        List<CustomPlace> mockPlaces = new ArrayList<>();
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        mockPlaces.add(place1);
        mockPlaces.add(place2);

        // Simulate the callback when the repository calls back
        doAnswer(invocation -> {
            GooglePlacesRepository.GooglePlacesCallback callback = invocation.getArgument(7);
            callback.onNearbyRestaurantsLoaded(mockPlaces);
            return null;
        }).when(googlePlacesRepository).loadNearbyRestaurants(anyString(), anyDouble(), anyDouble(), anyDouble(), anyInt(), anyList(), anyString(), any(GooglePlacesRepository.GooglePlacesCallback.class));

        // Act: Call the method to be tested
        googlePlaceViewModel.loadNearbyPlaces("apiKey", 10.0, 20.0, 1000, 10, Arrays.asList("restaurant"), "fieldMask", firestoreRepositoryInterface);

        // Assert: Verify that the LiveData places has been updated
        verify(placesObserver).onChanged(mockPlaces);

        // Assert: Verify that the checkOrCreateRestaurant method has been called on firestoreRepositoryInterface
        for (CustomPlace place : mockPlaces) {
            verify(firestoreRepositoryInterface).checkOrCreateRestaurant(place.placeId, 0, null);
        }
    }

    // Test the getPlaceDetails method for a successful response
    @Test
    public void testGetPlaceDetails_success() {
        // Arrange: Set up test data and mock behavior
        CustomPlace mockPlace = new CustomPlace();
        mockPlace.placeId = "1";

        // Simulate the callback when the repository calls back
        doAnswer(invocation -> {
            GooglePlacesRepository.PlaceDetailsCallback callback = invocation.getArgument(3);
            callback.onPlaceDetailsLoaded(mockPlace);
            return null;
        }).when(googlePlacesRepository).getPlaceDetails(anyString(), anyString(), anyString(), any(GooglePlacesRepository.PlaceDetailsCallback.class));

        // Act: Call the method to be tested
        googlePlaceViewModel.getPlaceDetails("placeId", "apiKey", "fieldMask", new GooglePlaceViewModel.PlaceDetailsCallback() {
            @Override
            public void onPlaceDetailsLoaded(CustomPlace placeDetails) {
                // Assert: Verify that the place details are added to the LiveData
                assertEquals(googlePlaceViewModel.getPlaces().getValue().get(0), mockPlace);
            }

            @Override
            public void onFailure(Exception e) {
                // No-op
            }
        });
    }

    // Test the filterPlaces method
    @Test
    public void testFilterPlaces() {
        // Arrange: Set up test data
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        place1.displayName = new CustomPlace.DisplayName();
        place1.displayName.value = "Place One";

        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        place2.displayName = new CustomPlace.DisplayName();
        place2.displayName.value = "Place Two";

        List<CustomPlace> mockPlaces = Arrays.asList(place1, place2);

        // Act: Set the places and filter them
        googlePlaceViewModel.setPlaces(mockPlaces);
        googlePlaceViewModel.filterPlaces("One");

        // Assert: Verify that the filtered places observer is called with the correct data
        verify(filteredPlacesObserver).onChanged(Arrays.asList(place1));
    }

    // Test the setPlaceForDetailFragmentById method
    @Test
    public void testSetPlaceForDetailFragmentById() {
        // Arrange: Set up test data
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        place1.displayName = new CustomPlace.DisplayName();
        place1.displayName.value = "Place One";

        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        place2.displayName = new CustomPlace.DisplayName();
        place2.displayName.value = "Place Two";

        List<CustomPlace> mockPlaces = Arrays.asList(place1, place2);

        // Act: Set the places and select a place by ID
        googlePlaceViewModel.setPlaces(mockPlaces);
        googlePlaceViewModel.setPlaceForDetailFragmentById("2");

        // Assert: Verify that the selected place is correct
        assertEquals(place2, googlePlaceViewModel.getSelectedPlace().getValue());
    }

    // Test the getAllCustomPlaceName method
    @Test
    public void testGetAllCustomPlaceName() {
        // Arrange: Set up test data
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        place1.displayName = new CustomPlace.DisplayName();
        place1.displayName.value = "Place One";

        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        place2.displayName = new CustomPlace.DisplayName();
        place2.displayName.value = "Place Two";

        List<CustomPlace> mockPlaces = Arrays.asList(place1, place2);

        // Act: Set the places and get all place names
        googlePlaceViewModel.setPlaces(mockPlaces);

        // Assert: Verify that the returned place names are correct
        List<String> expectedNames = Arrays.asList("Place One", "Place Two");
        assertEquals(expectedNames, googlePlaceViewModel.getAllCustomPlaceName());
    }

    // Test the checkIfPlaceListContainsRestaurant method
    @Test
    public void testCheckIfPlaceListContainsRestaurant() {
        // Arrange: Set up test data
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        place1.displayName = new CustomPlace.DisplayName();
        place1.displayName.value = "Place One";

        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        place2.displayName = new CustomPlace.DisplayName();
        place2.displayName.value = "Place Two";

        List<CustomPlace> mockPlaces = Arrays.asList(place1, place2);

        // Act: Set the places and check if the list contains certain restaurants
        googlePlaceViewModel.setPlaces(mockPlaces);

        // Assert: Verify that the method returns the correct boolean values
        assertTrue(googlePlaceViewModel.checkIfPlaceListContainsRestaurant("1"));
        assertFalse(googlePlaceViewModel.checkIfPlaceListContainsRestaurant("3"));
    }
}
