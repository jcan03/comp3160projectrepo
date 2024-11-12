package com.example.comp3160project;


public class Restaurant {
    private String id;
    private String imageUrl;
    private String name;
    private String street;
    private int distance;
    private double rating;

    // Default constructor for Firebase
    public Restaurant() {}

    // Constructor with all fields
    public Restaurant(String id, String imageUrl, String name, String street, int distance, double rating) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.street = street;
        this.distance = distance;
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

    public String getDistance() {
        if (distance < 1000)
            return distance + "m";
        else
            return distance/1000 + "km"; // TODO: Add decimal formatting if needed
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

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}

