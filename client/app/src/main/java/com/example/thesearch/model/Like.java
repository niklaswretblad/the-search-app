package com.example.thesearch.model;

import java.io.Serializable;

public class Like implements Serializable {
    private int spot_id;
    private String user_id;

    public Like(int spotId, String userId) {
        this.spot_id = spotId;
        this.user_id = userId;
    }

    public int getSpotId() {
        return spot_id;
    }

    public void setSpotId(int spotId) {
        this.spot_id = spotId;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
