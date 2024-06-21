package com.example.myapplication.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.databinding.FragmentRestaurantListBinding;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.ui.adapter.PlaceListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.viewModel.GooglePlaceViewModel;
import com.example.myapplication.viewModel.RestaurantViewModel;
import com.example.myapplication.viewModel.UserViewModel;
import com.example.myapplication.viewModel.ViewModelFactory.GooglePlaceViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.RestaurantViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.UserViewModelFactory;

public class RestaurantListFragment extends Fragment implements PlaceListAdapter.OnItemClickListener{

    private FragmentRestaurantListBinding fragmentRestaurantListBinding;
    private RecyclerView recyclerView;
    private PlaceListAdapter adapter;
    private String apiKey;

    private GooglePlaceViewModel googlePlaceViewModel;
    private UserViewModel userViewModel;
    private RestaurantViewModel restaurantViewModel;

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

        // Initialize view binding and recycler view.
        initializeViewBinding(inflater, container);
        initializeRecyclerView();

        return fragmentRestaurantListBinding.getRoot();
    }

    private void initializeViewBinding(LayoutInflater inflater, ViewGroup container) {
        fragmentRestaurantListBinding = FragmentRestaurantListBinding.inflate(inflater, container, false);
    }

    // initializes the RecyclerView, sets its layout manager, and adapter.
    private void initializeRecyclerView(){
        apiKey = getString(R.string.google_maps_key);
        recyclerView = fragmentRestaurantListBinding.recyclerViewXml;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlaceListAdapter(this, apiKey, restaurantViewModel, userViewModel);
        recyclerView.setAdapter(adapter);

        // Load data into the RecyclerView.
        displayRecyclerViewData();
    }

    // observes data changes and updates the RecyclerView accordingly.
    private void displayRecyclerViewData(){
        // Observe changes in combined places data. (updated by a change in the "CustomPlace" list and "RestaurantList" list)
        googlePlaceViewModel.getPlaces().observe(getViewLifecycleOwner(), places -> {
            restaurantViewModel.getRestaurantList().observe(getViewLifecycleOwner(), restaurantList -> {
                adapter.setPlaces(places, userViewModel.getCurrentUserLatLng().getValue());
            });
        });

        // Observe changes in filtered places data. (updated by a change in the searchBar)
        googlePlaceViewModel.getFilteredPlaces().observe(getViewLifecycleOwner(), places -> {
                adapter.setPlaces(places, userViewModel.getCurrentUserLatLng().getValue());
        });
    }

    @Override
    public void onItemClick(CustomPlace place) {
        // when a item is clicked, pass it to the viewModel and open DetailActivity
        googlePlaceViewModel.setSelectedPlace(place);
        DetailFragment detailBottomSheetFragment = new DetailFragment();
        detailBottomSheetFragment.show(getParentFragmentManager(), "DetailFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentRestaurantListBinding = null;
    }
}