package com.example.myapplication.ui.RestaurantListFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DetailFragment;
import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.PlaceListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.SharedPlaceViewModel;
import com.example.myapplication.databinding.FragmentDashboardBinding;

public class RestaurantListFragment extends Fragment implements PlaceListAdapter.OnItemClickListener{

    private FragmentDashboardBinding binding;
    private RecyclerView recyclerView;
    private PlaceListAdapter adapter;
    private SharedPlaceViewModel sharedPlaceViewModel;
    private String apiKey;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the ViewModel for shared data.
        sharedPlaceViewModel = new ViewModelProvider(requireActivity()).get(SharedPlaceViewModel.class);

        // Initialize view binding and recycler view.
        initializeViewBinding(inflater, container);
        initializeRecyclerView();

        return binding.getRoot();
    }

    private void initializeViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
    }

    // initializes the RecyclerView, sets its layout manager, and adapter.
    private void initializeRecyclerView(){
        apiKey = getString(R.string.google_maps_key);
        recyclerView = binding.recyclerViewXml;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlaceListAdapter(this, apiKey, sharedPlaceViewModel);
        recyclerView.setAdapter(adapter);

        // Load data into the RecyclerView.
        displayRecyclerViewData();
    }

    // observes data changes and updates the RecyclerView accordingly.
    private void displayRecyclerViewData(){
        // Observe changes in combined places data. (updated by a change in the "CustomPlace" list and "RestaurantList" list)
        sharedPlaceViewModel.getCombinedPlaces().observe(getViewLifecycleOwner(), places -> {
            adapter.setPlaces(places, sharedPlaceViewModel.getCurrentUserLatLng().getValue());
        });

        // Observe changes in filtered places data. (updated by a change in the searchBar)
        sharedPlaceViewModel.getFilteredPlaces().observe(getViewLifecycleOwner(), places -> {
                adapter.setPlaces(places, sharedPlaceViewModel.getCurrentUserLatLng().getValue());
        });
    }

    @Override
    public void onItemClick(CustomPlace place) {
        // when a item is clicked, pass it to the viewModel and open DetailActivity
        sharedPlaceViewModel.setSelectedPlace(place);
        DetailFragment detailBottomSheetFragment = new DetailFragment();
        detailBottomSheetFragment.show(getParentFragmentManager(), "DetailFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}