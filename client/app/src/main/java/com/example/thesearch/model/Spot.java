
package com.example.thesearch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Spot implements Serializable {
    private int id;
    private String creator;
    private double latitude;
    private double longitude;
    private String name;
    private String description;
    private int quality_rating;
    private int difficulty_rating;
    private List<Like> likes;
    private List<Comment> comments;

    public Spot() {
        likes = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQualityRating() {
        return quality_rating;
    }

    public void setQualityRating(int quality_rating) {
        this.quality_rating = quality_rating;
    }

    public int getDifficultyRating() {
        return difficulty_rating;
    }

    public void setDifficultyRating(int difficulty_rating) {
        this.difficulty_rating = difficulty_rating;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

}