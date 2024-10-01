package com.example.thesearch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thesearch.model.User;
import com.example.thesearch.repository.UserRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<List<User>> searchResults = new MutableLiveData<>();
    private final UserRepository userRepository;

    public SearchViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<List<User>> getSearchResults() {
        return searchResults;
    }

    public void searchUsers(String query) {
        userRepository.searchUsers(query, new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResults.postValue(response.body());
                } else {
                    // Handle the error case
                    searchResults.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Handle failure
                searchResults.postValue(null);
            }
        });
    }
}
