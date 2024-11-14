package com.example.comp3160project;


public class Restaurant {
    private String id;
    private String imageUrl;
    private String name;
    private String street;
    private double rating;

    // default constructor needed for firebase
    public Restaurant() {}

    // constructor with all fields
    public Restaurant(String id, String imageUrl, String name, String street, double rating) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.street = street;
        this.rating = rating;
    }

    // Getter and setter for ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Other getters and setters remain the same
    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public double getRating() {
        return rating;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}

