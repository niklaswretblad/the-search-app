
package com.example.thesearch.model;

public class LoginRequest {
    private String id_token;

    public LoginRequest(String id_token) {
        this.id_token = id_token;
    }

    public String getIdToken() {
        return id_token;
    }

    public void setIdToken(String id_token) {
        this.id_token = id_token;
    }
}