package com.example.myapplication.ui.coworkerListFragment;

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
import com.example.myapplication.DetailFragment;
import com.example.myapplication.SharedPlaceViewModel;
import com.example.myapplication.WorkmateListAdapter;
import com.example.myapplication.databinding.FragmentNotificationsBinding;

public class CoworkerListFragment extends Fragment implements WorkmateListAdapter.OnItemClickListener{

    private FragmentNotificationsBinding binding;
    private SharedPlaceViewModel sharedplaceViewModel;

    private RecyclerView recyclerView;
    private WorkmateListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedplaceViewModel = new ViewModelProvider(requireActivity()).get(SharedPlaceViewModel.class);

        // Initialize view binding and recyclerView
        initializeViewBinding(inflater,container);
        initializeRecyclerView();

        return binding.getRoot();
    }

    private void initializeViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
    }

    // Method to initialize the RecyclerView
    private void initializeRecyclerView(){
        // Use Glide to handle image loading
        RequestManager glide = Glide.with(this);
        recyclerView = binding.workmateRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter with Glide and the click listener
        adapter = new WorkmateListAdapter(glide, this);
        recyclerView.setAdapter(adapter);

        // Observe changes in the user list and update the adapter
        sharedplaceViewModel.getUserList().observe(getViewLifecycleOwner(), users ->
                adapter.setUsers(users));

        // Observe changes in filteredUsers data. (updated by a change in the searchBar)
        sharedplaceViewModel.getFilteredUsers().observe(getViewLifecycleOwner(), users ->
                adapter.setUsers(users));
    }

    @Override
    public void onItemClick(String restaurantId) {
        if (restaurantId != null) {
            // when a item is clicked, pass it to the viewModel and open DetailActivity
            sharedplaceViewModel.setPlaceForDetailFragmentById(restaurantId);
            DetailFragment detailBottomSheetFragment = new DetailFragment();
            detailBottomSheetFragment.show(getParentFragmentManager(), "DetailFragment");
        } else {
            Toast.makeText(getContext(), "aucun restaurant sélectionné", Toast.LENGTH_SHORT).show();
        }
    }

    // Clean up view binding to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}