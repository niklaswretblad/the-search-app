package com.example.thesearch.model;

import android.util.Log;

public class UserManager {
    private final String TAG = "UserManager";

    private static UserManager instance;
    private User user;
    private String authToken;

    private UserManager() {
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public String getUserId() {
        return user != null ? user.getId() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public User getUser() {
        return user;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void saveUser(String token, User user) {
        this.user = user;
        this.authToken = token;

        Log.d(TAG, "User data and auth token saved");
    }

    public void clearUserData() {
        this.user = null;
        this.authToken = null;
        Log.d(TAG, "User data and auth token cleared");
    }
}
