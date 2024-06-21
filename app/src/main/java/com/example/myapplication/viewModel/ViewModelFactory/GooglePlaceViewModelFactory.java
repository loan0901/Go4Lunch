package com.example.myapplication.viewModel.ViewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.Repository.GooglePlacesRepository;
import com.example.myapplication.viewModel.GooglePlaceViewModel;

public class GooglePlaceViewModelFactory implements ViewModelProvider.Factory {
    private final GooglePlacesRepository googlePlacesRepository;

    public GooglePlaceViewModelFactory(GooglePlacesRepository googlePlacesRepository) {
        this.googlePlacesRepository = googlePlacesRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GooglePlaceViewModel.class)) {
            return (T) new GooglePlaceViewModel(googlePlacesRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}