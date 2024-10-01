
package com.example.thesearch.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.thesearch.model.*;
import com.example.thesearch.repository.UserRepository;

import java.util.List;

import retrofit2.Callback;

public class CommentViewModel extends ViewModel {
    private UserRepository userRepository;

    public CommentViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void commentSpot(int spotId, CommentRequest request, Callback<CommentResponse> callback) {
        userRepository.commentSpot(spotId, request, callback);
    }

    public void removeComment(int commentId, String token, Callback<Void> callback) {
        userRepository.removeComment(commentId, callback);
    }

    public void getSpotComments(int spotId, String token, Callback<List<Comment>> callback) {
        userRepository.getSpotComments(spotId, callback);
    }
}