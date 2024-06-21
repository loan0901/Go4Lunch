package com.example.myapplication.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.R;
import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.databinding.FragmentCoworkerBinding;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.viewModel.GooglePlaceViewModel;
import com.example.myapplication.ui.adapter.WorkmateListAdapter;
import com.example.myapplication.viewModel.UserViewModel;
import com.example.myapplication.viewModel.ViewModelFactory.GooglePlaceViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.UserViewModelFactory;

public class CoworkerListFragment extends Fragment implements WorkmateListAdapter.OnItemClickListener{

    private FragmentCoworkerBinding fragmentCoworkerBinding;
    private GooglePlaceViewModel googlePlaceViewModel;
    private UserViewModel userViewModel;

    private RecyclerView recyclerView;
    private WorkmateListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the repository
        GooglePlacesRepository googlePlacesRepository = new GooglePlacesRepository(RetrofitClient.getApiService());
        FirestoreRepository firestoreRepository = new FirestoreRepository();

        // Initialize the ViewModel with Factory
        GooglePlaceViewModelFactory factory = new GooglePlaceViewModelFactory(googlePlacesRepository);
        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(firestoreRepository);

        googlePlaceViewModel = new ViewModelProvider(requireActivity(), factory).get(GooglePlaceViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity(), userViewModelFactory).get(UserViewModel.class);

        // Initialize view binding and recyclerView
        initializeViewBinding(inflater,container);
        initializeRecyclerView();

        return fragmentCoworkerBinding.getRoot();
    }

    private void initializeViewBinding(LayoutInflater inflater, ViewGroup container) {
        fragmentCoworkerBinding = FragmentCoworkerBinding.inflate(inflater, container, false);
    }

    // Method to initialize the RecyclerView
    private void initializeRecyclerView(){
        // Use Glide to handle image loading
        RequestManager glide = Glide.with(this);
        recyclerView = fragmentCoworkerBinding.workmateRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter with Glide and the click listener
        adapter = new WorkmateListAdapter(glide, this);
        recyclerView.setAdapter(adapter);

        // Observe changes in the user list and update the adapter
        userViewModel.getUserList().observe(getViewLifecycleOwner(), users ->
                adapter.setUsers(users));

        // Observe changes in filteredUsers data. (updated by a change in the searchBar)
        userViewModel.getFilteredUsers().observe(getViewLifecycleOwner(), users ->
                adapter.setUsers(users));
    }

    @Override
    public void onItemClick(String restaurantId) {
        //check if the user has chosen a restaurant
        if (restaurantId != null ) {
            //check if its already in the viewModel, open DetailActivity
            if (googlePlaceViewModel.checkIfPlaceListContainsRestaurant(restaurantId)) {
                openDetailActivity(restaurantId);
            } else {
                // otherwise, load the place, add it to the viewModel and open DetailActivity
                String apiKey = getString(R.string.google_maps_key);
                String fieldMask = "id,displayName,formattedAddress,location,rating,regularOpeningHours,photos,internationalPhoneNumber,websiteUri";
                googlePlaceViewModel.getPlaceDetails(restaurantId, apiKey, fieldMask, new GooglePlaceViewModel.PlaceDetailsCallback() {
                    @Override
                    public void onPlaceDetailsLoaded(CustomPlace placeDetails) {
                        openDetailActivity(restaurantId);
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Erreur lors de la récupération du restaurant", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // if no restaurant is selected, notify the user
            Toast.makeText(getContext(), "aucun restaurant sélectionné", Toast.LENGTH_SHORT).show();
        }
    }

    // methode to open the DetailActivity with the restaurantId
    private void openDetailActivity(String restaurantId){
        googlePlaceViewModel.setPlaceForDetailFragmentById(restaurantId);
        DetailFragment detailBottomSheetFragment = new DetailFragment();
        detailBottomSheetFragment.show(getParentFragmentManager(), "DetailFragment");
    }

    // Clean up view binding to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCoworkerBinding = null;
    }
}