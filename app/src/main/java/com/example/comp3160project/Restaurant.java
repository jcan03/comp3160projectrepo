package com.example.comp3160project;


public class Restaurant {

    private String imageUrl;
    private String name;
    private String street;
    private int distance;
    private double rating;

    public Restaurant()
    {

    }


    public Restaurant(String imageUrl, String name, String street, int distance, double rating) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.street = street;
        this.distance = distance;
        this.rating = rating;
    }


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
            return distance/1000 + "km"; //todo: make it so it also includes a decimal point
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
