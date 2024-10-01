// User.java
package com.example.thesearch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String email;
    private String first_name;
    private String last_name;
    private String picture_url;
    private List<Spot> spots;
//    private List<Like> likes;
//    private List<Comment> comments;

    public User() {
       spots = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getPictureUrl() {
        return picture_url;
    }

    public void setPictureUrl(String picture_url) {
        this.picture_url = picture_url;
    }

    public List<Spot> getSpots() {
        return spots;
    }

    public int getNumberOfSpots() {
        return spots.size();
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
    }

//    public List<Like> getLikes() {
//        return likes;
//    }
//
//    public void setLikes(List<Like> likes) {
//        this.likes = likes;
//    }
//
//    public List<Comment> getComments() {
//        return comments;
//    }
//
//    public void setComments(List<Comment> comments) {
//        this.comments = comments;
//    }

    @Override
    public String toString() {
        return email;
    }
}
