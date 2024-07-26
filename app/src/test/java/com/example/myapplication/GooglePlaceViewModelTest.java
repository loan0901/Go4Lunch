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

@RunWith(MockitoJUnitRunner.class)
public class GooglePlaceViewModelTest {

    @Mock
    private GooglePlacesRepository googlePlacesRepository;

    @Mock
    private FirestoreRepositoryInterface firestoreRepositoryInterface;

    @InjectMocks
    private GooglePlaceViewModel googlePlaceViewModel;

    @Mock
    private Observer<List<CustomPlace>> placesObserver;

    @Mock
    private Observer<List<CustomPlace>> filteredPlacesObserver;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        googlePlaceViewModel = new GooglePlaceViewModel(googlePlacesRepository);
        googlePlaceViewModel.getPlaces().observeForever(placesObserver);
        googlePlaceViewModel.getFilteredPlaces().observeForever(filteredPlacesObserver);
    }

    @Test
    public void testLoadNearbyPlaces_success() {
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

        googlePlaceViewModel.loadNearbyPlaces("apiKey", 10.0, 20.0, 1000, 10, Arrays.asList("restaurant"), "fieldMask", firestoreRepositoryInterface);

        // Vérifiez que le LiveData places a bien été mis à jour
        verify(placesObserver).onChanged(mockPlaces);

        // Vérifiez que la méthode checkOrCreateRestaurant a été appelée sur firestoreRepositoryInterface
        for (CustomPlace place : mockPlaces) {
            verify(firestoreRepositoryInterface).checkOrCreateRestaurant(place.placeId, 0, null);
        }
    }

    @Test
    public void testGetPlaceDetails_success() {
        CustomPlace mockPlace = new CustomPlace();
        mockPlace.placeId = "1";

        // Simulate the callback when the repository calls back
        doAnswer(invocation -> {
            GooglePlacesRepository.PlaceDetailsCallback callback = invocation.getArgument(3);
            callback.onPlaceDetailsLoaded(mockPlace);
            return null;
        }).when(googlePlacesRepository).getPlaceDetails(anyString(), anyString(), anyString(), any(GooglePlacesRepository.PlaceDetailsCallback.class));

        googlePlaceViewModel.getPlaceDetails("placeId", "apiKey", "fieldMask", new GooglePlaceViewModel.PlaceDetailsCallback() {
            @Override
            public void onPlaceDetailsLoaded(CustomPlace placeDetails) {
                // Verify that the place details are added to the LiveData
                assertEquals(googlePlaceViewModel.getPlaces().getValue().get(0), mockPlace);
            }

            @Override
            public void onFailure(Exception e) {
                // No-op
            }
        });
    }

    @Test
    public void testFilterPlaces() {
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        place1.displayName = new CustomPlace.DisplayName();
        place1.displayName.value = "Place One";

        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        place2.displayName = new CustomPlace.DisplayName();
        place2.displayName.value = "Place Two";

        List<CustomPlace> mockPlaces = Arrays.asList(place1, place2);

        googlePlaceViewModel.setPlaces(mockPlaces);
        googlePlaceViewModel.filterPlaces("One");

        verify(filteredPlacesObserver).onChanged(Arrays.asList(place1));
    }

    @Test
    public void testSetPlaceForDetailFragmentById() {
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        place1.displayName = new CustomPlace.DisplayName();
        place1.displayName.value = "Place One";

        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        place2.displayName = new CustomPlace.DisplayName();
        place2.displayName.value = "Place Two";

        List<CustomPlace> mockPlaces = Arrays.asList(place1, place2);

        googlePlaceViewModel.setPlaces(mockPlaces);
        googlePlaceViewModel.setPlaceForDetailFragmentById("2");

        assertEquals(place2, googlePlaceViewModel.getSelectedPlace().getValue());
    }

    @Test
    public void testGetAllCustomPlaceName() {
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        place1.displayName = new CustomPlace.DisplayName();
        place1.displayName.value = "Place One";

        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        place2.displayName = new CustomPlace.DisplayName();
        place2.displayName.value = "Place Two";

        List<CustomPlace> mockPlaces = Arrays.asList(place1, place2);

        googlePlaceViewModel.setPlaces(mockPlaces);

        List<String> expectedNames = Arrays.asList("Place One", "Place Two");
        assertEquals(expectedNames, googlePlaceViewModel.getAllCustomPlaceName());
    }

    @Test
    public void testCheckIfPlaceListContainsRestaurant() {
        CustomPlace place1 = new CustomPlace();
        place1.placeId = "1";
        place1.displayName = new CustomPlace.DisplayName();
        place1.displayName.value = "Place One";

        CustomPlace place2 = new CustomPlace();
        place2.placeId = "2";
        place2.displayName = new CustomPlace.DisplayName();
        place2.displayName.value = "Place Two";

        List<CustomPlace> mockPlaces = Arrays.asList(place1, place2);

        googlePlaceViewModel.setPlaces(mockPlaces);

        assertTrue(googlePlaceViewModel.checkIfPlaceListContainsRestaurant("1"));
        assertFalse(googlePlaceViewModel.checkIfPlaceListContainsRestaurant("3"));
    }
}
