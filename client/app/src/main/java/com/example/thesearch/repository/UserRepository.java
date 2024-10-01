package com.example.thesearch.repository;

import android.content.Context;

import com.example.thesearch.api.ApiClient;
import com.example.thesearch.api.ApiService;
import com.example.thesearch.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiService apiService;

    public UserRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void loginUser(LoginRequest request, Callback<LoginResponse> callback) {
        Call<LoginResponse> call = apiService.loginUser(request);
        call.enqueue(callback);
    }

    public void logout(Callback<Void> callback) {
        Call<Void> call = apiService.logout(); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void getUserSpots(String userId, Callback<List<Spot>> callback) {
        Call<List<Spot>> call = apiService.getUserSpots(userId); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void searchUsers(String query, Callback<List<User>> callback) {
        apiService.searchUsers(query).enqueue(callback);
    }

    public void createSpot(SpotRequest request, Callback<SpotResponse> callback) {
        Call<SpotResponse> call = apiService.createSpot(request); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void getSpot(int spotId, Callback<Spot> callback) {
        Call<Spot> call = apiService.getSpot(spotId); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void updateSpot(int spotId, SpotRequest request, Callback<Void> callback) {
        Call<Void> call = apiService.updateSpot(spotId, request);
        call.enqueue(callback);
    }

    public void removeSpot(int spotId, Callback<Void> callback) {
        Call<Void> call = apiService.removeSpot(spotId); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void getUserAndFollowedSpots(String userId, Callback<List<Spot>> callback) {
        Call<List<Spot>> call = apiService.getUserAndFollowedSpots(userId); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void likeSpot(LikeRequest request, Callback<LikeResponse> callback) {
        Call<LikeResponse> call = apiService.likeSpot(request); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void unlikeSpot(UnlikeRequest request, Callback<Void> callback) {
        Call<Void> call = apiService.unlikeSpot(request); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void getNumberOfLikes(int spotId, Callback<LikeCountResponse> callback) {
        Call<LikeCountResponse> call = apiService.getNumberOfLikes(spotId); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void commentSpot(int spotId, CommentRequest request, Callback<CommentResponse> callback) {
        Call<CommentResponse> call = apiService.commentSpot(spotId, request); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void removeComment(int commentId, Callback<Void> callback) {
        Call<Void> call = apiService.removeComment(commentId); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void getSpotComments(int spotId, Callback<List<Comment>> callback) {
        Call<List<Comment>> call = apiService.getSpotComments(spotId); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void followUser(FollowRequest request, Callback<Void> callback) {
        Call<Void> call = apiService.followUser(request); // Token added automatically by AuthInterceptor
        call.enqueue(callback);
    }

    public void unfollowUser(String userId, String targetId, Callback<Void> callback) {
        Call<Void> call = apiService.unfollowUser(userId, targetId);
        call.enqueue(callback);
    }

    public void checkIfFollowing(String userId, String targetId, Callback<FollowCheckResponse> callback) {
        Call<FollowCheckResponse> call = apiService.checkIfFollowing(userId, targetId);
        call.enqueue(callback);
    }

    public void followUser(String userId, String targetId, Callback<Void> callback) {
        FollowRequest request = new FollowRequest(userId, targetId);
        Call<Void> call = apiService.followUser(request);
        call.enqueue(callback);
    }

    public void getFollowedSpots(String userId, Callback<List<Spot>> callback) {
        Call<List<Spot>> call = apiService.getFollowedSpots(userId);
        call.enqueue(callback);
    }

}
