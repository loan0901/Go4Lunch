package com.example.myapplication.viewModel.ViewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.viewModel.UserViewModel;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final FirestoreRepository firestoreRepository;

    public UserViewModelFactory(FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(firestoreRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
