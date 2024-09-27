package com.example.comp3160project;

public class ChatMessageModel {

    // variables for chat model
    private String username;
    private String message;
    private long timestamp;

    // required empty constructor for firebase
    public ChatMessageModel() {
    }

    // parameterized constructor
    public ChatMessageModel(String username, String message, long timestamp) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
    }

    // getters and setters for each variable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

