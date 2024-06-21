package com.example.myapplication.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.Model.User;
import com.example.myapplication.Repository.FirestoreRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel {

    private final FirestoreRepository firestoreRepository;

    private final MutableLiveData<List<User>> userList = new MutableLiveData<>();
    private final MutableLiveData<List<User>> filteredUser = new MutableLiveData<>();
    private final MutableLiveData<LatLng> currentUserLatLng = new MutableLiveData<>();

    public UserViewModel(FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    public void setUserList(List<User> userList) {this.userList.setValue(userList);}

    public LiveData<List<User>> getUserList() {return userList;}

    public void setFilteredUser(List<User> filteredUsers) {this.filteredUser.setValue(filteredUsers);}

    public LiveData<List<User>> getFilteredUsers() {return filteredUser;}

    public void setCurrentUserLatLng(LatLng latLng) {this.currentUserLatLng.setValue(latLng);}

    public LiveData<LatLng> getCurrentUserLatLng() {return currentUserLatLng;}

    // Check and create user in the dataBase
    public void checkAndCreateUser(String uid, String userName, String selectedRestaurantId, String selectedRestaurantName, List<String> favoriteRestaurants, String photoUrl) {
        firestoreRepository.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);
    }

    public void updateUserList(){
        // Retrieve all users from Firestore
        firestoreRepository.getAllUsers(new FirestoreRepository.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                // Update the user list in the ViewModel
                setUserList(userList);
                // Set up listener for real-time updates
                listenForUsersUpdates();
            }

            @Override
            public void onError(Exception e) {
                // Set up listener for real-time updates even in case of initial error
                listenForUsersUpdates();
                Log.w("Firestore", "Error getting users.", e);
            }
        });
    }

    public void listenForUsersUpdates() {
        // Listen for real-time updates to the user list
        firestoreRepository.listenForUsersUpdates(new FirestoreRepository.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                // Update the user list in the ViewModel
                setUserList(userList);
            }

            @Override
            public void onError(Exception e) {
                Log.w("Firestore", "Error getting users.", e);
            }
        });
    }

    public void removeListeners() {
        firestoreRepository.removeUsersListener();
    }

    // Method to get users by selected restaurant ID
    public List<User> getUsersBySelectedRestaurantId(String restaurantId) {
        List<User> allUsers = userList.getValue();
        List<User> filteredUsers = new ArrayList<>();
        if (allUsers != null) {
            for (User user : allUsers) {
                if (restaurantId.equals(user.getSelectedRestaurantId())) {
                    filteredUsers.add(user);
                }
            }
        }
        return filteredUsers;
    }

    // Method to get users by restaurant name
    public void getUsersByRestaurantName(String restaurantName) {
        List<User> allUsers = userList.getValue();
        List<User> filteredUsers = new ArrayList<>();
        if (allUsers != null) {
            if (restaurantName.isEmpty()) {
                filteredUsers.addAll(allUsers);
            } else {
                for (User user : allUsers) {
                    if (user.getSelectedRestaurantName() != null && user.getSelectedRestaurantName().toLowerCase().contains(restaurantName.toLowerCase())) {
                        filteredUsers.add(user);
                    }
                }
            }
        }
        setFilteredUser(filteredUsers);
    }

    // Method to get a user by their ID
    public User getUserById(String userUid){
        List<User> allUsers = userList.getValue();
        if (allUsers != null){
            for (User user : allUsers){
                if (user.getUserId().equals(userUid)){
                    return user;
                }
            }
        }
        return null; //user not found
    }
}
