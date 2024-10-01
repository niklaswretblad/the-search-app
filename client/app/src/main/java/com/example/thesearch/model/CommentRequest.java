
package com.example.thesearch.model;

public class CommentRequest {
    private String text;
    private String author_id;

    public CommentRequest(String text, String author_id) {
        this.text = text;
        this.author_id = author_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorId() {
        return author_id;
    }

    public void setAuthorId(String author_id) {
        this.author_id = author_id;
    }
}
