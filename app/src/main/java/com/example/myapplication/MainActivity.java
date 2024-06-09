package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.service.FirestoreUtils;
import com.example.myapplication.ui.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirestoreUtils firestoreUtils;
    private SharedPlaceViewModel viewModel;
    private View rootView;
    private boolean isKeyboardVisible = false;
    private boolean isSecondFragmentVisible = false;
    private boolean isThirdFragmentVisible = false;

    private AutoCompleteTextView autoCompleteSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rootView = findViewById(android.R.id.content);

        viewModel = new ViewModelProvider(this).get(SharedPlaceViewModel.class);

        // Initialize the date and time library
        AndroidThreeTen.init(this);

        // Initialize Firebase and get currentUser
        FirebaseApp.initializeApp(this);
        firestoreUtils = new FirestoreUtils();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        //create a user in the database if it does not exist
        String currentUserUid = currentUser.getUid();
        firestoreUtils.checkAndCreateUser(currentUserUid, currentUser.getDisplayName(), null, null,  null, currentUser.getPhotoUrl().toString());

        setUpNavView();
        configureToolBar();
        setupAutoCompleteSearch();
        setupKeyboardListener();

        updateUserList();
        updateRestaurantList();

        openDrawerButton();
        configureDrawer();

        lunchButtonClick();
        settingsButtonClick();
        logoutButton();

        loadProfilePicture();
        loadProfileNameAndEmail();
    }

    private void setUpNavView(){
        // Set up the navigation view with the controller
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Add destination change listener to update fragment visibility flags
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_dashboard) {
                isSecondFragmentVisible = true;
                isThirdFragmentVisible = false;
            } else if (destination.getId() == R.id.navigation_notifications) {
                isSecondFragmentVisible = false;
                isThirdFragmentVisible = true;
            } else {
                isSecondFragmentVisible = false;
                isThirdFragmentVisible = false;
            }
            setupAutoCompleteSearch(); // Reconfigure the AutoCompleteTextView
        });
    }

    private void updateUserList(){
        // Retrieve all users from Firestore
        firestoreUtils.getAllUsers(new FirestoreUtils.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                // Update the user list in the ViewModel
                viewModel.setUserList(userList);

                // Set up listener for real-time updates
                listenForUsersUpdates();
            }

            @Override
            public void onError(Exception e) {
                Log.w("Firestore", "Error getting users.", e);

                // Set up listener for real-time updates even in case of initial error
                listenForUsersUpdates();
            }
        });
    }

    private void listenForUsersUpdates() {
        // Listen for real-time updates to the user list
        firestoreUtils.listenForUsersUpdates(new FirestoreUtils.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                // Update the user list in the ViewModel
                viewModel.setUserList(userList);
            }

            @Override
            public void onError(Exception e) {
                Log.w("Firestore", "Error getting users.", e);
            }
        });
    }

    private void updateRestaurantList() {
        // Retrieve all restaurants from Firestore
        firestoreUtils.getAllRestaurants(new FirestoreUtils.OnRestaurantsRetrievedListener() {
            @Override
            public void onRestaurantsRetrieved(List<Restaurant> restaurantList) {
                // Update the restaurant list in the ViewModel
                viewModel.setRestaurantList(restaurantList);
                // Set up listener for real-time updates
                listenForRestaurantUpdates();
            }

            @Override
            public void onError(Exception e) {
                Log.w("Firestore", "Error getting restaurants.", e);
                // Set up listener for real-time updates even in case of initial error
                listenForRestaurantUpdates();
            }
        });
    }

    private void listenForRestaurantUpdates() {
        // Listen for real-time updates to the restaurant list
        firestoreUtils.listenForRestaurantUpdates(new FirestoreUtils.OnRestaurantsRetrievedListener() {
            @Override
            public void onRestaurantsRetrieved(List<Restaurant> restaurantList) {
                // Update the restaurant list in the ViewModel
                viewModel.setRestaurantList(restaurantList);
            }

            @Override
            public void onError(Exception e) {
                Log.w("Firestore", "Error getting restaurants.", e);
            }
        });
    }

    private void configureToolBar(){
        // Configure the toolbar
        setSupportActionBar(binding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    private void setupAutoCompleteSearch() {
        // Set up the AutoCompleteTextView for search functionality
        autoCompleteSearch = binding.autoCompleteTextView;

        // Set up the search button to toggle the visibility of the AutoCompleteTextView
        binding.searchButton.setOnClickListener(v -> {
            if (autoCompleteSearch.getVisibility() == View.GONE) {
                autoCompleteSearch.setVisibility(View.VISIBLE);
                autoCompleteSearch.setText("");
                autoCompleteSearch.requestFocus();
                showKeyboard(autoCompleteSearch);
            } else {
                hideAutoCompleteSearch();
            }
        });

        // Observe the ViewModel to get the list of restaurant names
        viewModel.getPlaces().observe(this, places -> {
            List<String> placeNames = viewModel.getAllCustomPlaceName();
            ArrayAdapter<String> adapter;

            if (isSecondFragmentVisible || isThirdFragmentVisible) {
                // if the second or third fragment is displayed, disable autocomplete
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
            } else {
                // if the first fragment is displayed, prepare autocomplete
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, placeNames);
            }
            autoCompleteSearch.setAdapter(adapter);
        });

        // Handle item selection in the AutoCompleteTextView
        autoCompleteSearch.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedRestaurantName = (String) adapterView.getItemAtPosition(position);
            // when clicked, pass response to viewModel
            viewModel.setSelectedRestaurantName(selectedRestaurantName);
            Toast.makeText(this, "Selected: " + selectedRestaurantName, Toast.LENGTH_SHORT).show();
            hideAutoCompleteSearch();
        });

        // Handle the IME action done event
        autoCompleteSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String selectedRestaurantName = autoCompleteSearch.getText().toString();
                // when clicked, pass response to viewModel
                viewModel.setSelectedRestaurantName(selectedRestaurantName);
                Toast.makeText(this, "Selected: " + selectedRestaurantName, Toast.LENGTH_SHORT).show();
                hideAutoCompleteSearch();
                return true;
            }
            return false;
        });

        // Add a TextWatcher to detect text changes
        autoCompleteSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isThirdFragmentVisible) {
                    // Filter users by restaurant name for third fragment
                    viewModel.getUsersByRestaurantName(s.toString());
                } else {
                    // Filter customPlace by restaurant name for first and second fragments
                    viewModel.filterPlaces(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });


    }

    private void setupKeyboardListener() {
        // Set up a listener to detect when the keyboard is shown or hidden
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;
            boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15; // Arbitrary threshold
            if (isKeyboardVisible && !isKeyboardNowVisible) {
                hideAutoCompleteSearch();
            }
            isKeyboardVisible = isKeyboardNowVisible;
        });
    }

    public void hideAutoCompleteSearch() {
        // Hide the AutoCompleteTextView and clear its text
        if (autoCompleteSearch.getVisibility() == View.VISIBLE) {
            hideKeyboard();
            autoCompleteSearch.setVisibility(View.GONE);
            autoCompleteSearch.setText(""); // Vider le texte ici
            binding.searchButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideKeyboard() {
        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showKeyboard(View view) {
        // Show the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void openDrawerButton(){
        // Open the navigation drawer with the top left button
        binding.drawerButton.setOnClickListener(view -> {
            binding.drawerLayout.open();
        });
    }

    private void loadProfilePicture(){
        // Load the user's profile picture
        ImageView profilePictureImageView = binding.profilePictureImageView;
        if (currentUser.getPhotoUrl() != null){
            Uri photoUri = currentUser.getPhotoUrl();
            Glide.with(this)
                    .load(photoUri)
                    .into(profilePictureImageView);
        }
    }

    private void loadProfileNameAndEmail(){
        // Load the user's profile name and email
        if (currentUser.getDisplayName() != null){
            String name = currentUser.getDisplayName();
            String emailAddress = currentUser.getEmail();
            binding.profileName.setText(name);
            binding.textviewMail.setText(emailAddress);
        }
    }

    private void configureDrawer(){
        // Configure the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_drawer, R.string.close_drawer);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void lunchButtonClick(){
        // Set up the click action for the "your lunch" button
        binding.lunchButton.setOnClickListener(view -> {
            User user = viewModel.getUserById(currentUser.getUid());
            // if the user has chosen a restaurant, display it in the detailActivity
            if (user.getSelectedRestaurantId() != null){
                viewModel.setPlaceForDetailFragmentById(user.getSelectedRestaurantId());
                DetailFragment detailBottomSheetFragment = new DetailFragment();
                detailBottomSheetFragment.show(getSupportFragmentManager(), "DetailFragment");
            } else {
                // else display message
                Toast noRestaurantSelectedMessage = Toast.makeText(MainActivity.this, "vous n'avez pas encore choisi de restaurant", Toast.LENGTH_SHORT);
                noRestaurantSelectedMessage.show();
            }
        });
    }

    private void settingsButtonClick(){
        // Set up the click action for the settings button
        binding.settingButton.setOnClickListener(view -> {
            // TODO : ?? setting ??
        });
    }

    private void logoutButton(){
        // Set up the logout button action
        binding.logoutButton.setOnClickListener(view -> {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listeners to avoid memory leaks
        firestoreUtils.removeUsersListener();
        firestoreUtils.removeRestaurantListener();
    }
}