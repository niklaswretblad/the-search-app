
package com.example.thesearch.model;

public class UnfollowRequest {
    private String user_id;
    private String target_id;

    public UnfollowRequest(String userId, String targetId) {
        this.user_id = userId;
        this.target_id = targetId;
    }

    public String getUserId() {
        return user_id;
    }

    public String getTargetId() {
        return target_id;
    }
}