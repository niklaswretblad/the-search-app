package com.example.thesearch.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.thesearch.repository.UserRepository;


public class MapViewModel extends ViewModel {

    private final UserRepository userRepository;

    public MapViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
