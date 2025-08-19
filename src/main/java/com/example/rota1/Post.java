package com.example.rota1;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String postId;
    private String topic;
    private String description;
    private String imageUrl;
    private int likes;
    private Map<String, Boolean> likedBy;  // New field to track users who liked the post

    public Post() {
        likedBy = new HashMap<>();  // Initialize the map
    }

    public Post(String postId, String topic, String description, String imageUrl) {
        this.postId = postId;
        this.topic = topic;
        this.description = description;
        this.imageUrl = imageUrl;
        this.likes = 0;
        this.likedBy = new HashMap<>();  // Initialize the map
    }

    public String getPostId() {
        return postId;
    }

    public String getTopic() {
        return topic;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Map<String, Boolean> likedBy) {
        this.likedBy = likedBy;
    }
}
