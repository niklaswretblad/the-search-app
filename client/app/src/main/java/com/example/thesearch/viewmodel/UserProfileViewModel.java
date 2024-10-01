package com.example.thesearch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thesearch.model.FollowCheckResponse;
import com.example.thesearch.model.User;
import com.example.thesearch.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> followStatus = new MutableLiveData<>();


    public UserProfileViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<User> getUserProfile() {
        return userProfile;
    }

    public LiveData<Boolean> getFollowStatus() {
        return followStatus;
    }

    public void checkIfFollowing(String userId, String targetId) {
        userRepository.checkIfFollowing(userId, targetId, new Callback<FollowCheckResponse>() {
            @Override
            public void onResponse(Call<FollowCheckResponse> call, Response<FollowCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    followStatus.postValue(response.body().isFollowing());
                } else {
                    followStatus.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<FollowCheckResponse> call, Throwable t) {
                followStatus.postValue(false);
            }
        });
    }

    public void toggleFollowUser(String userId, String targetId) {
        Boolean isFollowing = followStatus.getValue();

        if (isFollowing != null && isFollowing) {
            userRepository.unfollowUser(userId, targetId, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        followStatus.postValue(false);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Handle failure
                }
            });
        } else {
            userRepository.followUser(userId, targetId, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        followStatus.postValue(true);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Handle failure
                }
            });
        }
    }


    public void setUserProfile(User user) {
        userProfile.postValue(user);
    }
}
