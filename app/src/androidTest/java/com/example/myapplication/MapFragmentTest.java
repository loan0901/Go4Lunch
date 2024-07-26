package com.example.myapplication;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.ui.view.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Collections;

@RunWith(AndroidJUnit4.class)
public class MapFragmentTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Mock
    FirebaseAuth mockFirebaseAuth;

    @Mock
    FirebaseUser mockFirebaseUser;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock FirebaseAuth
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn("mockUid");
        when(mockFirebaseUser.getDisplayName()).thenReturn("Mock User");
        when(mockFirebaseUser.getPhotoUrl()).thenReturn(null);

        // Initialize Places API
        Places.initialize(InstrumentationRegistry.getInstrumentation().getTargetContext(), "TEST_API_KEY");
    }

    @After
    public void tearDown() {
        // Cleanup code if needed
    }

    @Test
    public void testPermissionGranted() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);

        scenario.onFragment(fragment -> {
            fragment.onPermissionGranted();

            // Verify that nearby restaurants are shown
            assertNotNull(fragment.getGooglePlaceViewModel().getPlaces().getValue());
        });
    }

    @Test
    public void testMapFragmentUIComponents() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);

        // Check if ProgressBar is displayed
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));

        // Check if MapView is displayed
        onView(withId(R.id.mapView)).check(matches(isDisplayed()));

        // Check if Location Button is displayed
        onView(withId(R.id.fabLocation)).check(matches(isDisplayed()));
    }

    @Test
    public void testLocationButtonClick() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);

        // Click on the location button
        onView(withId(R.id.fabLocation)).perform(click());

        // Check if ProgressBar is displayed after clicking the location button
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
    }

    @Test
    public void testShowLastLocation() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);

        scenario.onFragment(fragment -> {
            LatLng testLatLng = new LatLng(46.2, 2.1);

            // Simulate showing the last location on the map
            fragment.showLastLocation(testLatLng);

            // Check if camera position is the same as testLatLng
            assertNotNull(fragment.getGoogleMap());
            assertEquals(fragment.getGoogleMap().getCameraPosition().target, testLatLng);
        });
    }

    @Test
    public void testShowNearbyRestaurants() {
        FragmentScenario<MapFragment> scenario = FragmentScenario.launchInContainer(MapFragment.class);

        // Simulate a nearby restaurant being added to the map
        scenario.onFragment(fragment -> {
            CustomPlace place = new CustomPlace();
            place.placeId = "testPlaceId";
            place.displayName = new CustomPlace.DisplayName();
            place.displayName.value = "Test Restaurant";
            place.location = new LatLng(46.2, 2.1);

            Restaurant restaurant = new Restaurant();
            restaurant.setUserIdSelected(Collections.emptyList());

            when(fragment.getRestaurantViewModel().getRestaurantById("testPlaceId")).thenReturn(restaurant);

            fragment.placePin(place);

            // Check if the marker for the restaurant is added to the map
            assertNotNull(fragment.getMarkerMap().get("Test Restaurant"));
        });
    }
}