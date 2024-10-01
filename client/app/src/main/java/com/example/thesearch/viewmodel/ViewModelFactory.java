package com.example.thesearch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.thesearch.repository.UserRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;

    public ViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SpotViewModel.class)) {
            return (T) new SpotViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(FollowViewModel.class)) {
            return (T) new FollowViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(CommentViewModel.class)) {
            return (T) new CommentViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
