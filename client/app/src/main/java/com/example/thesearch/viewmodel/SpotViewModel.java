
package com.example.thesearch.viewmodel;

import static java.util.Collections.addAll;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thesearch.model.*;
import com.example.thesearch.repository.UserRepository;

import java.util.List;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpotViewModel extends ViewModel {
    private final String TAG = "SpotViewModel";
    private final UserRepository userRepository;
    private final MutableLiveData<List<Spot>> spotsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Spot>> followedSpotsLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<Resource<SpotResponse>> spotCreationStatus = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<SpotResponse>> spotEditStatus = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<SpotResponse>> spotRemovalStatus = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> likeToggleStatus = new SingleLiveEvent<>();
    private final MutableLiveData<List<Comment>> commentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Like>> likesLiveData = new MutableLiveData<>();

    public SpotViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;

        spotsLiveData.setValue(new ArrayList<>());
        followedSpotsLiveData.setValue(new ArrayList<>());
        spotCreationStatus.setValue(Resource.loading(null));
        spotEditStatus.setValue(Resource.loading(null));
        spotRemovalStatus.setValue(Resource.loading(null));
        commentsLiveData.setValue(new ArrayList<>());
        likesLiveData.setValue(new ArrayList<>());
    }

    public LiveData<List<Spot>> getSpotsLiveData() {
        return spotsLiveData;
    }

    public LiveData<List<Spot>> getFollowedSpotsLiveData() { return followedSpotsLiveData; }

    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public SingleLiveEvent<Resource<SpotResponse>> getSpotCreationStatus() {
        return spotCreationStatus;
    }

    public SingleLiveEvent<Resource<SpotResponse>> getSpotEditStatus() {
        return spotEditStatus;
    }

    public SingleLiveEvent<Resource<SpotResponse>> getSpotRemovalStatus() {
        return spotRemovalStatus;
    }

    public SingleLiveEvent<Boolean> getLikeToggleStatus() {
        return likeToggleStatus;
    }

    public void createSpot(Spot spot) {
        SpotRequest spotRequest = new SpotRequest(spot);
        userRepository.createSpot(spotRequest, new Callback<SpotResponse>() {
            @Override
            public void onResponse(Call<SpotResponse> call, Response<SpotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    spotCreationStatus.postValue(Resource.success(response.body()));
                    List<Spot> currentSpots = spotsLiveData.getValue();
                    List<Spot> updatedSpots = new ArrayList<>(currentSpots);
                    spot.setId(response.body().getId());
                    updatedSpots.add(spot);
                    spotsLiveData.postValue(updatedSpots);
                    Log.d(TAG, "Updated spotsLiveData with new spot, total spots: " + updatedSpots.size());
                } else {
                    spotCreationStatus.postValue(Resource.error("Spot creation failed"));
                }
            }

            @Override
            public void onFailure(Call<SpotResponse> call, Throwable t) {
                spotCreationStatus.postValue(Resource.error("Spot creation failed: " + t.getMessage()));
            }
        });
    }

    public void removeSpot(int spotId) {
        userRepository.removeSpot(spotId, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removeSpotFromLiveData(spotId);
                    spotRemovalStatus.postValue(Resource.success(null));
                } else {
                    spotRemovalStatus.postValue(Resource.error("Spot creation failed"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                spotRemovalStatus.postValue(Resource.error("Failed to remove spot: " + t.getMessage()));
            }
        });
    }

    public void updateSpot(Spot spot) {
        SpotRequest spotRequest = new SpotRequest(spot);
        userRepository.updateSpot(spot.getId(), spotRequest, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    updateSpotInLiveData(spot.getId(), spot);
                    spotEditStatus.postValue(Resource.success(null));
                } else {
                    spotEditStatus.postValue(Resource.error("Edit spot failed"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                spotEditStatus.postValue(Resource.error("Failed to update spot: " + t.getMessage()));
            }
        });
    }

    public void toggleLikeSpot(String userId, int spotId, boolean isLiked) {
        if (isLiked) {
            unlikeSpot(userId, spotId);
        } else {
            likeSpot(userId, spotId);
        }
    }

    public void likeSpot(String userId, int spotId) {
        LikeRequest likeRequest = new LikeRequest(userId, spotId);
        userRepository.likeSpot(likeRequest, new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if (response.isSuccessful()) {
                    likeToggleStatus.setValue(true);
                    Like like = new Like(spotId, userId);
                    updateSpotLikes(spotId, like, true);
                } else {
                    likeToggleStatus.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                likeToggleStatus.setValue(false);
            }
        });
    }

    private void unlikeSpot(String userId, int spotId) {
        userRepository.unlikeSpot(new UnlikeRequest(userId, spotId), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    likeToggleStatus.setValue(false);
                    Like like = new Like(spotId, userId);
                    updateSpotLikes(spotId, like, false);
                } else {
                    likeToggleStatus.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                likeToggleStatus.setValue(true);
            }
        });
    }

    public void loadSpots() {
        // Load spots from repository or network
        userRepository.getUserSpots(UserManager.getInstance().getUserId(), new Callback<List<Spot>>() {
            @Override
            public void onResponse(Call<List<Spot>> call, Response<List<Spot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    spotsLiveData.postValue(response.body());
                } else {
                    Log.e(TAG, "Failed to load spots: " + response.message());
                    Log.e(TAG, "Error code: " + response.code());
                    Log.e(TAG, "Error body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Spot>> call, Throwable t) {
                Log.e(TAG, "loadSpots() Failed to load spots", t);
            }
        });
    }

    public void loadFollowedSpots() {
        String userId = UserManager.getInstance().getUserId();
        userRepository.getFollowedSpots(userId, new Callback<List<Spot>>() {
            @Override
            public void onResponse(Call<List<Spot>> call, Response<List<Spot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    followedSpotsLiveData.postValue(response.body());
                } else {
                    Log.e(TAG, "Failed to load follow spots: " + response.message());
                    Log.e(TAG, "Error code: " + response.code());
                    Log.e(TAG, "Error body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Spot>> call, Throwable t) {
                Log.e(TAG, "loadFollowedSpots() Failed to load spots", t);
            }
        });
    }


    private void updateSpotInLiveData(int spotId, Spot updatedSpot) {
        List<Spot> currentSpots = spotsLiveData.getValue();
        if (currentSpots != null) {
            for (int i = 0; i < currentSpots.size(); i++) {
                Spot spot = currentSpots.get(i);
                if (spot.getId() == spotId) {
                    currentSpots.set(i, updatedSpot);
                    spotsLiveData.postValue(currentSpots);
                    break;
                }
            }
        }
    }

    private void removeSpotFromLiveData(int spotId) {
        List<Spot> currentSpots = spotsLiveData.getValue();
        Log.i(TAG, String.format("removeSpotsFromLiveData() Length before removal: %d", currentSpots.size()));

        for (int i = 0; i < currentSpots.size(); i++) {
            if (currentSpots.get(i).getId() == spotId) {
                currentSpots.remove(i);
                spotsLiveData.postValue(currentSpots);
                Log.i(TAG, String.format("removeSpotsFromLiveData() Length after removal: %d", spotsLiveData.getValue().size()));
                break;
            }
        }
    }

    private void updateSpotLikes(int spotId, Like like, boolean isLike) {
        List<Spot> currentSpots = spotsLiveData.getValue();
        if (currentSpots != null) {
            for (Spot spot : currentSpots) {
                if (spot.getId() == spotId) {
                    List<Like> likes = spot.getLikes();

                    if (isLike) {
                        likes.add(like);
                    } else {
                        for (int i = 0; i < likes.size(); i++) {
                            Like existingLike = likes.get(i);
                            if (existingLike.getUserId().equals(like.getUserId())) {
                                likes.remove(i);
                                break;
                            }
                        }
                    }

                    spotsLiveData.postValue(currentSpots);
                    break;
                }
            }
        }
    }
}
