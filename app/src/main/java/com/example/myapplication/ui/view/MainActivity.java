package com.example.myapplication.ui.view;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.CustomPlace;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.service.AlarmReceiver;
import com.example.myapplication.viewModel.GooglePlaceViewModel;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.viewModel.RestaurantViewModel;
import com.example.myapplication.viewModel.UserViewModel;
import com.example.myapplication.viewModel.ViewModelFactory.GooglePlaceViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.RestaurantViewModelFactory;
import com.example.myapplication.viewModel.ViewModelFactory.UserViewModelFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_NOTIFICATION_PERMISSION_REQUESTED = "notificationPermissionRequested";
    private static final String KEY_NOTIFICATION_PERMISSION_REQUEST_COUNT = "notificationPermissionRequestCount";
    private static final int MAX_PERMISSION_REQUESTS = 2;

    private NotificationPermissionListener notificationPermissionListener;

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private View rootView;
    private AutoCompleteTextView autoCompleteSearch;
    private boolean isKeyboardVisible = false;
    private boolean isSecondFragmentVisible = false;
    private boolean isThirdFragmentVisible = false;

    private GooglePlaceViewModel googlePlaceViewModel;
    private RestaurantViewModel restaurantViewModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rootView = findViewById(android.R.id.content);

        // Initialize SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load the saved state of the switch
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isEnabled = sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, false);
        binding.switchNotifications.setChecked(isEnabled);

        // Initialize the repository
        GooglePlacesRepository googlePlacesRepository = new GooglePlacesRepository(RetrofitClient.getApiService());
        FirestoreRepository firestoreRepository = new FirestoreRepository();

        // Initialize the ViewModel with Factory
        GooglePlaceViewModelFactory factory = new GooglePlaceViewModelFactory(googlePlacesRepository);
        RestaurantViewModelFactory restaurantViewModelFactory = new RestaurantViewModelFactory(firestoreRepository);
        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(firestoreRepository);

        googlePlaceViewModel = new ViewModelProvider(this, factory).get(GooglePlaceViewModel.class);
        restaurantViewModel = new ViewModelProvider(this, restaurantViewModelFactory).get(RestaurantViewModel.class);
        userViewModel = new ViewModelProvider(this, userViewModelFactory).get(UserViewModel.class);

        // Initialize the date and time library
        AndroidThreeTen.init(this);

        // Initialize Firebase and get currentUser
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Check if the currentUser is null
        if (currentUser == null) {
            // Redirect to LoginActivity if user is not logged in
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Finish this activity so the user can't navigate back to it
            return; // Exit onCreate method to avoid executing the following code
        }
        //create a user in the database if it does not exist
        String currentUserUid = currentUser.getUid();
        userViewModel.checkAndCreateUser(currentUserUid, currentUser.getDisplayName(), null, null,  null, currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null);

        //get all the current User & Restaurant from the dataBase
        userViewModel.updateUserList();
        restaurantViewModel.updateRestaurantList();

        setUpNavView();
        configureToolBar();
        setupAutoCompleteSearch();
        setupKeyboardListener();

        openDrawerButton();
        configureDrawer();

        lunchButtonClick();
        notificationSwitchButtonClick();
        logoutButton();

        loadProfilePicture();
        loadProfileNameAndEmail();

        askNotificationPermissionOnCreate();
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
            if (autoCompleteSearch.getVisibility() == View.INVISIBLE) {
                autoCompleteSearch.setVisibility(View.VISIBLE);
                autoCompleteSearch.setText("");
                autoCompleteSearch.requestFocus();
                showKeyboard(autoCompleteSearch);
            } else {
                hideAutoCompleteSearch();
            }
        });

        // Observe the ViewModel to get the list of restaurant names
        googlePlaceViewModel.getPlaces().observe(this, places -> {
            List<String> placeNames = googlePlaceViewModel.getAllCustomPlaceName();
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
            restaurantViewModel.setSelectedRestaurantName(selectedRestaurantName);
            hideAutoCompleteSearch();
        });

        // Handle the IME action done event
        autoCompleteSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String selectedRestaurantName = autoCompleteSearch.getText().toString();
                // when clicked, pass response to viewModel
                restaurantViewModel.setSelectedRestaurantName(selectedRestaurantName);
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
                    userViewModel.getUsersByRestaurantName(s.toString());
                } else {
                    // Filter customPlace by restaurant name for first and second fragments
                    googlePlaceViewModel.filterPlaces(s.toString());
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
            autoCompleteSearch.setVisibility(View.INVISIBLE);
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
        } else {
            profilePictureImageView.setImageResource(R.drawable.baseline_account_circle_24);
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
            //check if the user has chosen a restaurant
            User user = userViewModel.getUserById(currentUser.getUid());
            String selectedRestaurantId = user.getSelectedRestaurantId();
            if (user.getSelectedRestaurantId() != null){
                //check if its already in the viewModel, open DetailActivity
                if (googlePlaceViewModel.checkIfPlaceListContainsRestaurant(selectedRestaurantId)) {
                    openDetailActivity(selectedRestaurantId);
                } else {
                    // otherwise, load the place, add it to the viewModel and open DetailActivity
                    String apiKey = getString(R.string.google_maps_key);
                    String fieldMask = "id,displayName,formattedAddress,location,rating,regularOpeningHours,photos,internationalPhoneNumber,websiteUri";
                    googlePlaceViewModel.getPlaceDetails(selectedRestaurantId, apiKey, fieldMask, new GooglePlaceViewModel.PlaceDetailsCallback() {
                        @Override
                        public void onPlaceDetailsLoaded(CustomPlace placeDetails) {
                            openDetailActivity(selectedRestaurantId);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(MainActivity.this, R.string.error_retrieving_restaurant, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                // if no restaurant is selected, notify the user
                Toast noRestaurantSelectedMessage = Toast.makeText(MainActivity.this, R.string.no_restaurant_chosen, Toast.LENGTH_SHORT);
                noRestaurantSelectedMessage.show();
            }
        });
    }

    // open the DetailActivity with the restaurantId
    private void openDetailActivity(String restaurantId){
        googlePlaceViewModel.setPlaceForDetailFragmentById(restaurantId);
        DetailFragment detailBottomSheetFragment = new DetailFragment();
        detailBottomSheetFragment.show(getSupportFragmentManager(), "DetailFragment");
    }

    private void notificationSwitchButtonClick(){
        // Set up the click action for the settings button
        binding.switchNotifications.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            // Save the state in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(NOTIFICATIONS_ENABLED, isChecked);
            editor.apply();
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    // Enable notifications
                    setDailyNotification();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }

            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    // Disable notifications
                    disableNotifications();
                }
            }
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

    // Declare and initialize an ActivityResultLauncher to handle permission requests.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Get the shared preferences to store and retrieve permission data.
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

                // If the permission is granted, enable the notifications switch and Notify the end of the permission request (for the following request).
                if (isGranted) {
                    binding.switchNotifications.setChecked(true);
                    notifyRequestEnd();
                } else {
                    // If the permission is denied, retrieve the number of times the permission has been requested and increment.
                    int requestCount = preferences.getInt(KEY_NOTIFICATION_PERMISSION_REQUEST_COUNT, 0);
                    preferences.edit().putInt(KEY_NOTIFICATION_PERMISSION_REQUEST_COUNT, requestCount + 1).apply();
                    preferences.edit().putBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, true).apply();
                    // Disable the notifications switch.
                    binding.switchNotifications.setChecked(false);

                    if (requestCount + 1 >= MAX_PERMISSION_REQUESTS) {
                        // If the maximum request count is reached, show a dialog to guide the user to the settings.
                        showSettingsDialog();
                    } else {
                        // Otherwise, notify the end of the permission request (for the following request).
                        notifyRequestEnd();
                    }
                }
            });

    // ask notification permission if it's the first time
    private void askNotificationPermissionOnCreate() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean permissionRequested = preferences.getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false);

        if (!permissionRequested) {
            // First time asking for permission
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            notifyRequestEnd();
        }
    }

    public interface NotificationPermissionListener {
        void onNotificationPermissionFinish();
    }

    public void setNotificationPermissionListener(NotificationPermissionListener listener) {
        this.notificationPermissionListener = listener;
    }

    // notify request end to launch the next authorization request
    private void notifyRequestEnd(){
        if (notificationPermissionListener != null) {
            notificationPermissionListener.onNotificationPermissionFinish();
        }
    }

    // Show settings dialog to enable location permission
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_notification_setting)
                .setTitle(R.string.permission_require)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton(R.string.settings, (dialogInterface, i) -> {
                    openAppSettings();
                    dialogInterface.dismiss();
                });
        builder.show();
    }

    // Open app settings to enable location permission
    private void openAppSettings() {
        Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        settingIntent.setData(uri);
        startActivity(settingIntent);
    }

    // set daily notification
    private void setDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Configure the alarm to trigger at 12:00 PM
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);

        // If the current time is past 12:00 PM, set the alarm for the next day
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    // disable notification
    private void disableNotifications() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        Log.d("disableNotifications", "Notifications disabled");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listeners to avoid memory leaks
        userViewModel.removeListeners();
        restaurantViewModel.removeListeners();
    }
}
