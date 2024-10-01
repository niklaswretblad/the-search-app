
package com.example.thesearch.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.thesearch.repository.UserRepository;


public class UserViewModel extends ViewModel {
    private UserRepository userRepository;

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}