package com.example.myapplication.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// The DailyResetWorker class extends Worker to perform daily reset tasks in Firestore DataBase
// remove the "selectedRestaurantId", "selectedRestaurantId" and "userIdSelected" to clean the data for next day.
public class DailyResetWorker extends Worker {

    // Initialize the Firebase Firestore instance.
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Constructor for the DailyResetWorker class.
    public DailyResetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Reset user selections.
        resetUserSelections();
        // Reset restaurant selections.
        resetRestaurantSelections();
        // Return success result after completing the work.
        return Result.success();
    }

    // Method to reset user selections in the Firestore database.
    private void resetUserSelections() {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("selectedRestaurantName", null);
                    updates.put("selectedRestaurantId", null);
                    db.collection("users").document(document.getId()).update(updates);
                }
            }
        });
    }

    // Method to reset restaurant selections in the Firestore database.
    private void resetRestaurantSelections() {
        db.collection("restaurants").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("userIdSelected", null);
                    db.collection("restaurants").document(document.getId()).update(updates);
                }
            }
        });
    }
}
