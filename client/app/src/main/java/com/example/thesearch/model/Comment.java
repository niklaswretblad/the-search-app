package com.example.thesearch.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment implements Serializable {

    private int id;
    private String author_id;
    private String text;
    private int spot_id;
    private Date created_at;

    public Comment(int id, String authorId, String text, int spotId, Date createdAt) {
        this.id = id;
        this.author_id = authorId;
        this.text = text;
        this.spot_id = spotId;
        this.created_at = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorId() {
        return author_id;
    }

    public void setAuthorId(String authorId) {
        this.author_id = authorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSpot_id() {
        return spot_id;
    }

    public void setSpot_id(int spot_id) {
        this.spot_id = spot_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(created_at);
    }
}
