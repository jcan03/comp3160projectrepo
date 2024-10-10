package com.example.comp3160project;

public class UserModel {
    // variables for chat model
    private String username;
    private String email;

    // required empty constructor for firebase
    public UserModel() {
    }

    // parameterized constructor
    public UserModel(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // getters and setters for each variable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
