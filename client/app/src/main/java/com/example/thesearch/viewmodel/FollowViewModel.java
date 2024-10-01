
package com.example.thesearch.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.thesearch.model.*;
import com.example.thesearch.repository.UserRepository;

import retrofit2.Callback;

public class FollowViewModel extends ViewModel {
    private UserRepository userRepository;

    public FollowViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



}