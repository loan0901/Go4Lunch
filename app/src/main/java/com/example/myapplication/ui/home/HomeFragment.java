package com.example.myapplication.ui.home;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Model.Location;
import com.example.myapplication.Model.Place;
import com.example.myapplication.Model.SearchRequestModel;
import com.example.myapplication.PlacesResponse;
import com.example.myapplication.PlacesService;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient fusedLocationClient;
    private FragmentHomeBinding binding;
    private GoogleMap googleMap;

    private static LatLng userLatLng;

    public static PlacesResponse currentResults;

    private PlacesClient placesClient;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //ViewModelProvider
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //Initialize
        initializeViewBinding(inflater, container);
        initializeLocationServices();
        initializeMap();
        initializePlacesClient();

        return binding.getRoot();
    }

    private void initializeViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
    }

    private void initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    private void initializeMap() {
        binding.mapView.onCreate(null);
        binding.mapView.onResume();
        binding.mapView.getMapAsync(this);
    }

    private void initializePlacesClient() {
        Places.initialize(requireContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(requireContext());
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            onPermissionGranted();

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

            showRationaleDialog();

        } else {

            requestLocationPermission();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {

            onPermissionGranted();

        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

            showSettingsDialog();

        } else {

            checkPermission();
        }
    });

    private void onPermissionGranted() {

        getUserLatLng(latLng -> {
            showLastLocation(latLng);
            testSearchRestaurant(latLng);
        });
    }

    private void requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void showRationaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Cette application nécessite l'accès à votre position pour fonctionner correctement.")
                .setTitle("Permission recommandée")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> requestLocationPermission())
                .setNegativeButton("Annuler", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

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

    private void openAppSettings() {
        Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        settingIntent.setData(uri);
        startActivity(settingIntent);
    }

    private void showLastLocation(LatLng position) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
        }
    }

    public interface LatLngCallback {
        void onResult(LatLng latLng);
    }

    public void getUserLatLng(LatLngCallback callback) {
        if (userLatLng != null) {
            callback.onResult(userLatLng);
        } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                } else {
                    userLatLng = new LatLng(43.3, 5.4); // Valeur par défaut
                }
                callback.onResult(userLatLng);
            });
        } else {
            userLatLng = new LatLng(43.3, 5.4); // Valeur par défaut
            callback.onResult(userLatLng);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        System.out.println("Map is ready !");

        googleMap = map;

        configureMapStyle();
        checkPermission();
        configureLocationButton();
    }

    private void configureLocationButton() {
        MainActivity mainActivity = (MainActivity) getActivity();
        ImageButton imageButtonPosition = mainActivity.findViewById(R.id.locationButton);

        imageButtonPosition.setOnClickListener(view -> {
            checkPermission();
        });
    }

    private void configureMapStyle() {
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style);
        googleMap.setMapStyle(styleOptions);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void testSearchRestaurant(LatLng position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://places.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesService service = retrofit.create(PlacesService.class);

        // Construisez votre objet de requête ici
        SearchRequestModel request = new SearchRequestModel();
        request.includedTypes = Arrays.asList("restaurant");
        request.maxResultCount = 10;
        request.locationRestriction = new SearchRequestModel.LocationRestriction();
        request.locationRestriction.circle = new SearchRequestModel.Circle();
        request.locationRestriction.circle.center = new SearchRequestModel.Center();
        request.locationRestriction.circle.center.latitude = position.latitude;
        request.locationRestriction.circle.center.longitude = position.longitude;
        request.locationRestriction.circle.radius = 500.0;

        // Effectuez la requête
        System.out.println("Requesting results...");
        Call<PlacesResponse> call = service.searchNearby(request);

        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                if (response.isSuccessful()) {
                    // Gérez la réponse ici
                    System.out.println("got restaurant responses");

                    currentResults = response.body();

                    //TODO : remove all pins
                    for (Place place : currentResults.places) {
                        LatLng pos = new LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(place.getDisplayName().getText()));
                    }

                } else {
                    System.out.println("Request failed with error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}