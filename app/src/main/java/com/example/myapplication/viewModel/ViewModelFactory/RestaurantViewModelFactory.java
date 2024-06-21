package com.example.myapplication.viewModel.ViewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.viewModel.RestaurantViewModel;

public class RestaurantViewModelFactory implements ViewModelProvider.Factory {
    private final FirestoreRepository firestoreRepository;

    public RestaurantViewModelFactory(FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)) {
            return (T) new RestaurantViewModel(firestoreRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
