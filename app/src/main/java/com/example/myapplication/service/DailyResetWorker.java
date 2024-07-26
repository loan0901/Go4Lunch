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

public class DailyResetWorker extends Worker {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DailyResetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        resetUserSelections();
        resetRestaurantSelections();
        return Result.success();
    }

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
