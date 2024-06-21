package com.example.myapplication.ui.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.databinding.FragmentMapBinding;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.viewModel.GooglePlaceViewModel;
import com.example.myapplication.R;

import com.example.myapplication.service.CustomMarkerService;
import com.example.myapplication.viewModel.RestaurantViewModel;
import com.example.myapplication.viewModel.UserViewModel;
import com.example.myapplication.viewModel.ViewModelFactory.GooglePlaceViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.RestaurantViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.UserViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapFragment extends Fragment implements OnMapReadyCallback{

    private static final float DEFAULT_ZOOM = 18f;
    private static final long LOADING_TIMEOUT = 10000L; // 10 seconds

    private FusedLocationProviderClient fusedLocationClient;
    private FragmentMapBinding fragmentMapBinding;
    private GoogleMap googleMap;
    private final Map<String, Marker> markerMap = new HashMap<>();
    private static LatLng userLatLng;
    private PlacesClient placesClient;
    private ProgressBar progressBar;

    private GooglePlaceViewModel googlePlaceViewModel;
    private RestaurantViewModel restaurantViewModel;
    private UserViewModel userViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the repository
        GooglePlacesRepository googlePlacesRepository = new GooglePlacesRepository(RetrofitClient.getApiService());
        FirestoreRepository firestoreRepository = new FirestoreRepository();

        // Initialize the ViewModel with Factory
        GooglePlaceViewModelFactory factory = new GooglePlaceViewModelFactory(googlePlacesRepository);
        RestaurantViewModelFactory restaurantViewModelFactory = new RestaurantViewModelFactory(firestoreRepository);
        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(firestoreRepository);

        googlePlaceViewModel = new ViewModelProvider(requireActivity(), factory).get(GooglePlaceViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity(), restaurantViewModelFactory).get(RestaurantViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity(), userViewModelFactory).get(UserViewModel.class);

        initializeViewBinding(inflater, container);
        initializeLocationServices();
        initializeMap();
        initializePlacesClient();
        showPlaceAfterSearch();

        // Display nearby restaurants if already load
        displayNearbyRestaurant();

        return fragmentMapBinding.getRoot();
    }

    // Initialize view binding
    private void initializeViewBinding(LayoutInflater inflater, ViewGroup container) {
        fragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false);
        progressBar = fragmentMapBinding.progressBar;
    }

    // Initialize location services
    private void initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    // Initialize map
    private void initializeMap() {
        fragmentMapBinding.mapView.onCreate(null);
        fragmentMapBinding.mapView.onResume();
        fragmentMapBinding.mapView.getMapAsync(this);
    }

    // Initialize Google Places client
    private void initializePlacesClient() {
        Places.initialize(requireContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(requireContext());
    }

    // Show place on map after search
    private void showPlaceAfterSearch(){
        restaurantViewModel.getSelectedRestaurantName().observe(getViewLifecycleOwner(), selectedRestaurantName -> {
            if (selectedRestaurantName != null) {
                Marker marker = markerMap.get(selectedRestaurantName);
                if (marker != null){
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15f));
                    marker.showInfoWindow();
                } else {
                    Toast.makeText(requireContext(), "aucun restaurant correspondant", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Check for location permission
    private void checkPermissionAndShowNearbyRestaurant() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if is granted, get user location and show nearby restaurant
            onPermissionGranted();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // show dialog to explain and ask again for permission
            showRationaleDialog();
        } else {
            // ask for permission
            requestLocationPermission();
        }
    }

    // Request location permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // if is granted, get user location and show nearby restaurant
            onPermissionGranted();
        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // if he refuses twice, explain to him and guide him to the settings
            showSettingsDialog();
        } else {
            checkPermissionAndShowNearbyRestaurant();
        }
    });

    // on permission granted, get user's location and show nearby restaurant
    private void onPermissionGranted() {
        getUserLatLng(this::showLastLocation);

        // if the "customPlace" have not yet been loaded, load them
        if (googlePlaceViewModel.getPlaces().getValue() == null || googlePlaceViewModel.getPlaces().getValue().isEmpty()) {
            loadNearbyRestaurants();
        } else {
            // else, display the "customPlace" with ViewModel
            displayNearbyRestaurant();
        }
    }

    // Request location permission
    private void requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    // Show rationale dialog for location permission
    private void showRationaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Cette application nécessite l'accès à votre position pour fonctionner correctement.")
                .setTitle("Permission recommandée")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> requestLocationPermission())
                .setNegativeButton("Annuler", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    // Show settings dialog to enable location permission
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Pour afficher la carte autour de vous, l'application nécessite l'accès à votre position que vous avez refusée. Vous pouvez toujours l'autoriser dans les paramètres.")
                .setTitle("Permission requise")
                .setCancelable(false)
                .setNegativeButton("Annuler", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Paramètres", (dialogInterface, i) -> {
                    openAppSettings();
                    dialogInterface.dismiss();
                });
        builder.show();
    }

    // Open app settings to enable location permission
    private void openAppSettings() {
        Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        settingIntent.setData(uri);
        startActivity(settingIntent);
    }

    // Show last known location on the map
    private void showLastLocation(LatLng position) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));

            userViewModel.setCurrentUserLatLng(position);
        }
    }

    // Callback interface for user location
    public interface LatLngCallback {
        void onResult(LatLng latLng);
    }

    // Get user's last known location, if this is not possible, display the default position (the company position)
    public void getUserLatLng(LatLngCallback callback) {
        LatLng defaultLocation = new LatLng(46.2, 2.1);
        if (userLatLng != null && !userLatLng.equals(defaultLocation)) {
            callback.onResult(userLatLng);
        } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                } else {
                    userLatLng = defaultLocation; // Default location
                    Toast.makeText(requireContext(), "Unable to display your location, showing default location.", Toast.LENGTH_SHORT).show();
                }
                callback.onResult(userLatLng);
            }).addOnFailureListener(e -> {
                userLatLng = defaultLocation; // Default location in case of failure
                Toast.makeText(requireContext(), "Failed to fetch your location, showing default location.", Toast.LENGTH_SHORT).show();
                callback.onResult(userLatLng);
            });
        } else {
            userLatLng = new LatLng(46.2, 2.1); // Default location if permission not granted
            Toast.makeText(requireContext(), "Unable to display your location, showing default location.", Toast.LENGTH_SHORT).show();
            callback.onResult(userLatLng);
        }
    }

    // initialise map, permission, location, etc... when the map is ready to be used
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        System.out.println("Map is ready !");

        googleMap = map;

        configureMapStyle();
        checkPermissionAndShowNearbyRestaurant();
        configureLocationButton();
        openPlaceDetailByMarkerId();
    }

    // Configure the location button : get current location and display nearby restaurant on map
    private void configureLocationButton() {
        fragmentMapBinding.fabLocation.setOnClickListener(view -> {
            getUserLatLng(this::showLastLocation);
            checkPermissionAndShowNearbyRestaurant();
        });
    }

    // Configure the map style
    private void configureMapStyle() {
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style);
        googleMap.setMapStyle(styleOptions);
    }

    // Load nearby restaurants and add them to the View Model
    private void loadNearbyRestaurants() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // get restaurants with only the information that is necessary
        String apiKey = getString(R.string.google_maps_key);
        String fieldMask = "places.id,places.displayName,places.formattedAddress,places.location,places.rating,places.regularOpeningHours,places.photos,places.internationalPhoneNumber,places.websiteUri";
        double radius = 500.0;
        int maxResultCount = 5;
        List<String> includedTypes = Collections.singletonList("restaurant");
        getUserLatLng(latLng -> {
            // add all the CustomPlace to the ViewModel & data base
            googlePlaceViewModel.loadNearbyPlaces(apiKey, latLng.latitude, latLng.longitude, radius, maxResultCount, includedTypes, fieldMask, restaurantViewModel.getFirestoreRepository());

            // Set a timeout for hiding the loading indicator in case of no results
            new Handler(Looper.getMainLooper()).postDelayed(() -> progressBar.setVisibility(View.GONE), LOADING_TIMEOUT);
        });
    }

    // Display nearby restaurants on the map. automatically updated when "customPlace" or "Restaurant" is changed
    private void displayNearbyRestaurant(){
        googlePlaceViewModel.getPlaces().observe(getViewLifecycleOwner(), places -> {
            restaurantViewModel.getRestaurantList().observe(getViewLifecycleOwner(), restaurantList -> {
                if (places != null) {
                    clearMarkers();
                    for (CustomPlace place : places) {
                        // add a marker on the map
                        placePin(place);
                    }
                    // Hide loading indicator after places are displayed
                    progressBar.setVisibility(View.GONE);
                }
            });
        });
    }

    // Add a marker for the place on the map
    private void placePin(CustomPlace place){
        Restaurant restaurant = restaurantViewModel.getRestaurantById(place.placeId);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(place.location)
                .title(place.displayName.value));
        // if the restaurant is selected by at least one user, change the color
        if (restaurant != null) {
            if (restaurant.getUserIdSelected() == null || restaurant.getUserIdSelected().isEmpty()) {
                marker.setIcon(CustomMarkerService.getMarkerIconFromVector(requireContext(), R.drawable.custom_marker_red));
            } else {
                marker.setIcon(CustomMarkerService.getMarkerIconFromVector(requireContext(), R.drawable.custom_marker_green));
            }
            marker.setTag(place.placeId);
            markerMap.put(place.displayName.value, marker);
        }
    }

    // Open the place detail by marker ID
    private void openPlaceDetailByMarkerId(){
        googleMap.setOnMarkerClickListener(marker -> {
            String placeId = (String) marker.getTag();
            if (placeId != null) {
                // when a marker is clicked, pass it to the viewModel and open DetailActivity
                googlePlaceViewModel.setPlaceForDetailFragmentById(placeId);
                DetailFragment detailBottomSheetFragment = new DetailFragment();
                detailBottomSheetFragment.show(getParentFragmentManager(), "DetailFragment");
            }
            return false;
        });
    }

    // Clear all marker
    private void clearMarkers() {
        for (Marker marker : markerMap.values()) {
            marker.remove();
        }
        markerMap.clear();
    }

    // Clean up view binding on view destruction
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMapBinding = null;
    }
}