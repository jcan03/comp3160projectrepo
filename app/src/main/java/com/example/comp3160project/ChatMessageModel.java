package com.example.comp3160project;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatMessageModel {

    // variables for chat model
    private String username;
    private String message;
    private long timestamp;
    private String email;

    // required empty constructor for Firebase
    public ChatMessageModel() {
    }

    // parameterized constructor
    public ChatMessageModel(String username, String message, long timestamp, String email) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    // method to convert the timestamp to a formatted date and time to display
    public String getFormattedTimestamp() {
        // convert the long timestamp to Date object
        Date date = new Date(timestamp);

        // format for the date view
        SimpleDateFormat formattedDate = new SimpleDateFormat("MMM dd, h:mm a", Locale.US);

        // set timezone to pacific time
        formattedDate.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        // return the formatted date
        return formattedDate.format(date);
    }
}
