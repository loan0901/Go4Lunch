package com.example.myapplication.service;

import android.util.Log;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreUtils {

    private final FirebaseFirestore db;
    private ListenerRegistration userListenerRegistration;
    private ListenerRegistration restaurantListenerRegistration;

    // Constructor for FirestoreUtils. Initializes Firestore instance.
    public FirestoreUtils() {
        db = FirebaseFirestore.getInstance();
    }

    // Check if the user exists, otherwise create a new user
    public void checkAndCreateUser(String uid, String userName, String selectedRestaurantId, String selectedRestaurantName, List<String> favoriteRestaurants, String photoUrl) {
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "User already exists");
                    // User exists, handle existing user if needed
                } else {
                    Log.d("Firestore", "User does not exist, creating user");
                    createUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);
                    // User doesn't exists, call methode createUser
                }
            } else {
                Log.w("Firestore", "Error checking user existence", task.getException());
            }
        });
    }

    // Create a new user in Firestore
    private void createUser(String uid,String userName, String selectedRestaurantId, String selectedRestaurantName, List<String> favoriteRestaurants, String photoUrl) {
        // Create a map to hold the user's data
        Map<String, Object> user = new HashMap<>();
        user.put("selectedRestaurantId", selectedRestaurantId);
        user.put("selectedRestaurantName", selectedRestaurantName);
        user.put("favoriteRestaurants", favoriteRestaurants);
        user.put("photoUrl", photoUrl);
        user.put("userName", userName);

        // Add the user to the Firestore collection
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User created successfully"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error creating user", e));
    }


    public interface OnUsersRetrievedListener {
        void onUsersRetrieved(List<User> userList);
        void onError(Exception e);
    }

    // Retrieve all users from Firestore
    public void getAllUsers(OnUsersRetrievedListener listener) {
        CollectionReference usersRef = db.collection("users");

        usersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> userList = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                User user = document.toObject(User.class);
                                user.setUserId(document.getId());
                                userList.add(user);
                            }
                        }
                        listener.onUsersRetrieved(userList);
                    } else {
                        Log.w("Firestore", "Error getting users.", task.getException());
                        listener.onError(task.getException());
                    }
                });
    }

    // Listen for updates to the users collection
    public void listenForUsersUpdates(OnUsersRetrievedListener listener) {
        CollectionReference usersRef = db.collection("users");
        userListenerRegistration = usersRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                listener.onError(e);
                return;
            }

            if (querySnapshot != null) {
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    User user = document.toObject(User.class);
                    user.setUserId(document.getId());
                    userList.add(user);
                }
                listener.onUsersRetrieved(userList);
            }
        });
    }

    // Remove the listener for updates to the users collection
    public void removeUsersListener() {
        if (userListenerRegistration != null) {
            userListenerRegistration.remove();
        }
    }

    // Check if a restaurant exists, otherwise create a new restaurant
    public void checkOrCreateRestaurant(String restaurantId, int likeCount, List<String> userIdSelected) {
        DocumentReference restaurantRef = db.collection("restaurants").document(restaurantId);

        restaurantRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "Restaurant already exists, no action taken");
                } else {
                    Log.d("Firestore", "Restaurant does not exist, creating restaurant");
                    createRestaurant(restaurantId, likeCount, userIdSelected);
                }
            } else {
                Log.w("Firestore", "Error checking restaurant existence", task.getException());
            }
        });
    }

    // Create a new restaurant in Firestore
    private void createRestaurant(String restaurantId, int likeCount, List<String> userIdSelected) {
        Map<String, Object> restaurant = new HashMap<>();
        restaurant.put("likeCount", likeCount);
        restaurant.put("userIdSelected", userIdSelected);

        db.collection("restaurants").document(restaurantId)
                .set(restaurant)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Restaurant created successfully"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error creating restaurant", e));
    }

    public interface OnRestaurantsRetrievedListener {
        void onRestaurantsRetrieved(List<Restaurant> restaurantList);
        void onError(Exception e);
    }

    // Retrieve all restaurants from Firestore
    public void getAllRestaurants(OnRestaurantsRetrievedListener listener) {
        CollectionReference restaurantsRef = db.collection("restaurants");

        restaurantsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurantList = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurant.setRestaurantId(document.getId());
                                restaurantList.add(restaurant);
                            }
                        }
                        listener.onRestaurantsRetrieved(restaurantList);
                    } else {
                        Log.w("Firestore", "Error getting restaurants.", task.getException());
                        listener.onError(task.getException());
                    }
                });
    }

    // Listen for updates to the restaurants collection
    public void listenForRestaurantUpdates(OnRestaurantsRetrievedListener listener) {
        CollectionReference restaurantsRef = db.collection("restaurants");
        restaurantListenerRegistration = restaurantsRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                listener.onError(e);
                return;
            }

            if (querySnapshot != null) {
                List<Restaurant> restaurantList = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Restaurant restaurant = document.toObject(Restaurant.class);
                    restaurant.setRestaurantId(document.getId());
                    restaurantList.add(restaurant);
                }
                listener.onRestaurantsRetrieved(restaurantList);
            }
        });
    }

    // Remove the listener for updates to the restaurants collection
    public void removeRestaurantListener() {
        if (restaurantListenerRegistration != null) {
            restaurantListenerRegistration.remove();
        }
    }

    // Toggle the favorite status of a restaurant for a user
    public void toggleFavoriteRestaurant(String uid, String restaurantId) {
        DocumentReference userRef = db.collection("users").document(uid);
        DocumentReference restaurantRef = db.collection("restaurants").document(restaurantId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> favoriteRestaurants = (List<String>) document.get("favoriteRestaurants");
                    if (favoriteRestaurants != null && favoriteRestaurants.contains(restaurantId)) {
                        // If the restaurant is already in favorites, remove it
                        userRef.update("favoriteRestaurants", FieldValue.arrayRemove(restaurantId))
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Restaurant removed from favorites"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error removing from favorites", e));

                        // Decrement the like count of the restaurant
                        updateLikeCount(restaurantRef, -1);
                    } else {
                        // If the restaurant is not in favorites, add it
                        userRef.update("favoriteRestaurants", FieldValue.arrayUnion(restaurantId))
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Restaurant added to favorites"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error adding to favorites", e));

                        // Increment the like count of the restaurant
                        updateLikeCount(restaurantRef, 1);
                    }
                } else {
                    Log.d("Firestore", "User document does not exist");
                }
            } else {
                Log.w("Firestore", "Error checking user document", task.getException());
            }
        });
    }

    // Update the like count of a restaurant
    private void updateLikeCount(DocumentReference restaurantRef, int increment) {
        restaurantRef.update("likeCount", FieldValue.increment(increment))
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Restaurant like count updated successfully"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating restaurant like count", e));
    }

    // Update the selected restaurant for a user
    public void updateSelectedRestaurant(String uid, String newRestaurantId, String restaurantName) {
        DocumentReference userRef = db.collection("users").document(uid);

        db.runTransaction(transaction -> {
            // Get the user document to obtain the old selected restaurant
            DocumentSnapshot userSnapshot = transaction.get(userRef);
            String oldRestaurantId = userSnapshot.getString("selectedRestaurantId");

            if (oldRestaurantId != null && oldRestaurantId.equals(newRestaurantId)) {
                // If the user clicks on the same restaurant again, deselect it
                transaction.update(userRef, "selectedRestaurantId", null);
                transaction.update(userRef, "selectedRestaurantName", null);
                DocumentReference oldRestaurantRef = db.collection("restaurants").document(oldRestaurantId);
                transaction.update(oldRestaurantRef, "userIdSelected", FieldValue.arrayRemove(uid));
            } else {
                // Update the user's selected restaurant with the new ID and name
                transaction.update(userRef, "selectedRestaurantId", newRestaurantId);
                transaction.update(userRef, "selectedRestaurantName", restaurantName);

                // Add the user's UID to the userIdSelected list of the new restaurant
                DocumentReference newRestaurantRef = db.collection("restaurants").document(newRestaurantId);
                transaction.update(newRestaurantRef, "userIdSelected", FieldValue.arrayUnion(uid));

                // If an old restaurant was selected, remove the user's UID from its userIdSelected list
                if (oldRestaurantId != null) {
                    DocumentReference oldRestaurantRef = db.collection("restaurants").document(oldRestaurantId);
                    transaction.update(oldRestaurantRef, "userIdSelected", FieldValue.arrayRemove(uid));
                }
            }

            return null;
        });
    }
}
