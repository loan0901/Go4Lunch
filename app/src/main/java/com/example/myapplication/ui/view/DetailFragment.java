package com.example.myapplication.ui.view;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.ui.adapter.DetailUserAdapter;
import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.example.myapplication.viewModel.GooglePlaceViewModel;
import com.example.myapplication.databinding.FragmentDetailBinding;
import com.example.myapplication.service.AddressService;
import com.example.myapplication.service.PhotoService;
import com.example.myapplication.service.RatingService;
import com.example.myapplication.viewModel.RestaurantViewModel;
import com.example.myapplication.viewModel.UserViewModel;
import com.example.myapplication.viewModel.ViewModelFactory.GooglePlaceViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.RestaurantViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.UserViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class DetailFragment extends BottomSheetDialogFragment {

    private FragmentDetailBinding binding;
    private PhotoService photoService;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private RatingService ratingService;

    private RecyclerView recyclerView;
    private DetailUserAdapter adapter;

    private GooglePlaceViewModel googlePlaceViewModel;
    private RestaurantViewModel restaurantViewModel;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

        //ViewBinding
        initializeViewBinding(inflater, container);

        // Initialisation de RatingService
        this.ratingService = new RatingService();

        // Initialize services and Firebase
        String apikey = getString(R.string.google_maps_key);
        photoService = new PhotoService(apikey);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Display place information
        displayPlaceInfo();

        return binding.getRoot();
    }

    private void initializeViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // initialize the BottomSheetDialog
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                // Apply rounded corners
                ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder(
                        getContext(),
                        0,
                        R.style.ShapeAppearanceOverlay_BottomSheet
                ).build();

                // set the model and color
                MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
                materialShapeDrawable.setFillColor(new ColorStateList(new int[][]{new int[0]}, new int[]{Color.WHITE}));
                bottomSheet.setBackground(materialShapeDrawable);
            }
        });
        return dialog;
    }

    // Method to display place information
    private void displayPlaceInfo(){
        googlePlaceViewModel.getSelectedPlace().observe(getViewLifecycleOwner(), place -> {

            // updated by change in the restaurantList or userList
            restaurantViewModel.getRestaurantList().observe(getViewLifecycleOwner(), restaurantList -> {
                userViewModel.getUserList().observe(getViewLifecycleOwner(), userList -> {

                    Restaurant currentRestaurant = restaurantViewModel.getRestaurantById(place.placeId);

                    // get userID
                    User user = userViewModel.getUserById(currentUser.getUid());

                    //Display photo
                    photoService.displayFirstPhoto(place, binding.placeImage);

                    // display restaurant name and address
                    binding.restaurantName.setText(place.displayName.value);
                    binding.restaurantAddress.setText(AddressService.getStreetAndNumber(place.address));

                    // Show star ratings
                    ratingService.computeRating(currentRestaurant, userList.size(), new RatingService.OnRatingComputedListener() {
                        @Override
                        public void onRatingComputed(int rating) {
                            binding.oneStarsDetail.setVisibility(View.INVISIBLE);
                            binding.twoStarsDetail.setVisibility(View.INVISIBLE);
                            binding.threeStarsDetail.setVisibility(View.INVISIBLE);
                            if (rating >= 1) {
                                binding.oneStarsDetail.setVisibility(View.VISIBLE);
                            }
                            if (rating >= 2) {
                                binding.twoStarsDetail.setVisibility(View.VISIBLE);
                            }
                            if (rating == 3) {
                                binding.threeStarsDetail.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle error
                            Log.e("RatingError", "Error computing rating", e);
                        }
                    });

                    //update select button
                    updateFabDrawable(user, place.placeId);

                    // change selected restaurant in the dataBase
                    binding.fabIsSelected.setOnClickListener(v -> {
                        restaurantViewModel.updateSelectedRestaurant(currentUser.getUid(), place.placeId, place.displayName.value);
                    });

                    // Phone button click listener
                    binding.buttonCall.setOnClickListener(view -> {
                        if (place.phoneNumber != null) {
                            openDialerWithPhoneNumber(place.phoneNumber);
                        } else {
                            Toast.makeText(getContext(), R.string.no_phone_restaurant, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Website button click listener
                    binding.buttonWebSite.setOnClickListener(view -> {
                        if (place.websiteUri != null) {
                            openWebLink(Uri.parse(place.websiteUri));
                        } else {
                            Toast.makeText(getContext(), R.string.no_website, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Update like button state
                    updateLikeButton(user, place.placeId);

                    // Add and remove like to database
                    binding.buttonFavorite.setOnClickListener(view -> {
                        restaurantViewModel.toggleFavoriteRestaurant(currentUser.getUid(), place.placeId);
                    });

                    // Show RecyclerView with workmate list
                    initializeRecyclerView(place.placeId);
                });
            });
        });
    }

    // Method to initialize RecyclerView
    private void initializeRecyclerView(String restaurantId){
        RequestManager glide = Glide.with(this);
        recyclerView = binding.recyclerViewDetail;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DetailUserAdapter(getContext(), glide);
        recyclerView.setAdapter(adapter);

        // display the list of users who selected this restaurant if there is at least one
        if (restaurantId != null){
            List<User> userList = userViewModel.getUsersBySelectedRestaurantId(restaurantId);
            if (userList.isEmpty()){
                binding.recyclerViewDetail.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewDetail.setVisibility(View.VISIBLE);
                binding.emptyView.setVisibility(View.GONE);
                adapter.setUsers(userList);
            }
        }
    }

    // Method to update FAB drawable state
    private void updateFabDrawable(User user, String restaurantId){
        if (user.getSelectedRestaurantId() != null && user.getSelectedRestaurantId().contains(restaurantId)){
            binding.fabIsSelected.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.fab_icon_selected_48));
        } else {
            binding.fabIsSelected.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.fab_icon_unsected_48));
        }
    }

    // Method to update like button state
    private void updateLikeButton(User user, String restaurantId){
        if (user.getFavoriteRestaurants() != null && user.getFavoriteRestaurants().contains(restaurantId)){
            binding.buttonFavorite.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_liked_48), null, null);
        } else {
            binding.buttonFavorite.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_unliked_48), null, null);
        }
    }

    // Method to open dialer with phone number
    private void openDialerWithPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    // Method to open web link
    private void openWebLink(Uri webUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(webUri);
        startActivity(intent);
    }
}
