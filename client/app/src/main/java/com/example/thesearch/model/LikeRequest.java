
package com.example.thesearch.model;

public class LikeRequest {
    private String user_id;
    private int spot_id;

    public LikeRequest(String user_id, int spot_id) {
        this.user_id = user_id;
        this.spot_id = spot_id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public int getSpotId() {
        return spot_id;
    }

    public void setSpotId(int spot_id) {
        this.spot_id = spot_id;
    }
}
